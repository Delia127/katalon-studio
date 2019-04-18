package com.kms.katalon.controller;

import java.util.List;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

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
}
