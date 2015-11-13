package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface IReportDataProvider {
    public String getLogDirectory(TestCaseEntity testCase) throws Exception;

    public String getLogDirectory(TestSuiteEntity testSuite) throws Exception;

    public ReportEntity getReportEntity(ProjectEntity project, TestSuiteEntity testSuite, String reportName)
            throws Exception;

    public String getEntityPk(ReportEntity report);

    public ReportEntity getReportEntity(String reportPk) throws Exception;

    public void deleteReport(ReportEntity report) throws Exception;

    public ReportEntity updateReport(ReportEntity report) throws Exception;

    public List<ReportEntity> listReportEntities(TestSuiteEntity testSuite, ProjectEntity project) throws Exception;
    
    public FolderEntity getReportFolder(TestSuiteEntity testSuite, ProjectEntity project) throws Exception;
}
