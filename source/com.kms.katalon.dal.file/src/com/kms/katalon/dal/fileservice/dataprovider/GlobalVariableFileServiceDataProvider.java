package com.kms.katalon.dal.fileservice.dataprovider;

import java.util.List;

import com.kms.katalon.dal.IGlobalVariableDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.manager.GlobalVariableFileServiceManager;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class GlobalVariableFileServiceDataProvider implements IGlobalVariableDataProvider {

    @Override
    public List<GlobalVariableEntity> getAll(String projectPk) throws Exception {
        return GlobalVariableFileServiceManager.getAll(projectPk);
    }

    @Override
    public GlobalVariableEntity getVariable(String name, String projectPk) throws Exception {
        for (GlobalVariableEntity variable : GlobalVariableFileServiceManager.getAll(projectPk)) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

    @Override
    public GlobalVariableEntity addNewVariable(String newName, String value, String projectPk) throws Exception {
        return GlobalVariableFileServiceManager.addNewVariable(newName, value, projectPk);
    }

    @Override
    public List<GlobalVariableEntity> updateVariables(List<GlobalVariableEntity> variables, String projectPk)
            throws Exception {
        return GlobalVariableFileServiceManager.updateVariables(variables, projectPk);
    }

    @Override
    public ExecutionProfileEntity get(String name, ProjectEntity project) throws DALException {
        return GlobalVariableFileServiceManager.get(name, project);
    }

    @Override
    public ExecutionProfileEntity getById(String id, ProjectEntity project) throws DALException {
        return GlobalVariableFileServiceManager.getById(id, project);
    }

    @Override
    public List<ExecutionProfileEntity> getAll(ProjectEntity project) throws DALException {
        return GlobalVariableFileServiceManager.getAll(project);
    }

    @Override
    public ExecutionProfileEntity update(ExecutionProfileEntity executionProfile) throws DALException {
        try {
            EntityService.getInstance().saveEntity(executionProfile);
        } catch (Exception e) {
            throw new DALException(e);
        }
        return executionProfile;
    }

    @Override
    public ExecutionProfileEntity rename(String newName, ExecutionProfileEntity executionProfile) throws DALException {
        try {
            EntityService.getInstance().deleteEntity(executionProfile);

            executionProfile.setName(newName);

            EntityService.getInstance().saveEntity(executionProfile);

            return executionProfile;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public void delete(ExecutionProfileEntity executionProfile) throws DALException {
        try {
            EntityService.getInstance().deleteEntity(executionProfile);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    @Override
    public ExecutionProfileEntity newProfile(String newName, ProjectEntity project) throws DALException {
        return GlobalVariableFileServiceManager.newProfile(newName, false, project);
    }

    @Override
    public ExecutionProfileEntity copyProfile(String newName, ExecutionProfileEntity profileEntity) throws DALException {
        return GlobalVariableFileServiceManager.newProfile(newName, false,
                profileEntity.getGlobalVariableEntities(), profileEntity.getProject());
    }

}
