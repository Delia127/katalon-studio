package com.kms.katalon.controller;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;

public class PropertiesController extends EntityController {
    private static EntityController instance;

    public static PropertiesController getInstance() {
        if (instance == null) {
            instance = new PropertiesController();
        }
        return (PropertiesController) instance;
    }

    public FileEntity updateProperties(FileEntity entity) throws DALException {
        boolean isUpdated = getDataProviderSetting().getEntityDataProvider().update(entity);

        if (isUpdated) {
            return entity;
        }

        return null;
    }
}
