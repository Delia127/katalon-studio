package com.kms.katalon.dal.fileservice.dataprovider;

import java.util.Collections;
import java.util.List;

import com.kms.katalon.dal.IFolderDataProvider;
import com.kms.katalon.dal.fileservice.manager.FolderFileServiceManager;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class FolderFileServiceDataProvider implements IFolderDataProvider {

    @Override
    public FolderEntity addNewFolder(FolderEntity parentFolder, String folderName) throws Exception {
        return FolderFileServiceManager.addNewFolder(parentFolder, folderName);
    }

    @Override
    public FolderEntity getFolder(String folderLocation) throws Exception {
        return FolderFileServiceManager.getFolder(folderLocation);
    }

    @Override
    public List<FileEntity> getChildren(FolderEntity folder) throws Exception {
        if (folder != null) {
            if (folder.getFolderType() == FolderType.REPORT) {
                return FolderFileServiceManager.getChildReportsOfFolder(folder);
            }
            return FolderFileServiceManager.getChildren(folder);
        }
        return Collections.emptyList();
    }

    @Override
    public void updateFolderName(FolderEntity folder, String newName) throws Exception {
        FolderFileServiceManager.updateFolderName(folder, newName);
    }

    @Override
    public void deleteFolder(FolderEntity folder) throws Exception {
        FolderFileServiceManager.deleteFolder(folder);
    }

    @Override
    public FolderEntity getTestSuiteRoot(ProjectEntity project) throws Exception {
        return FolderFileServiceManager.getTestSuiteRoot(project);
    }

    @Override
    public FolderEntity getTestCaseRoot(ProjectEntity project) throws Exception {
        return FolderFileServiceManager.getTestCaseRoot(project);
    }

    @Override
    public FolderEntity getTestDataRoot(ProjectEntity project) throws Exception {
        return FolderFileServiceManager.getTestDataRoot(project);
    }

    @Override
    public FolderEntity getObjectRepositoryRoot(ProjectEntity project) throws Exception {
        return FolderFileServiceManager.getObjectRepositoryRoot(project);
    }

    @Override
    public FolderEntity getKeywordRoot(ProjectEntity project) throws Exception {
        return FolderFileServiceManager.getKeywordRoot(project);
    }

    @Override
    public FolderEntity getReportRoot(ProjectEntity project) throws Exception {
        return FolderFileServiceManager.getReportRoot(project);
    }

    @Override
    public FolderEntity getCheckpointRoot(ProjectEntity project) throws Exception {
        return FolderFileServiceManager.getCheckpointRoot(project);
    }

    @Override
    public FolderEntity copyFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        return FolderFileServiceManager.copyFolder(folder, destinationFolder);
    }

    @Override
    public FolderEntity moveFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        return FolderFileServiceManager.moveFolder(folder, destinationFolder);
    }

    @Override
    public void refreshFolder(FolderEntity folder) throws Exception {
        FolderFileServiceManager.refreshFolder(folder);
    }

    @Override
    public FolderEntity saveFolder(FolderEntity folder) throws Exception {
        return FolderFileServiceManager.saveFolder(folder);
    }

    @Override
    public String getAvailableFolderName(FolderEntity parentFolder, String name) throws Exception {
        return FolderFileServiceManager.getAvailableFolderName(parentFolder, name);
    }

    @Override
    public List<TestCaseEntity> getTestCaseChildren(FolderEntity parentFolder) throws Exception {
        return FolderFileServiceManager.getChildTestCasesOfFolder(parentFolder);
    }
}
