package com.kms.katalon.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.Entity;
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

    public String generateReportFolder(TestSuiteEntity testSuite) throws Exception {
        String testSuiteRootLogFolder = getDataProviderSetting().getReportDataProvider().getLogDirectory(testSuite);
        return generateReportFolder(testSuiteRootLogFolder);
    }

    public String generateTemporaryExecutionFolder(FileEntity testCase) throws DALException, InterruptedException {
        String testCaseRootLogFolder = getDataProviderSetting().getReportDataProvider()
                .getTemporaryLogDirectory(testCase);
        return generateReportFolder(testCaseRootLogFolder);
    }

    private synchronized String generateReportFolder(String reportRootFolderPath) throws InterruptedException {
        // create report folder if it doesn't exist
        File reportFolderAtRuntime = new File(reportRootFolderPath, dateFormat.format(new Date()));
       
        while (reportFolderAtRuntime.exists()) {
            Thread.sleep(1000);
            reportFolderAtRuntime = new File(reportRootFolderPath, dateFormat.format(new Date()));
        }

        reportFolderAtRuntime.mkdir();

        return reportFolderAtRuntime.getAbsolutePath();
    }

    public File getLogFile(TestCaseEntity testCase, String reportFolderName) throws Exception {
        String testCaseRootLogFolder = getDataProviderSetting().getReportDataProvider()
                .getTemporaryLogDirectory(testCase);
        File testCaseReportFolderAtRuntime = new File(testCaseRootLogFolder, reportFolderName);

        return new File(testCaseReportFolderAtRuntime, LOG_FILE_NAME);
    }

    public File getLogFile(TestSuiteEntity testSuite, String reportFolderName) throws Exception {
        String testSuiteRootLogFolder = getDataProviderSetting().getReportDataProvider().getLogDirectory(testSuite);
        File testSuiteReportFolderAtRuntime = new File(testSuiteRootLogFolder, reportFolderName);

        return new File(testSuiteReportFolderAtRuntime, LOG_FILE_NAME);
    }

    public File getExecutionSettingFile(TestCaseEntity testCase, String reportFolderName) throws Exception {
        String testCaseRootLogFolder = getDataProviderSetting().getReportDataProvider()
                .getTemporaryLogDirectory(testCase);
        File testCaseReportFolderAtRuntime = new File(testCaseRootLogFolder, reportFolderName);

        return new File(testCaseReportFolderAtRuntime, EXECUTION_SETTING_FILE_NAME);
    }

    public File getExecutionSettingFile(TestSuiteEntity testSuite, String reportFolderName) throws Exception {
        String testSuiteRootLogFolder = getDataProviderSetting().getReportDataProvider().getLogDirectory(testSuite);
        File testSuiteReportFolderAtRuntime = new File(testSuiteRootLogFolder, reportFolderName);

        return new File(testSuiteReportFolderAtRuntime, EXECUTION_SETTING_FILE_NAME);
    }

    public File getExecutionSettingFile(String logFolderPath) {
        return new File(logFolderPath, EXECUTION_SETTING_FILE_NAME);
    }

    public Date getDateFromReportFolderName(String reportFolderName) throws ParseException {
        return dateFormat.parse(reportFolderName);
    }

    public ReportEntity getReportEntity(TestSuiteEntity testSuite, String reportName) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        return getDataProviderSetting().getReportDataProvider().getReportEntity(project, testSuite, reportName);
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

    public FolderEntity getReportFolder(TestSuiteEntity testSuiteEntity, ProjectEntity projectEntity) throws Exception {
        return getDataProviderSetting().getReportDataProvider().getReportFolder(testSuiteEntity, projectEntity);
    }

    public String getTestSuiteFolderId(String reportFolderId) {
        if (reportFolderId == null) {
            return null;
        }
        return reportFolderId.replaceFirst("Reports", "Test Suites");
    }

    public TestSuiteEntity getTestSuiteByReportParentFolder(FolderEntity parentReportFolder) throws Exception {
        String testSuiteDisplayId = parentReportFolder.getIdForDisplay().replaceFirst("Reports", "Test Suites");
        return TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteDisplayId,
                parentReportFolder.getProject());
    }

    public ReportEntity reloadReport(ReportEntity report, Entity entity) throws Exception {
        return getReportEntity(entity.getId());
    }

    public ReportCollectionEntity newReportCollection(ProjectEntity projectEntity, TestSuiteCollectionEntity entity,
            String newName) throws DALException {
        return getDataProviderSetting().getReportDataProvider().newReportCollectionEntity(projectEntity, entity,
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
