package com.kms.katalon.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class ReportController extends EntityController {
    private static EntityController _instance;

    private static String LOG_FILE_NAME = "execution0.log";
    private static String EXECUTION_SETTING_FILE_NAME = "execution.properties";
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

    public String getTestCaseLogFolder(TestCaseEntity testCase) throws Exception {
        return dataProviderSetting.getReportDataProvider().getLogDirectory(testCase);
    }

    /**
     * s
     * 
     * @param testCase
     * @return report folder's name
     * @throws Exception
     */
    public String generateReportFolder(TestCaseEntity testCase) throws Exception {
        String testCaseRootLogFolder = dataProviderSetting.getReportDataProvider().getLogDirectory(
                (TestCaseEntity) testCase);
        return generateReportFolderAndLogFile(testCaseRootLogFolder);
    }

    public String generateReportFolder(TestSuiteEntity testSuite) throws Exception {
        String testSuiteRootLogFolder = dataProviderSetting.getReportDataProvider().getLogDirectory(testSuite);
        return generateReportFolderAndLogFile(testSuiteRootLogFolder);
    }

    private String generateReportFolderAndLogFile(String reportRootFolderPath) throws Exception {
        // create report folder if it doesn't exist
        long current = Calendar.getInstance().getTimeInMillis();
        File reportFolderAtRuntime = new File(reportRootFolderPath, dateFormat.format(new Date()));
        while (reportFolderAtRuntime.exists() && (Calendar.getInstance().getTimeInMillis() - current) < 30 * 1000) {
            Thread.sleep(1000);
            reportFolderAtRuntime = new File(reportRootFolderPath, dateFormat.format(new Date()));
        }
        reportFolderAtRuntime.mkdir();

        // create log file
        (new File(reportFolderAtRuntime, LOG_FILE_NAME)).createNewFile();

        return reportFolderAtRuntime.getName();
    }

    public File getLogFile(TestCaseEntity testCase, String reportFolderName) throws Exception {
        String testCaseRootLogFolder = dataProviderSetting.getReportDataProvider().getLogDirectory(testCase);
        File testCaseReportFolderAtRuntime = new File(testCaseRootLogFolder, reportFolderName);

        return new File(testCaseReportFolderAtRuntime, LOG_FILE_NAME);
    }

    public File getLogFile(TestSuiteEntity testSuite, String reportFolderName) throws Exception {
        String testSuiteRootLogFolder = dataProviderSetting.getReportDataProvider().getLogDirectory(testSuite);
        File testSuiteReportFolderAtRuntime = new File(testSuiteRootLogFolder, reportFolderName);

        return new File(testSuiteReportFolderAtRuntime, LOG_FILE_NAME);
    }

    public File getExecutionSettingFile(TestCaseEntity testCase, String reportFolderName) throws Exception {
        String testCaseRootLogFolder = dataProviderSetting.getReportDataProvider().getLogDirectory(testCase);
        File testCaseReportFolderAtRuntime = new File(testCaseRootLogFolder, reportFolderName);

        return new File(testCaseReportFolderAtRuntime, EXECUTION_SETTING_FILE_NAME);
    }

    public File getExecutionSettingFile(TestSuiteEntity testSuite, String reportFolderName) throws Exception {
        String testSuiteRootLogFolder = dataProviderSetting.getReportDataProvider().getLogDirectory(testSuite);
        File testSuiteReportFolderAtRuntime = new File(testSuiteRootLogFolder, reportFolderName);

        return new File(testSuiteReportFolderAtRuntime, EXECUTION_SETTING_FILE_NAME);
    }

    // public boolean copyReportToProject(TestSuiteEntity testSuite, String reportFolder) throws Exception{
    // return dataProviderSetting.getReportDataProvider().copyReportToProject(testSuite, reportFolder);
    // }

    public Date getDateFromReportFolderName(String reportFolderName) throws ParseException {
        return dateFormat.parse(reportFolderName);
    }

    public ReportEntity getReportEntity(TestSuiteEntity testSuite, String reportName) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        return dataProviderSetting.getReportDataProvider().getReportEntity(project, testSuite, reportName);
    }

    public ReportEntity getLastRunReportEntity(TestSuiteEntity testSuite) throws Exception {
        String reportName = dateFormat.format(testSuite.getLastRun()).toString();
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        return dataProviderSetting.getReportDataProvider().getReportEntity(project, testSuite, reportName);
    }

    public void deleteReport(ReportEntity report) throws Exception {
        dataProviderSetting.getReportDataProvider().deleteReport(report);
    }

    public ReportEntity getReportEntity(String reportPk) throws Exception {
        return dataProviderSetting.getReportDataProvider().getReportEntity(reportPk);
    }

    public ReportEntity getReportEntityByDisplayId(String reportDisplayId, ProjectEntity projectEntity)
            throws Exception {
        String reportPk = projectEntity.getFolderLocation() + File.separator + reportDisplayId;
        return dataProviderSetting.getReportDataProvider().getReportEntity(reportPk);
    }

    public ReportEntity updateReport(ReportEntity report) throws Exception {
        return dataProviderSetting.getReportDataProvider().updateReport(report);
    }

    public TestSuiteEntity getTestSuiteByReport(ReportEntity report) throws Exception {
        if (report == null || report.getParentFolder() == null) {
            return null;
        }
        
        String testSuiteDisplayId = FolderController.getInstance().getIdForDisplay(report.getParentFolder())
                .replaceFirst("Reports/", "Test Suites/");
        return TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteDisplayId, report.getProject());
    }

    public List<ReportEntity> listReportEntities(TestSuiteEntity testSuiteEntity, ProjectEntity projectEntity)
            throws Exception {
        return dataProviderSetting.getReportDataProvider().listReportEntities(testSuiteEntity, projectEntity);
    }
}
