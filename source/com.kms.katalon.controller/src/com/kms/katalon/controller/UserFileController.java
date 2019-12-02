package com.kms.katalon.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class UserFileController extends EntityController {

    private static UserFileController instance;
    
    public static UserFileController getInstance() {
        if (instance == null) {
            instance = new UserFileController();
        }
        return (UserFileController) instance;
    }
    
    private UserFileController() {        
    }
    
    public UserFileEntity getUserFileEntity(String userFilePath, ProjectEntity projectEntity)
            throws ControllerException {
        try {
            return getDataProviderSetting().getUserFileDataProvider().getUserFileEntity(userFilePath,projectEntity);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }
    
    public List<FileEntity> getChildren(FolderEntity parentFolder) throws ControllerException {
        try {
            return getDataProviderSetting().getUserFileDataProvider().getChildren(parentFolder);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }
    
    public UserFileEntity newFile(String name, FolderEntity parentFolder) throws ControllerException {
        try {
            return getDataProviderSetting().getUserFileDataProvider().newFile(name, parentFolder);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }
    
    public UserFileEntity newRootFile(String name, ProjectEntity project) throws ControllerException {
        try {
            return getDataProviderSetting().getUserFileDataProvider().newRootFile(name, project);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }
    
    public UserFileEntity renameFile(String newName, UserFileEntity userFileEntity) {
        return getDataProviderSetting().getUserFileDataProvider().renameFile(newName, userFileEntity);
    }
    
    public List<FileEntity> getSiblingFiles(UserFileEntity fileEntity, FolderEntity parentFolder)
            throws ControllerException {
        return getChildren(parentFolder).stream().filter(f -> !f.getName().equals(fileEntity.getName()))
                .collect(Collectors.toList());
    }
    
    public void deleteFile(UserFileEntity userFileEntity) {
        getDataProviderSetting().getUserFileDataProvider().deleteFile(userFileEntity);
    }
}
