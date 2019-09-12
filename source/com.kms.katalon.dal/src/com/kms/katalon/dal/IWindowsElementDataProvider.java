package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public interface IWindowsElementDataProvider {

    WindowsElementEntity get(String id) throws DALException;
    
    List<WindowsElementEntity> getChildren(FolderEntity parentFolder) throws DALException;

    void delete(String id) throws DALException;

    WindowsElementEntity add(FolderEntity parentFolder, String newName) throws DALException;

    WindowsElementEntity rename(String id, String newName) throws DALException;

    WindowsElementEntity update(WindowsElementEntity windowsElement) throws DALException;

    WindowsElementEntity move(String id, FolderEntity newLocation) throws DALException;

    WindowsElementEntity copy(String id, FolderEntity location) throws DALException;
}
