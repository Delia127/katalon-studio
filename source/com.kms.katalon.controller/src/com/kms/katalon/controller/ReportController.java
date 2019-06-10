package com.kms.katalon.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class ReportController extends EntityController {
    private static EntityController _instance;

    public static String LOG_FILE_NAME = "execution0.log";

    public static String EXECUTION_SETTING_FILE_NAME = "execution.properties";

    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    // synchronizedSet - just in case
    private static Set<String> REPORT_SUB_FOLDER_NAMES = Collections.synchronizedSet(new HashSet<>());

    private ReportController() {
        super();
    }

    public static ReportController getInstance() {
        if (_instance == null) {
            _instance = new ReportController();
        }
        return (ReportController) _instance;
    }

    public String getTestCaseLogFolder(TestCaseEntity testCase) throws DALException {
        return getDataProviderSetting().getReportDataProvider().getTemporaryLogDirectory(testCase);
    }

    public String generateReportFolder(TestSuiteEntity testSuite, String executionSessionId) throws Exception {
        String testSuiteRootLogFolder = getDataProviderSetting().getReportDataProvider().getLogDirectory(testSuite, executionSessionId);
        return generateReportFolder(testSuiteRootLogFolder);
    }

    public String generateTemporaryExecutionFolder(FileEntity testCase) throws DALException, InterruptedException {
        String testCaseRootLogFolder = getDataProviderSetting().getReportDataProvider()
                .getTemporaryLogDirectory(testCase);
        return generateReportFolder(testCaseRootLogFolder);
    }

    private String generateReportFolder(String reportRootFolderPath) throws InterruptedException {
        // create report folder if it doesn't exist
        long current = Calendar.getInstance().getTimeInMillis();
        File reportFolderAtRuntime = new File(reportRootFolderPath, getReportSubFolderName());
        while (reportFolderAtRuntime.exists() && (Calendar.getInstance().getTimeInMillis() - current) < 30 * 1000) {
            Thread.sleep(1000);
            reportFolderAtRuntime = new File(reportRootFolderPath, getReportSubFolderName());
        }
        reportFolderAtRuntime.mkdir();

        return reportFolderAtRuntime.getAbsolutePath();
    }

    private synchronized String getReportSubFolderName() throws InterruptedException {
        long current = Calendar.getInstance().getTimeInMillis();
        String name = dateFormat.format(new Date());
        while (REPORT_SUB_FOLDER_NAMES.contains(name) && (Calendar.getInstance().getTimeInMillis() - current) < 30 * 1000) {
            Thread.sleep(1000);
            name = dateFormat.format(new Date());
        }
        REPORT_SUB_FOLDER_NAMES.add(name);
        return name;
    }

    public File getLogFile(TestCaseEntity testCase, String reportFolderName) throws Exception {
        String testCaseRootLogFolder = getDataProviderSetting().getReportDataProvider()
                .getTemporaryLogDirectory(testCase);
        File testCaseReportFolderAtRuntime = new File(testCaseRootLogFolder, reportFolderName);

        return new File(testCaseReportFolderAtRuntime, LOG_FILE_NAME);
    }

    public File getLogFile(TestSuiteEntity testSuite, String reportFolderName, String executionSessionId) throws Exception {
        String testSuiteRootLogFolder = getDataProviderSetting().getReportDataProvider().getLogDirectory(testSuite, executionSessionId);
        File testSuiteReportFolderAtRuntime = new File(testSuiteRootLogFolder, reportFolderName);

        return new File(testSuiteReportFolderAtRuntime, LOG_FILE_NAME);
    }

    public File getExecutionSettingFile(TestCaseEntity testCase, String reportFolderName) throws Exception {
        String testCaseRootLogFolder = getDataProviderSetting().getReportDataProvider()
                .getTemporaryLogDirectory(testCase);
        File testCaseReportFolderAtRuntime = new File(testCaseRootLogFolder, reportFolderName);

        return new File(testCaseReportFolderAtRuntime, EXECUTION_SETTING_FILE_NAME);
    }

    public File getExecutionSettingFile(TestSuiteEntity testSuite, String reportFolderName, String executionSessionId) throws Exception {
        String testSuiteRootLogFolder = getDataProviderSetting().getReportDataProvider().getLogDirectory(testSuite, executionSessionId);
        File testSuiteReportFolderAtRuntime = new File(testSuiteRootLogFolder, reportFolderName);

        return new File(testSuiteReportFolderAtRuntime, EXECUTION_SETTING_FILE_NAME);
    }

    public File getExecutionSettingFile(String logFolderPath) {
        return new File(logFolderPath, EXECUTION_SETTING_FILE_NAME);
    }

    public Date getDateFromReportFolderName(String reportFolderName) throws ParseException {
        return dateFormat.parse(reportFolderName);
    }

    public ReportEntity getReportEntity(TestSuiteEntity testSuite, String reportName, String executionSessionId) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        return getDataProviderSetting().getReportDataProvider().getReportEntity(project, testSuite, reportName, executionSessionId);
    }

    public ReportEntity getLastRunReportEntity(TestSuiteEntity testSuite) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        ReportEntity lastRunReport = null;
        List<ReportEntity> reports = listReportEntities(testSuite, project);
        if (reports.size() > 0) {
            lastRunReport = reports.get(0);
            Date lastRunDate = parseReportDateFromName(lastRunReport.getName());
            for (ReportEntity report : reports) {
                String reportName = report.getName();
                Date reportDate = parseReportDateFromName(reportName);
                if (reportDate.after(lastRunDate)) {
                    lastRunDate = reportDate;
                    lastRunReport = report;
                }
            }
        }
        return lastRunReport;
    }
    
    public ReportCollectionEntity getLastRunReportCollectionEntity(TestSuiteCollectionEntity testSuiteCollection) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        ReportCollectionEntity lastRunReport = null;
        List<ReportCollectionEntity> reports = listReportCollectionEntities(testSuiteCollection, project);
        if (reports.size() > 0) {
            lastRunReport = reports.get(0);
            Date lastRunDate = lastRunReport.getDateCreated();
            for (ReportCollectionEntity report : reports) {
                Date reportDate = report.getDateCreated();
                if (reportDate.after(lastRunDate)) {
                    lastRunDate = reportDate;
                    lastRunReport = report;
                }
            }
        }
        return lastRunReport;
    }

    private Date parseReportDateFromName(String reportName) throws ParseException {
        return dateFormat.parse(reportName);
    }

    public Date getReportDate(ReportEntity report) throws Exception {
        return parseReportDateFromName(report.getName());
    }

    public void deleteReport(ReportEntity report) throws Exception {
        getDataProviderSetting().getReportDataProvider().deleteReport(report);
    }

    public ReportEntity getReportEntity(String reportPk) throws Exception {
        return getDataProviderSetting().getReportDataProvider().getReportEntity(reportPk);
    }

    public ReportEntity getReportEntityByDisplayId(String reportDisplayId, ProjectEntity projectEntity)
            throws ControllerException {
        try {
            String reportPk = projectEntity.getFolderLocation() + File.separator + reportDisplayId;
            return getDataProviderSetting().getReportDataProvider().getReportEntity(reportPk);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    public ReportEntity updateReport(ReportEntity report) throws ControllerException {
        try {
            return getDataProviderSetting().getReportDataProvider().updateReport(report);
        } catch (Exception e) {
            throw new ControllerException(e.getMessage());
        }
    }

    public TestSuiteEntity getTestSuiteByReport(ReportEntity report) throws Exception {
        if (report == null || report.getParentFolder() == null) {
            return null;
        }

        return getTestSuiteByReportParentFolder(report.getParentFolder());
    }

    public List<ReportEntity> listReportEntities(TestSuiteEntity testSuiteEntity, ProjectEntity projectEntity)
            throws Exception {
        return getDataProviderSetting().getReportDataProvider().listReportEntities(testSuiteEntity, projectEntity);
    }
    
    public List<ReportCollectionEntity> listReportCollectionEntities(TestSuiteCollectionEntity testSuiteCollectionEntity, ProjectEntity projectEntity) throws Exception {
        return getDataProviderSetting().getReportDataProvider().listReportCollectionEntities(testSuiteCollectionEntity, projectEntity);
    }

    public FolderEntity getReportFolder(TestSuiteEntity testSuiteEntity, ProjectEntity projectEntity, String executionSessionId) throws Exception {
        return getDataProviderSetting().getReportDataProvider().getReportFolder(testSuiteEntity, projectEntity, executionSessionId);
    }

    public String getTestSuiteFolderId(String reportFolderId) {
        if (reportFolderId == null) {
            return null;
        }
        String[] groupName = reportFolderId.split(StringConstants.ENTITY_ID_SEPARATOR);
        return "Test Suites" + StringConstants.ENTITY_ID_SEPARATOR + groupName[groupName.length - 1];
    }

    public TestSuiteEntity getTestSuiteByReportParentFolder(FolderEntity parentReportFolder) throws Exception {
        String testSuiteDisplayId = "Test Suites" + StringConstants.ENTITY_ID_SEPARATOR + parentReportFolder.getName();
        return TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteDisplayId,
                parentReportFolder.getProject());
    }

    public ReportEntity reloadReport(ReportEntity report, Entity entity) throws Exception {
        return getReportEntity(entity.getId());
    }

    public ReportCollectionEntity newReportCollection(ProjectEntity projectEntity, TestSuiteCollectionEntity entity,
            String executionSessionId, String newName) throws DALException {
        return getDataProviderSetting().getReportDataProvider().newReportCollectionEntity(projectEntity, entity, executionSessionId,
                newName);
    }

    public void updateReportCollection(ReportCollectionEntity reportCollection) throws DALException {
        getDataProviderSetting().getReportDataProvider().updateReportCollectionEntity(reportCollection);
    }

    public void deleteReportCollection(ReportCollectionEntity reportCollection) throws DALException {
        getDataProviderSetting().getReportDataProvider().deleteReportCollection(reportCollection);
    }

    public ReportCollectionEntity getReportCollection(String id) throws DALException {
        return getDataProviderSetting().getReportDataProvider().getReportCollectionEntity(id);
    }

    public ReportEntity renameReport(ReportEntity report, String newName) throws DALException {
        return getDataProviderSetting().getReportDataProvider().renameReport(report, newName);
    }

    public ReportCollectionEntity renameReportCollection(ReportCollectionEntity collectionReport, String newName)
            throws DALException {
        return getDataProviderSetting().getReportDataProvider().renameCollectionReport(collectionReport, newName);
    }
}
