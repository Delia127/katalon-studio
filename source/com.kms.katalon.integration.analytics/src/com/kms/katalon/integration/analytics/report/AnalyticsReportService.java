package com.kms.katalon.integration.analytics.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.ZipUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.entity.ReportFolder;
import com.kms.katalon.execution.util.ApiKey;
import com.kms.katalon.integration.analytics.AnalyticsComponent;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.entity.AnalyticsExecution;
import com.kms.katalon.integration.analytics.entity.AnalyticsFileInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestRun;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsUploadInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.util.FileUtils;
import com.kms.katalon.logging.LogUtil;

public class AnalyticsReportService implements AnalyticsComponent {
    
    public boolean isIntegrationEnabled() {
        boolean isIntegrationEnabled = false;
        try {
            isIntegrationEnabled = getSettingStore().isIntegrationEnabled();
        } catch (IOException ex) {
            // do nothing
        }
        return isIntegrationEnabled;
    }
    
    private boolean isEncryptionEnabled() {
        boolean isEncryptionEnabled = false;
        try {
            isEncryptionEnabled = getSettingStore().isEncryptionEnabled();
        } catch (IOException ex) {
            // do nothing
        }
        return isEncryptionEnabled;
    }
    
    public void upload(ReportFolder reportFolder) throws AnalyticsApiExeception {
        if (isIntegrationEnabled()) {
            LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_SEND_TEST_RESULT_START);
            try {
                AnalyticsTokenInfo token = getKAToken();
                if (token != null) {
                    perform(token.getAccess_token(), reportFolder);
                } else {
                    LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_REQUEST_TOKEN_ERROR);
                }
            } catch (Exception e ) {
                LogUtil.logError(e, IntegrationAnalyticsMessages.MSG_SEND_ERROR);
                throw new AnalyticsApiExeception(e);
            }
            LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_SEND_TEST_RESULT_END);
        } else {
            LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_INTEGRATE_WITH_KA);
        }
    }

    private AnalyticsTokenInfo getKAToken() throws IOException, GeneralSecurityException, AnalyticsApiExeception {
        String serverUrl = getSettingStore().getServerEndpoint(isEncryptionEnabled());

        RunningMode runMode = ApplicationRunningMode.get();
        if (runMode == RunningMode.CONSOLE) {
            String apiKey = ApiKey.get();
            if (apiKey != null && !apiKey.isEmpty()) {
                return AnalyticsApiProvider.requestToken(serverUrl, "", apiKey);
            } else {
                LogUtil.printErrorLine(IntegrationAnalyticsMessages.VIEW_ERROR_MSG_SPECIFY_KATALON_API_KEY);
                return null;
            }
        }

        String email = getSettingStore().getEmail(isEncryptionEnabled());
        String password = getSettingStore().getPassword(getSettingStore().isEncryptionEnabled());
        return AnalyticsApiProvider.requestToken(serverUrl, email, password);
    }

    private void perform(String token, ReportFolder reportFolder) throws Exception {
    	if (reportFolder.isRunTestSuite()) {
            LogUtil.printOutputLine("Uploading log files of test suite");
    	} else {
            LogUtil.printOutputLine("Uploading log files of test suite collection");
    	}
        String serverUrl = getSettingStore().getServerEndpoint(isEncryptionEnabled());
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        Long projectId = getSettingStore().getProject().getId();
        List<Path> files = scanFiles(reportFolder);
        long timestamp = System.currentTimeMillis();
        Path reportLocation = Paths.get(FolderController.getInstance().getReportRoot(project).getLocation());
        
        List<AnalyticsExecution> executions = null;
        if (AnalyticsStringConstants.ANALYTICS_STOREAGE.equalsIgnoreCase("s3")) {
            List<AnalyticsUploadInfo> uploadInfoList = AnalyticsApiProvider.getMultipleUploadInfo(serverUrl, token,
                    projectId, files.size());
            List<AnalyticsFileInfo> fileInfoList = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                Path filePath = files.get(i);
                String folderPath = reportLocation.relativize(filePath.getParent()).toString();
                boolean isEnd = i == (files.size() - 1);
                File file = filePath.toFile();
                LogUtil.printOutputLine("Sending file: " + filePath.toAbsolutePath());
                AnalyticsUploadInfo uploadInfo = uploadInfoList.get(i);
                AnalyticsApiProvider.uploadFile(uploadInfo.getUploadUrl(), file);
                AnalyticsFileInfo fileInfo = new AnalyticsFileInfo();
                fileInfo.setFolderPath(folderPath);
                fileInfo.setFileName(file.getName());
                fileInfo.setUploadedPath(uploadInfo.getPath());
                fileInfo.setEnd(isEnd);
                fileInfoList.add(fileInfo);
            }
            executions = AnalyticsApiProvider.uploadMultipleFileInfo(serverUrl, projectId, timestamp, fileInfoList,
                    token);
        } else {
            for (int i = 0; i < files.size(); i++) {
                Path filePath = files.get(i);
                String folderPath = reportLocation.relativize(filePath.getParent()).toString();
                boolean isEnd = i == (files.size() - 1);

                LogUtil.printOutputLine("Sending file: " + filePath.toAbsolutePath());
                executions = AnalyticsApiProvider.sendLog(serverUrl, projectId, timestamp, folderPath,
                        filePath.toFile(), isEnd, token);
            }
        }
        if (executions != null && !executions.isEmpty()) {
            executions.stream()
            .filter(execution -> execution != null)
            .forEach(execution -> LogUtil.printOutputLine(
                    MessageFormat.format(IntegrationAnalyticsMessages.MSG_EXECUTION_URL, execution.getWebUrl())));
        }
    }
    
    private List<Path> scanFiles(ReportFolder reportFolder) {
        List<Path> files = new ArrayList<>();
        for (String path : reportFolder.getReportFolders()) {
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_UUID_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_REPORT_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_SCREENSHOT_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_LOG_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_VIDEO_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_RP_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_BASIC_REPORT_FILE_EXTENSION_PATTERN));
            addToList(files, scanHarFiles(path));
        }
        return files;
    }
    
    private List<Path> scanHarFiles(String path) {
        List<Path> harFiles = scanFilesWithFilter(path, true,  AnalyticsStringConstants.ANALYTICS_HAR_FILE_EXTENSION_PATTERN);
        if (harFiles == null || harFiles.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            Path zipFile = FileUtils.createTemporaryFile(StringUtils.appendIfMissing(path, File.separator) + "katalon-analytics-tmp", "hars-", ".zip");
            Path harsZipFile = ZipUtil.compress(harFiles, zipFile);
            return Arrays.asList(harsZipFile);
        } catch (IOException e) {
            LogUtil.logError(e, "Could not compress har files");
            return harFiles;
        }
    }
    
    private void addToList(List<Path> files, List<Path> other) {
        if (!other.isEmpty()) {
            files.addAll(other);
        }
    }
    
    private List<Path> scanFilesWithFilter(String path, boolean isScan, String pattern) {
        if (isScan) {
            return FileUtils.scanFiles(path, pattern);
        }
        return new ArrayList<>();
    }
    
    private String getFolderPath(Path filePath) {
        String folderPath;
        try {
            folderPath = filePath.getParent().getParent().toFile().getName()
                    + File.separator
                    + filePath.getParent().toFile().getName();
        } catch (Exception ex) {
            folderPath = filePath.getParent().toFile().getName();
        }
        return folderPath;
    }
    
    public void updateExecutionProccess(AnalyticsTestRun testRun) throws AnalyticsApiExeception {
    	try {
            AnalyticsTokenInfo token = getKAToken();
            if (token != null) {
            	String serverUrl = getSettingStore().getServerEndpoint(isEncryptionEnabled());
            	long projectId = getSettingStore().getProject().getId();
            	AnalyticsApiProvider.updateTestRunResult(serverUrl, projectId, token.getAccess_token(), testRun);
            } else {
                LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_REQUEST_TOKEN_ERROR);
            }
        } catch (AnalyticsApiExeception | IOException | GeneralSecurityException e ) {
            LogUtil.logError(e, IntegrationAnalyticsMessages.MSG_SEND_ERROR);
            throw new AnalyticsApiExeception(e);
        }
    }

}
