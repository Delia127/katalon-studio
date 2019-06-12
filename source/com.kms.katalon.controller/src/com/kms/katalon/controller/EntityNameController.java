package com.kms.katalon.controller;

import java.io.Serializable;

import com.kms.katalon.entity.folder.FolderEntity;

public class EntityNameController extends EntityController implements Serializable {

    private static final long serialVersionUID = 22275961091135393L;

    private static EntityController _instance;

    private EntityNameController() {
        super();
    }

    public static EntityNameController getInstance() {
        if (_instance == null) {
            _instance = new EntityNameController();
        }
        return (EntityNameController) _instance;
    }

    public String getAvailableName(String name, FolderEntity parentFolder, boolean isFolder) throws Exception {
        return getDataProviderSetting().getEntityNameProvider().getAvailableName(name, parentFolder, isFolder);
    }

    public static void validateName(String name) throws Exception {
        getDataProviderSetting().getEntityNameProvider().validateName(name);
    }

    public boolean isNameExisted(FolderEntity parentFolder, String entityName) throws Exception {
        for (String filename : FolderController.getInstance().getChildrenNames(parentFolder)) {
            if (filename.equalsIgnoreCase(entityName)) {
                return true;
            }
        }
        return false;
    }

}
