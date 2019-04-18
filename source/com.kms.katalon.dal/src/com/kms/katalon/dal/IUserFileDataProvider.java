package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public interface IUserFileDataProvider {
    List<FileEntity> getChildren(FolderEntity parentFolder) throws DALException;
    UserFileEntity newFile(String name, FolderEntity parentFolder) throws DALException;
}
