package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.dal.IReportDataProvider;
import com.kms.katalon.dal.ITestSuiteDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.dataprovider.setting.FileServiceDataProviderSetting;
import com.kms.katalon.dal.fileservice.manager.FolderFileServiceManager;
import com.kms.katalon.dal.fileservice.manager.ReportFileServiceManager;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class ReportFileServiceDataProvider implements IReportDataProvider {
    
    @Override
    public String getTemporaryLogDirectory(FileEntity systemFileEntity) throws DALException {
        File systemFileLogDir = new File(FileServiceConstant.TEMP_DIR, systemFileEntity.getIdForDisplay());
        try {
            ReportFileServiceManager.ensureFolderExist(systemFileLogDir);
            return systemFileLogDir.getAbsolutePath();
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public String getLogDirectory(TestSuiteEntity testSuite, String executionSessionId) throws Exception {
        File testSuiteLogDir = new File(
                ReportFileServiceManager.getReportFolderOfTestSuite(testSuite.getProject(), testSuite, executionSessionId));
        ReportFileServiceManager.ensureFolderExist(testSuiteLogDir);
        return testSuiteLogDir.getAbsolutePath();
    }

    @Override
    public ReportEntity getReportEntity(ProjectEntity project, TestSuiteEntity testSuite, String reportName, String executionSessionId)
            throws Exception {
        String testSuiteReportFolderPath = ReportFileServiceManager.getReportFolderOfTestSuite(project, testSuite, executionSessionId);
        FolderEntity parentFolder = FolderFileServiceManager.getFolder(testSuiteReportFolderPath);
        ReportEntity report = ReportFileServiceManager
                .getReportEntity(testSuiteReportFolderPath + File.separator + reportName);
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
        List<ReportEntity> lstReport = new ArrayList<ReportEntity>();
        String reportRootFolderPath = FileServiceConstant.getReportRootFolderLocation(project.getFolderLocation());
        File reportRootFolder = new File(reportRootFolderPath);
        for (File reportFolderByExecutionId : reportRootFolder.listFiles()) {
            if (reportFolderByExecutionId.isDirectory()) {
                String testSuiteReportFolderPath = reportFolderByExecutionId.getAbsolutePath() + File.separator
                        + testSuite.getIdForDisplay()
                                .substring(FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME.length() + 1)
                                .replace(GlobalStringConstants.ENTITY_ID_SEPARATOR, File.separator);
                File testSuiteReportFolder = new File(testSuiteReportFolderPath);
                if (testSuiteReportFolder.exists() && testSuiteReportFolder.isDirectory()) {
                    for (File reportFolder : testSuiteReportFolder.listFiles()) {
                        if (reportFolder.isDirectory()) {
                            ReportEntity report = ReportFileServiceManager
                                    .getReportEntity(reportFolder.getAbsolutePath());
                            if (report != null) {
                                lstReport.add(report);
                            }
                        }
                    }
                }
            }
        }
        return lstReport;
    }
    
    @Override
    public List<ReportCollectionEntity> listReportCollectionEntities(TestSuiteCollectionEntity testSuiteCollection, ProjectEntity project) throws Exception {
        List<ReportCollectionEntity> reports = new ArrayList<ReportCollectionEntity>();
        String reportRootFolderPath = FileServiceConstant.getReportRootFolderLocation(project.getFolderLocation());
        File reportRootFolder = new File(reportRootFolderPath);
        for (File reportFolderByExecutionId : reportRootFolder.listFiles()) {
            if (reportFolderByExecutionId.isDirectory()) {
                String testSuiteCollectionReportFolderPath = reportFolderByExecutionId.getAbsolutePath() + File.separator
                        + testSuiteCollection.getIdForDisplay()
                                .substring(FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME.length() + 1)
                                .replace(GlobalStringConstants.ENTITY_ID_SEPARATOR, File.separator);
                File testSuiteReportFolder = new File(testSuiteCollectionReportFolderPath);
                if (testSuiteReportFolder.exists() && testSuiteReportFolder.isDirectory()) {
                    for (File reportFolder : testSuiteReportFolder.listFiles()) {
                        if (reportFolder.isDirectory()) {
                            ReportCollectionEntity report = ReportFileServiceManager
                                    .getReportCollectionEntity(reportFolder.getAbsolutePath());
                            if (report != null) {
                                reports.add(report);
                            }
                        }
                    }
                }
            }
        }
        return reports;
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
        EntityService.getInstance().saveIntergratedFolderMetadataEntity(report);
        return report;
    }

    @Override
    public FolderEntity getReportFolder(TestSuiteEntity testSuite, ProjectEntity project, String executionSessionId) throws Exception {
        return FolderFileServiceManager
                .getFolder(ReportFileServiceManager.getReportFolderOfTestSuite(project, testSuite, executionSessionId));
    }

    private String getReportCollectionEntityLocation(ProjectEntity project, TestSuiteCollectionEntity entity, String executionSessionId)
            throws DALException {
        try {
//            return project.getFolderLocation() + File.separator
//                    + entity.getIdForDisplay()
//                            .replaceFirst(FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME,
//                                    FileServiceConstant.REPORT_ROOT_FOLDER_NAME)
//                            .replace(GlobalStringConstants.ENTITY_ID_SEPARATOR, File.separator);
            
            String testSuiteCollectionIdWithoutRoot = entity.getIdForDisplay()
                    .substring(FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME.length() + 1);
            String reportRootFolderPath = FileServiceConstant.getReportRootFolderLocation(project.getFolderLocation());

            return reportRootFolderPath + File.separator + executionSessionId + File.separator + testSuiteCollectionIdWithoutRoot;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public ReportCollectionEntity newReportCollectionEntity(ProjectEntity project, TestSuiteCollectionEntity tsEntity, 
            String executionSessionId, String newName) throws DALException {
        try {

            File folder = new File(getReportCollectionEntityLocation(project, tsEntity, executionSessionId), newName);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            FolderEntity parentFolder = FolderFileServiceManager.getFolder(folder.getAbsolutePath());

            ReportCollectionEntity reportCollection = new ReportCollectionEntity();
            reportCollection.setName(newName);
            reportCollection.setParentFolder(parentFolder);
            reportCollection.setProject(parentFolder.getProject());

            return reportCollection;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public ReportCollectionEntity getReportCollectionEntity(ProjectEntity project, TestSuiteCollectionEntity entity,
            String executionSessionId, String reportName) throws DALException {
        try {
            FolderEntity parentFolder = FolderFileServiceManager
                    .getFolder(getReportCollectionEntityLocation(project, entity, executionSessionId) + File.separator + reportName);

            return getReportCollectionEntity(
                    parentFolder.getId() + File.separator + reportName + ReportCollectionEntity.FILE_EXTENSION);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public ReportCollectionEntity getReportCollectionEntity(String id) throws DALException {
        try {
            ReportCollectionEntity reportCollection = (ReportCollectionEntity) getEntityService().getEntityByPath(id);

            FolderEntity parentFolder = FolderFileServiceManager.getFolder(new File(id).getParent());

            reportCollection.setParentFolder(parentFolder);
            reportCollection.setProject(parentFolder.getProject());
            return reportCollection;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    private EntityService getEntityService() throws DALException {
        try {
            return EntityService.getInstance();
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public void updateReportCollectionEntity(ReportCollectionEntity collectionReport) throws DALException {
        try {
            getEntityService().saveEntity(collectionReport, collectionReport.getId());
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public void deleteReportCollection(ReportCollectionEntity reportCollection) throws DALException {
        try {
            EntityService.getInstance().deleteEntity(reportCollection);

            FolderFileServiceManager.deleteFolder(reportCollection.getParentFolder());
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public ReportEntity renameReport(ReportEntity report, String newName) throws DALException {
        try {
            return ReportFileServiceManager.renameReport(report, newName);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public ReportCollectionEntity renameCollectionReport(ReportCollectionEntity collectionReport, String newName)
            throws DALException {
        try {
            return ReportFileServiceManager.renameReportCollection(collectionReport, newName);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
}
