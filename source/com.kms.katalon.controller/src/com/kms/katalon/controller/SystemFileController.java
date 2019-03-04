package com.kms.katalon.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class SystemFileController extends EntityController {

    private static SystemFileController _instance;

    public static SystemFileController getInstance() {
        if (_instance == null) {
            _instance = new SystemFileController();
        }
        return (SystemFileController) _instance;
    }

    public SystemFileEntity newFile(String newName, String content, FolderEntity folder) throws ControllerException {
        try {
            return getDataProviderSetting().getSystemFileDataProvider().newFile(newName, content, folder);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }

    public List<FileEntity> getChildren(FolderEntity parentFolder) throws ControllerException {
        try {
            return getDataProviderSetting().getSystemFileDataProvider().getChildren(parentFolder);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }

    public void deleteFile(SystemFileEntity fileEntity) {
        getDataProviderSetting().getSystemFileDataProvider().deleteFile(fileEntity);
    }

    public SystemFileEntity renameSystemFile(String newName, SystemFileEntity renamedFile) {
        return getDataProviderSetting().getSystemFileDataProvider().renameFile(newName, renamedFile);
    }

    public List<FileEntity> getSiblingFiles(SystemFileEntity renamedFile, FolderEntity parentFolder)
            throws ControllerException {
        return getChildren(parentFolder).stream()
                .filter(f -> !f.getName().equals(renamedFile.getName()))
                .collect(Collectors.toList());
    }

    public SystemFileEntity copySystemFile(SystemFileEntity sourceFile, FolderEntity targetFolder) throws DALException {
        return getDataProviderSetting().getSystemFileDataProvider().copyFile(sourceFile, targetFolder);
    }

    public SystemFileEntity moveSystemFile(SystemFileEntity systemFile, FolderEntity targetFolder) throws DALException {
        return getDataProviderSetting().getSystemFileDataProvider().moveFile(systemFile, targetFolder);
    }

    public SystemFileEntity getSystemFile(String systemFilePath, ProjectEntity projectEntity)
            throws ControllerException {
        try {
            return getDataProviderSetting().getSystemFileDataProvider().getSystemFile(systemFilePath, projectEntity);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }
}
