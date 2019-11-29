package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public interface IUserFileDataProvider {
    List<FileEntity> getChildren(FolderEntity parentFolder) throws DALException;
    UserFileEntity getUserFileEntity(String userFilePath, ProjectEntity projectEntity) throws DALException;
    UserFileEntity newFile(String name, FolderEntity parentFolder) throws DALException;
    UserFileEntity newRootFile(String name, ProjectEntity project) throws DALException;
    UserFileEntity renameFile(String newName, UserFileEntity userFileEntity);
    void deleteFile(UserFileEntity userFileEntity);
}
