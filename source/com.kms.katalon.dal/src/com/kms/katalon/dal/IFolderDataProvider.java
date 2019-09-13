package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public interface IFolderDataProvider {
    public FolderEntity addNewFolder(FolderEntity parentFolder, String folderName) throws Exception;

    public FolderEntity addNewRootFolder(ProjectEntity project, String folderName) throws Exception;
    
    public FolderEntity getFolder(String folderValue) throws Exception;

    public List<FileEntity> getChildren(FolderEntity parentFolder) throws Exception;

    public List<TestCaseEntity> getTestCaseChildren(FolderEntity parentFolder) throws Exception;

    public void updateFolderName(FolderEntity folder, String name) throws Exception;

    public void deleteFolder(FolderEntity folder) throws Exception;

    public FolderEntity copyFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception;

    public FolderEntity moveFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception;

    public FolderEntity getTestSuiteRoot(ProjectEntity project) throws Exception;

    public FolderEntity getTestCaseRoot(ProjectEntity project) throws Exception;

    public FolderEntity getTestDataRoot(ProjectEntity project) throws Exception;

    public FolderEntity getObjectRepositoryRoot(ProjectEntity project) throws Exception;

    public FolderEntity getKeywordRoot(ProjectEntity project) throws Exception;

    public FolderEntity getReportRoot(ProjectEntity project) throws Exception;

    public FolderEntity getCheckpointRoot(ProjectEntity project) throws Exception;

    public FolderEntity getTestListenerRoot(ProjectEntity project) throws Exception;
    
    public FolderEntity getProfileRoot(ProjectEntity project) throws DALException;

    public FolderEntity getIncludeRoot(ProjectEntity project) throws DALException;
    
    public FolderEntity getPluginsRoot(ProjectEntity project) throws DALException;

    public FolderEntity getFeatureRoot(ProjectEntity project) throws DALException;
    
    public FolderEntity getGroovyScriptRoot(ProjectEntity project) throws DALException;
    
    public void refreshFolder(FolderEntity folder) throws Exception;

    /*
     * Save meta data of the given folder. Ex: integration information.
     */
    public FolderEntity saveFolder(FolderEntity folder) throws Exception;

    public String getAvailableFolderName(FolderEntity parentFolder, String name) throws Exception;

    public List<FileEntity> getRootUserFilesOrFolders(ProjectEntity project) throws DALException;
}
