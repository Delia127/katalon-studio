package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.dal.IReportDataProvider;
import com.kms.katalon.dal.ITestCaseDataProvider;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.dataprovider.setting.FileServiceDataProviderSetting;
import com.kms.katalon.dal.fileservice.manager.FolderFileServiceManager;
import com.kms.katalon.dal.fileservice.manager.ReportFileServiceManager;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class ReportFileServiceDataProvider implements IReportDataProvider {

    @Override
    public String getLogDirectory(TestCaseEntity testCase) throws Exception {
        ITestCaseDataProvider testCaseProvider = (new FileServiceDataProviderSetting()).getTestCaseDataProvider();
        File testCaseLogDir = new File(FileServiceConstant.TEMP_DIR + File.separator
                + testCaseProvider.getIdForDisplay(testCase));

        ReportFileServiceManager.ensureFolderExist(testCaseLogDir);
        return testCaseLogDir.getAbsolutePath();
    }

    @Override
    public String getLogDirectory(TestSuiteEntity testSuite) throws Exception {
        File testSuiteLogDir = new File(ReportFileServiceManager.getReportFolderOfTestSuite(testSuite.getProject(),
                testSuite));
        ReportFileServiceManager.ensureFolderExist(testSuiteLogDir);
        return testSuiteLogDir.getAbsolutePath();
    }

    @Override
    public ReportEntity getReportEntity(ProjectEntity project, TestSuiteEntity testSuite, String reportName)
            throws Exception {
        String testSuiteReportFolderPath = ReportFileServiceManager.getReportFolderOfTestSuite(project, testSuite);
        FolderEntity parentFolder = FolderFileServiceManager.getFolder(testSuiteReportFolderPath);
        ReportEntity report = ReportFileServiceManager.getReportEntity(testSuiteReportFolderPath + File.separator
                + reportName + (reportName.endsWith(".html") ? "" : ".html"));
        if (report == null) {
            report = ReportFileServiceManager.createReportEntity(reportName, parentFolder);
        }
        File reportFile = new File(report.getId());
        if (reportFile.exists()) {
            return report;
        } else {
            return null;
        }
    }

    @Override
    public List<ReportEntity> listReportEntities(TestSuiteEntity testSuite, ProjectEntity project) throws Exception {
        String testSuiteReportFolderPath = ReportFileServiceManager.getReportFolderOfTestSuite(project, testSuite);
        File testSuiteReportFolder = new File(testSuiteReportFolderPath);

        List<ReportEntity> lstReport = new ArrayList<ReportEntity>();
        if (testSuiteReportFolder.exists() && testSuiteReportFolder.isDirectory()) {
            for (File childReportFolder : testSuiteReportFolder.listFiles()) {
                if (!childReportFolder.exists() || !childReportFolder.isDirectory()) {
                    continue;
                }

                lstReport.add(ReportFileServiceManager.getReportEntity(childReportFolder.getAbsolutePath()));
            }
        }

        return lstReport;
    }

    @Override
    public String getEntityPk(ReportEntity report) {
        return report.getLocation();
    }

    @Override
    public void deleteReport(ReportEntity report) throws Exception {
        FileUtils.deleteDirectory(new File(report.getLocation()));
    }

    @Override
    public ReportEntity getReportEntity(String reportPk) throws Exception {
        return ReportFileServiceManager.getReportEntity(reportPk);
    }

    @Override
    public ReportEntity updateReport(ReportEntity report) throws Exception {
        EntityService.getInstance().saveFolderMetadataEntity(report);
        return report;
    }
}
