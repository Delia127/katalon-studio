package com.kms.katalon.composer.report.provider;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.HyperLinkColumnLabelProvider;
import com.kms.katalon.composer.components.impl.providers.TableCellLayoutInfo;
import com.kms.katalon.composer.report.constants.ComposerReportMessageConstants;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.parts.ReportPart;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.setting.VideoRecorderSetting;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.execution.util.ExecutionUtil;

public class HyperlinkTestCaseVideoLabelProvider extends HyperLinkColumnLabelProvider<ILogRecord> {

    private ReportPart reportPart;

    private VideoRecorderSetting videoSetting;

    @SuppressWarnings("unchecked")
    public HyperlinkTestCaseVideoLabelProvider(ReportPart reportPart) {
        super(ReportPartTestCaseLabelProvider.CLMN_TEST_CASE_VIDEO);
        this.reportPart = reportPart;
        File executionSettingFile = ReportController.getInstance()
                .getExecutionSettingFile(reportPart.getReport().getLocation());
        if (executionSettingFile.exists()) {
            try {
                Map<String, Object> reportProperties = (Map<String, Object>) ExecutionUtil
                        .readRunConfigSettingFromFile(executionSettingFile.getAbsolutePath())
                        .getOrDefault("report", Collections.emptyMap());
                Map<String, Object> videoOptions = (Map<String, Object>) reportProperties
                        .getOrDefault(StringConstants.CONF_PROPERTY_VIDEO_RECORDER_OPTION, Collections.emptyMap());

                videoSetting = new VideoRecorderSetting();
                if (!videoOptions.isEmpty()) {
                    videoSetting = JsonUtil.fromJson(JsonUtil.toJson(videoOptions), VideoRecorderSetting.class);
                }
            } catch (IOException ignored) {}
        }
    }

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        File file = getVideoFile((ILogRecord) cell.getElement());
        if (file == null || !file.exists()) {
            return;
        }
        Program.launch(file.getAbsolutePath());
    }

    @Override
    protected Class<ILogRecord> getElementType() {
        return ILogRecord.class;
    }

    private int getOrder(ILogRecord[] testCaseLogRecords, ILogRecord testCaseLogRecord) {
        for (int index = 0; index < testCaseLogRecords.length; index++) {
            if (testCaseLogRecord.equals(testCaseLogRecords[index])) {
                return index;
            }
        }
        return -1;
    }

    @Override
    protected Image getImage(ILogRecord logRecord) {
        return getVideoFile(logRecord).exists() ? ImageConstants.IMG_16_VIDEO : null;
    }

    private File getVideoFile(ILogRecord logRecord) {
        ReportEntity report = reportPart.getReport();
        if (report == null) {
            return null;
        }

        int index = getOrder((ILogRecord[]) getViewer().getInput(), logRecord);
        return new File(report.getVideoFolder(),
                MessageFormat.format("test_{0}{1}", index + 1, videoSetting.getVideoFormat().getExtension()));
    }

    @Override
    protected String getText(ILogRecord element) {
        return StringUtils.EMPTY;
    }

    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new TableCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return 15;
            }
        };
    }

    @Override
    protected boolean isPlacedMouseHover(ViewerCell cell) {
        return cell != null && cell.getColumnIndex() == columnIndex && cell.getImage() != null;
    }

    @Override
    protected boolean shouldShowCursor(ViewerCell cell, Point currentMouseLocation) {
        return cell.getBounds().contains(currentMouseLocation);
    }

    @Override
    protected String getElementToolTipText(ILogRecord logRecord) {
        File videoFile = getVideoFile(logRecord);
        return (videoFile != null && videoFile.exists())
                ? ComposerReportMessageConstants.PROVIDER_TOOLTIP_OPEN_RECORDED_VIDEO : null;
    }
}
