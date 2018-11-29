package com.kms.katalon.controller;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.variable.VariableEntityWrapper;


public class LocalVariableController extends EntityController{
    private static LocalVariableController _instance;

    private LocalVariableController() {

    }

    public static LocalVariableController getInstance() {
        if (_instance == null) {
            _instance = new LocalVariableController();
        }
        return (LocalVariableController) _instance;
    }
    
    public VariableEntityWrapper toVariableEntityWrapper(String xmlString) throws DALException{
        return getDataProviderSetting().getEntityDataProvider().toEntity(xmlString, VariableEntityWrapper.class);
    }
}
