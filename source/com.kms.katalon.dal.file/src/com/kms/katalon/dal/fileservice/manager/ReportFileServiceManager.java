package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.ITestSuiteDataProvider;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.dataprovider.setting.FileServiceDataProviderSetting;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class ReportFileServiceManager {

    public static void initReportFolder(String path) throws IOException {
        File reportRootPath = new File(path, FileServiceConstant.REPORT_ROOT_FOLDER_NAME);
        if (!reportRootPath.exists()) {
            reportRootPath.mkdir();
        }
    }

    public static void ensureFolderExist(File folder) throws Exception {
        if (folder.exists()) {
            return;
        }
        if (!folder.getParentFile().exists()) {
            ensureFolderExist(folder.getParentFile());
        }
        folder.mkdirs();
    }

    public static String getReportFolderOfTestSuite(ProjectEntity project, TestSuiteEntity testSuite) throws Exception {
        ITestSuiteDataProvider testSuiteProvider = new FileServiceDataProviderSetting().getTestSuiteDataProvider();
        String testSuiteIdWithoutRoot = testSuiteProvider.getIdForDisplay(testSuite)
                .substring(FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME.length() + 1);
        String reportRootFolderPath = FileServiceConstant.getReportRootFolderLocation(project.getFolderLocation());

        return reportRootFolderPath + File.separator + testSuiteIdWithoutRoot;
    }
    
    public static String getReportFolderOfTestSuiteCollection(ProjectEntity project, TestSuiteCollectionEntity testSuiteCollection) throws Exception {
        ITestSuiteDataProvider testSuiteProvider = new FileServiceDataProviderSetting().getTestSuiteDataProvider();
        String testSuiteCollectionIdWithoutRoot = testSuiteProvider.getTestSuiteCollectionIdForDisplay(testSuiteCollection)
                .substring(FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME.length() + 1);
        String reportRootFolderPath = FileServiceConstant.getReportRootFolderLocation(project.getFolderLocation());
        
        return reportRootFolderPath + File.separator + testSuiteCollectionIdWithoutRoot;
        
    }

    public static void deleteReport(ReportEntity report) throws Exception {
        EntityFileServiceManager.delete(report);
    }

    public static ReportEntity getReportEntity(String path) throws Exception {
        File folderMetaDataFile = new File(new File(path), FolderEntity.getMetaDataFileExtension());
        FileEntity entity = null;
        File reportFile = new File(path);
        if (folderMetaDataFile.exists()) {
            entity = EntityService.getInstance().loadEntityFromFile(folderMetaDataFile.getAbsolutePath());
        } else {
            entity = new ReportEntity();
        }

        entity.setParentFolder(FolderFileServiceManager.getFolder(reportFile.getParent()));
        entity.setProject(DataProviderState.getInstance().getCurrentProject());
        entity.setName(FilenameUtils.getBaseName(path));

        if (entity != null && entity instanceof ReportEntity) {
            return (ReportEntity) entity;
        }
        return null;
    }
    
    public static ReportCollectionEntity getReportCollectionEntity(String path) throws Exception {
        File reportFolder = new File(path);
        File reportCollectionFile = new File(reportFolder,
                reportFolder.getName() + FileServiceConstant.REPORT_COLLECTION_FILE_EXTENSION);
        if (reportCollectionFile.exists()) {
            FileEntity entity = EntityService.getInstance().loadEntityFromFile(reportCollectionFile.getAbsolutePath());
            entity.setParentFolder(FolderFileServiceManager.getFolder(reportCollectionFile.getParent()));
            entity.setProject(DataProviderState.getInstance().getCurrentProject());
            entity.setName(FilenameUtils.getBaseName(path));
    
            if (entity != null && entity instanceof ReportCollectionEntity) {
                return (ReportCollectionEntity) entity;
            }
        }

        return null;
    }

    public static ReportEntity createReportEntity(String reportName, FolderEntity parentFolder) throws Exception {
        ReportEntity report = new ReportEntity();
        report.setName(reportName);
        report.setParentFolder(parentFolder);
        report.setProject(parentFolder.getProject());
        // Set Date created for sorting
        // Path filePath = Paths.get(parentFolder.getLocation() + File.separator + reportName + ".html");
        Path filePath = Paths.get(parentFolder.getLocation() + File.separator + reportName);
        BasicFileAttributeView basicView = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
        BasicFileAttributes basicAttr = basicView.readAttributes();
        FileTime createdDate = basicAttr.creationTime();
        FileTime modifiedDate = basicAttr.creationTime();
        report.setDateCreated(new Date(createdDate.toMillis()));
        report.setDateModified(new Date(modifiedDate.toMillis()));
        // Cache it
        if (!EntityService.getInstance().getEntityCache().contains(report.getLocation())) {
            EntityService.getInstance().getEntityCache().put(report.getLocation(), report);
        }
        return report;
    }

    private static Comparator<FileEntity> comparator;

    public static void sortListByCreatedDate(List<FileEntity> list, final boolean desc) {
        if (comparator == null) {
            comparator = new Comparator<FileEntity>() {
                @Override
                public int compare(FileEntity e1, FileEntity e2) {
                    if (canCompareUsingDisplayName(e1, e2)) {
                        return compareUsingDisplayName(desc, e1, e2);
                    }
                    if (e1 != null && e2 != null && e1.getDateCreated() != null && e2.getDateCreated() != null) {
                        return desc ? e2.getDateCreated().compareTo(e1.getDateCreated())
                                : e1.getDateCreated().compareTo(e2.getDateCreated());
                    }
                    return desc ? e2.getName().compareTo(e1.getName()) : e1.getName().compareTo(e2.getName());
                }

                private int compareUsingDisplayName(final boolean desc, FileEntity e1, FileEntity e2) {
                    return desc
                            ? ((ReportEntity) e2).getDisplayName().compareTo(((ReportEntity) e1).getDisplayName())
                            : ((ReportEntity) e1).getDisplayName().compareTo(((ReportEntity) e2).getDisplayName());
                }

                private boolean canCompareUsingDisplayName(FileEntity e1, FileEntity e2) {
                    return e1 instanceof ReportEntity && e2 instanceof ReportEntity
                            && ((ReportEntity) e1).getDisplayName() != null
                            && ((ReportEntity) e2).getDisplayName() != null;
                }
            };
        }
        if (list != null && list.size() > 0) {
            Collections.sort(list, comparator);
        }
    }

    private static void validateRenameReport(FileEntity fileEntity, String newName) throws Exception {
        EntityService.getInstance().validateName(newName);
        String newLocation = fileEntity.getParentFolder().getLocation() + File.separator + newName;
        if (newLocation.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(newLocation.length(), FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }
    }

    public static ReportEntity renameReport(ReportEntity report, String newName) throws Exception {
        if (report == null || report.getProject() == null) {
            return null;
        }
        validateRenameReport(report, newName);
        report.setDisplayName(newName);
        EntityService.getInstance().saveFolderMetadataEntity(report);
        FolderFileServiceManager.refreshFolder(report.getParentFolder());
        return report;
    }
    
    public static ReportCollectionEntity renameReportCollection(ReportCollectionEntity reportCollection, String newName) throws Exception {
        if (reportCollection == null || reportCollection.getProject() == null) {
            return null;
        }
        validateRenameReport(reportCollection, newName);
        reportCollection.setDisplayName(newName);
        EntityService.getInstance().saveEntity(reportCollection, reportCollection.getLocation());
        FolderFileServiceManager.refreshFolder(reportCollection.getParentFolder());
        return reportCollection;
    }
}
