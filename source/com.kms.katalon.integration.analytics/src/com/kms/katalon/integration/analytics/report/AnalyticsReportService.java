package com.kms.katalon.integration.analytics.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.integration.analytics.AnalyticsComponent;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
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
    
    public void upload(String folderPath) throws AnalyticsApiExeception {
        if (isIntegrationEnabled()) {
            LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_SEND_TEST_RESULT_START);
            try {
                String serverUrl = getSettingStore().getServerEndpoint(isEncryptionEnabled());
                String email = getSettingStore().getEmail(isEncryptionEnabled());
                String password = getSettingStore().getPassword(getSettingStore().isEncryptionEnabled());
                AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                if (token != null) {
                    perform(token.getAccess_token(), folderPath);
                } else {
                    LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_REQUEST_TOKEN_ERROR);
                }
            } catch (AnalyticsApiExeception | IOException | GeneralSecurityException e ) {
                LogUtil.logError(e, IntegrationAnalyticsMessages.MSG_SEND_ERROR);
                throw new AnalyticsApiExeception(e);
            }
            LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_SEND_TEST_RESULT_END);
        }
    }

    private void perform(String token, String path) throws AnalyticsApiExeception, IOException, GeneralSecurityException {
        LogUtil.printOutputLine("Uploading log files in folder path: " + path);
        String serverUrl = getSettingStore().getServerEndpoint(isEncryptionEnabled());
        Long projectId = getSettingStore().getProject().getId();
        List<Path> files = scanFiles(path);
        long timestamp = System.currentTimeMillis();
        boolean isEnd;
        String folderPath;
        Path filePath;
        for (int i = 0; i < files.size(); i++) {
            filePath = files.get(i);
            folderPath = getFolderPath(filePath);
            isEnd = i == (files.size() - 1);
            
            LogUtil.printOutputLine("Sending file: " + filePath.toAbsolutePath());
            if (AnalyticsStringConstants.ANALYTICS_STOREAGE.equalsIgnoreCase("s3")) {
                File file = filePath.toFile();
                AnalyticsUploadInfo uploadInfo = AnalyticsApiProvider.getUploadInfo(serverUrl, token, projectId);
                AnalyticsApiProvider.uploadFile(uploadInfo.getUploadUrl(), file);
                AnalyticsApiProvider.uploadFileInfo(serverUrl,
                        projectId, timestamp, folderPath, file.getName(), uploadInfo.getPath(), isEnd, token);
            } else {
                AnalyticsApiProvider.sendLog(serverUrl, projectId, timestamp, folderPath, filePath.toFile(), isEnd, token);
            }
        }
    }
    
    private List<Path> scanFiles(String path) {
        List<Path> files = new ArrayList<>();
        try {
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_REPORT_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, getSettingStore().isAttachScreenshot(), AnalyticsStringConstants.ANALYTICS_SCREENSHOT_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, getSettingStore().isAttachLog(), AnalyticsStringConstants.ANALYTICS_LOG_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, getSettingStore().isAttachCapturedVideos(), AnalyticsStringConstants.ANALYTICS_VIDEO_FILE_EXTENSION_PATTERN));
            addToList(files, scanFilesWithFilter(path, true, AnalyticsStringConstants.ANALYTICS_UUID_FILE_EXTENSION_PATTERN));
        } catch (IOException e) {
            LogUtil.logError(e, IntegrationAnalyticsMessages.MSG_SEND_ERROR);
        }
        return files;
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

}
