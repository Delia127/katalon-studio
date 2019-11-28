package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public interface IGlobalVariableDataProvider {
    public List<GlobalVariableEntity> getAll(String projectPk) throws Exception;

    public GlobalVariableEntity getVariable(String name, String projectPk) throws Exception;

    public GlobalVariableEntity addNewVariable(String newName, String value, String projectPk) throws Exception;

    public List<GlobalVariableEntity> updateVariables(List<GlobalVariableEntity> variables, String projectPk)
            throws Exception;
    
    ExecutionProfileEntity get(String name, ProjectEntity project) throws DALException;
    
    ExecutionProfileEntity getById(String id, ProjectEntity project) throws DALException;

    List<ExecutionProfileEntity> getAll(ProjectEntity project) throws DALException;
    
    ExecutionProfileEntity update(ExecutionProfileEntity executionProfile) throws DALException;
    
    ExecutionProfileEntity rename(String newName, ExecutionProfileEntity executionProfile) throws DALException;
    
    void delete(ExecutionProfileEntity executionProfile) throws DALException;

    ExecutionProfileEntity newProfile(String newName, ProjectEntity project) throws DALException;
    
    ExecutionProfileEntity copyProfile(String newName, ExecutionProfileEntity profileEntity) throws DALException;
}
