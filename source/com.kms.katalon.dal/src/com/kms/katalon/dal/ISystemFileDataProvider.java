package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public interface ISystemFileDataProvider {
    List<FileEntity> getChildren(FolderEntity parentFolder) throws DALException;

    List<SystemFileEntity> getFiles(FolderEntity parentFolder) throws DALException;

    SystemFileEntity newFile(String name, String content, FolderEntity parentFolder) throws DALException;

    void deleteFile(SystemFileEntity file);

    SystemFileEntity renameFile(String newName, SystemFileEntity systemFile);

    SystemFileEntity copyFile(SystemFileEntity systemFile, FolderEntity targetFolder) throws DALException;

    SystemFileEntity moveFile(SystemFileEntity systemFile, FolderEntity targetFolder) throws DALException;

    FolderEntity moveFolder(FolderEntity folder, FolderEntity destinationFolder) throws DALException;
}
