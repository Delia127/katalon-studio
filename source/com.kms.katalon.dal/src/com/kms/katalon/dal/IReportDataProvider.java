package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface IReportDataProvider {
    public String getTemporaryLogDirectory(FileEntity testCase) throws DALException;

    public String getLogDirectory(TestSuiteEntity testSuite) throws Exception;

    public ReportEntity getReportEntity(ProjectEntity project, TestSuiteEntity testSuite, String reportName)
            throws Exception;

    public String getEntityPk(ReportEntity report);

    public ReportEntity getReportEntity(String reportPk) throws Exception;

    public void deleteReport(ReportEntity report) throws Exception;

    public ReportEntity updateReport(ReportEntity report) throws Exception;

    public ReportEntity renameReport(ReportEntity report, String newName) throws DALException;

    public List<ReportEntity> listReportEntities(TestSuiteEntity testSuite, ProjectEntity project) throws Exception;

    public FolderEntity getReportFolder(TestSuiteEntity testSuite, ProjectEntity project) throws Exception;

    ReportCollectionEntity getReportCollectionEntity(String id) throws DALException;

    ReportCollectionEntity getReportCollectionEntity(ProjectEntity project, TestSuiteCollectionEntity entity,
            String reportName) throws DALException;

    void updateReportCollectionEntity(ReportCollectionEntity entity) throws DALException;
    
    public ReportCollectionEntity renameCollectionReport(ReportCollectionEntity collectionReport, String newName) throws DALException;

    ReportCollectionEntity newReportCollectionEntity(ProjectEntity project, TestSuiteCollectionEntity tsEntity,
            String newName) throws DALException;

    void deleteReportCollection(ReportCollectionEntity reportCollection) throws DALException;

    List<ReportCollectionEntity> listReportCollectionEntities(TestSuiteCollectionEntity testSuiteCollection, ProjectEntity project) throws Exception;
}
