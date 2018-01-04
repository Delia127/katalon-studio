package com.kms.katalon.execution.video;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.helper.screenrecorder.VideoRecorder;
import com.kms.katalon.core.helper.screenrecorder.VideoRecorderBuilder;
import com.kms.katalon.core.helper.screenrecorder.VideoRecorderException;
import com.kms.katalon.core.helper.screenrecorder.VideoSubtitleWriter;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.report.ReportTestCaseEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.execution.logging.LogEvaluator;
import com.kms.katalon.execution.setting.VideoRecorderSetting;
import com.kms.katalon.logging.LogUtil;

public class VideoRecorderService implements LauncherListener, LogEvaluator {
    private int logDepth = 0;

    private int testCaseIndex = 0;

    private int mainStepIndex = 0;

    private VideoRecorder videoRecorder;

    private VideoRecorderSetting videoSetting;

    private LogLevel currentTestCaseResult = LogLevel.NOT_RUN;

    private IRunConfiguration runConfig;

    private VideoSubtitleWriter videoSubtitleWriter;

    private long videoStartTime;

    private long actionStartTime;

    private String currentActionDescription = StringUtils.EMPTY;

    private ReportEntity report;

    public VideoRecorderService(IRunConfiguration runConfig, ReportEntity report) {
        this.runConfig = runConfig;
        this.report = report;
        getVideoSetting(runConfig);
    }

    private void getVideoSetting(IRunConfiguration runConfig) {
        @SuppressWarnings("unchecked")
        Map<String, Object> reportSettings = (Map<String, Object>) runConfig.getExecutionSetting()
                .getGeneralProperties()
                .get(StringConstants.CONF_PROPERTY_REPORT);
        videoSetting = (VideoRecorderSetting) reportSettings.get(StringConstants.CONF_PROPERTY_VIDEO_RECORDER_OPTION);
    }

    @Override
    public void handleLauncherEvent(LauncherEvent event, LauncherNotifiedObject notifiedObject) {
        if (!isRecordingAllowed()) {
            return;
        }

        switch (event) {
            case UPDATE_RECORD:
                handleLogUpdated(notifiedObject);
                return;
            case UPDATE_STATUS:
                handleLauncherStatus(notifiedObject);
                return;
            default:
                return;
        }
    }

    private boolean isRecordingAllowed() {
        return videoSetting.isEnable()
                && (videoSetting.isAllowedRecordIfFailed() || videoSetting.isAllowedRecordIfPassed());
    }

    private void handleLauncherStatus(LauncherNotifiedObject notifiedObject) {
        LauncherStatus status = (LauncherStatus) notifiedObject.getObject();
        if (status == LauncherStatus.TERMINATED) {
            stopVideoRecording();
            deleteVideo();
        }
    }

    private void handleLogUpdated(LauncherNotifiedObject notifiedObject) {
        XmlLogRecord logRecord = (XmlLogRecord) notifiedObject.getObject();

        LogLevel logLevel = LogLevel.valueOf(logRecord.getLevel().getName());
        if (logLevel == null) {
            return;
        }

        switch (logLevel) {
            case START:
                logDepth++;
                if (isStartTestCaseLog(logRecord) && isLogUnderTestCaseMainLevel(runConfig, logDepth)) {
                    initVideoRecorder(logRecord);
                    startVideoRecording();
                    testCaseIndex++;
                    mainStepIndex = 0;
                }

                if (isStartStep(logRecord) && isLogUnderMainTestStepLevel(runConfig, logDepth)) {
                    writeSub();
                    mainStepIndex++;

                    actionStartTime = System.currentTimeMillis();
                    String description = (String) logRecord.getProperties()
                            .get(StringConstants.XML_LOG_DESCRIPTION_PROPERTY);
                    currentActionDescription = StringUtils.isNotEmpty(description) ? description
                            : getStepMessage(logRecord);
                }

                break;
            case END:
                if (isEndTestCaseLog(logRecord) && isLogUnderTestCaseMainLevel(runConfig, logDepth)) {
                    stopVideoRecording();
                    switch (currentTestCaseResult) {
                        case FAILED:
                            if (!videoSetting.isAllowedRecordIfFailed()) {
                                deleteVideo();
                            }
                            break;
                        case PASSED:
                            if (!videoSetting.isAllowedRecordIfPassed()) {
                                deleteVideo();
                            }
                            break;
                        default:
                            deleteVideo();
                    }
                    currentTestCaseResult = LogLevel.NOT_RUN;
                    saveReport();
                }
                logDepth--;
                break;
            default:
                if (LogLevel.getResultLogs().contains(logLevel) && isLogUnderTestCaseMainLevel(runConfig, logDepth)) {
                    currentTestCaseResult = logLevel;
                }
                break;
        }
    }

    private void saveReport() {
        try {
            ReportController.getInstance().updateReport(report);
        } catch (Exception e) {
            LogUtil.printAndLogError(e);
        }
    }

    private void deleteVideo() {
        if (videoRecorder != null) {
            videoRecorder.delete();
        }
        if (videoSubtitleWriter != null) {
            videoSubtitleWriter.delete();
        }

        if (testCaseIndex > 0) {
            getCurrentReportItem(testCaseIndex - 1).setVideoLocation(StringUtils.EMPTY);
        }
    }

    private void writeSub() {
        try {
            if (StringUtils.isEmpty(currentActionDescription) || videoSubtitleWriter == null) {
                return;
            }
            videoSubtitleWriter.writeSub(actionStartTime - videoStartTime, System.currentTimeMillis() - videoStartTime,
                    String.format("%d. %s", mainStepIndex, currentActionDescription));
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    protected void initVideoRecorder(XmlLogRecord logRecord) {
        try {
            String videoName = String.format("test_%d", testCaseIndex + 1);
            String reportFolder = runConfig.getExecutionSetting().getFolderPath();
            String videoFolderName = new File(reportFolder,
                    ReportEntity.VIDEO_RECORDED_FOLDER).getAbsolutePath();
            videoRecorder = VideoRecorderBuilder.get()
                    .setVideoConfig(videoSetting.toVideoConfiguration())
                    .setOutputDirLocation(videoFolderName)
                    .setOutputVideoName(videoName)
                    .create();

            getCurrentReportItem(testCaseIndex).setVideoLocation(
                    PathUtil.absoluteToRelativePath(videoRecorder.getCurrentVideoLocation(), reportFolder));

            videoSubtitleWriter = new VideoSubtitleWriter(new File(videoFolderName, videoName).getAbsolutePath());
        } catch (VideoRecorderException e) {
            LogUtil.printAndLogError(e);
        }
    }

    private ReportTestCaseEntity getCurrentReportItem(int testCaseIndex) {
        return report.getReportTestCases().get(testCaseIndex);
    }

    protected void startVideoRecording() {
        if (videoRecorder == null) {
            return;
        }
        try {
            videoStartTime = actionStartTime = System.currentTimeMillis();
            videoRecorder.start();
        } catch (VideoRecorderException e) {
            LogUtil.printAndLogError(e);
        }
    }

    protected void stopVideoRecording() {
        if (videoRecorder == null || !videoRecorder.isStarted()) {
            return;
        }
        try {
            writeSub();
            videoRecorder.stop();
        } catch (VideoRecorderException e) {
            LogUtil.printAndLogError(e);
        }
    }
}
