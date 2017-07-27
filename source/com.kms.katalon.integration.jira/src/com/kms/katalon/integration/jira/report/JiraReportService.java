package com.kms.katalon.integration.jira.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.integration.jira.JiraComponent;
import com.kms.katalon.integration.jira.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.constant.StringConstants;
import com.kms.katalon.integration.jira.entity.JiraAttachment;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.entity.JiraTestResult;
import com.kms.katalon.logging.LogUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class JiraReportService implements JiraComponent {
    public void uploadTestCaseLog(TestCaseLogRecord logRecord, JiraIssue issue)
            throws IOException, JiraIntegrationException {
        TestSuiteLogRecord testSuiteLogRecord = (TestSuiteLogRecord) logRecord.getParentLogRecord();

        String logFolder = testSuiteLogRecord.getLogFolder();

        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();

        List<JiraAttachment> jiraAttachments = new ArrayList<>();
        if (getSettingStore().isAttachScreenshotEnabled()) {
            for (String screenshot : logRecord.getAttachments()) {
                jiraAttachments.addAll(handler.uploadAttachment(getCredential(), issue,
                        PathUtil.relativeToAbsolutePath(screenshot, logFolder)));
            }
        }

        if (getSettingStore().isAttachLogEnabled()) {
            for (String logFile : testSuiteLogRecord.getLogFiles()) {
                jiraAttachments.addAll(handler.uploadAttachment(getCredential(), issue,
                        PathUtil.relativeToAbsolutePath(logFile, logFolder)));
            }
        }
    }

    public void uploadTestCaseReport(TestCaseLogRecord logRecord, JiraIssue issue, File zipFile)
            throws IOException, JiraIntegrationException {
        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();

        List<JiraAttachment> jiraAttachments = new ArrayList<>();
        jiraAttachments.addAll(handler.uploadAttachment(getCredential(), issue, zipFile.getAbsolutePath()));

        List<Long> jiraAttachmentIds = jiraAttachments.stream()
                .map(attachment -> attachment.getId())
                .collect(Collectors.toList());
        String testStatus = logRecord.getStatus().getStatusValue().name();
        JiraTestResult testResult = JiraTestResult.from(testStatus,
                ArrayUtils.toPrimitive(jiraAttachmentIds.toArray(new Long[jiraAttachmentIds.size()])));

        handler.sendKatalonIntegrationProperty(getCredential(), issue, testResult);
    }

    public File zipReportFolder(File folderToZip) throws ZipException {
        File zipTempFile = new File(getJiraZipTempFolder(), folderToZip.getName() + ".zip");
        if (zipTempFile.exists()) {
            FileUtils.deleteQuietly(zipTempFile);
        }
        ZipFile returnedZipFile = new ZipFile(zipTempFile);

        ZipParameters parameters = new ZipParameters();

        // set compression method to store compression
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

        // Set the compression level
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        // Add folder to the zip file

        returnedZipFile.addFolder(folderToZip.getAbsolutePath(), parameters);

        return zipTempFile;
    }

    private File getJiraZipTempFolder() {
        File zipTempFolder = new File(ProjectController.getInstance().getTempDir(),
                "zip/" + StringConstants.JIRA_BUNDLE_ID);
        if (!zipTempFolder.exists()) {
            zipTempFolder.mkdirs();
        }
        return zipTempFolder;
    }

    public void linkIssues(TestCaseLogRecord logRecord, JiraIssue inwardIssue)
            throws JiraIntegrationException, IOException {
        JiraIssue outwardIssue = null;
        try {
            outwardIssue = JiraObjectToEntityConverter
                    .getJiraIssue(TestCaseController.getInstance().getTestCaseByDisplayId(logRecord.getId()));
        } catch (Exception e) {
            LogUtil.logError(e);
        }

        if (outwardIssue == null) {
            return;
        }
        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();
        handler.linkJiraIssues(getCredential(), inwardIssue, outwardIssue);
    }
}
