package com.kms.katalon.platform.internal.controller;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.controller.ExecutionProfileController;
import com.katalon.platform.api.exception.ResourceException;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.platform.internal.entity.ExecutionProfileEntityImpl;

public class ExecutionProfileControllerImpl implements ExecutionProfileController {

    @Override
    public List<com.katalon.platform.api.model.ExecutionProfileEntity> getAllProfiles(
            com.katalon.platform.api.model.ProjectEntity project) throws ResourceException {

        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }

            List<ExecutionProfileEntity> sourceExecutionProfiles = GlobalVariableController.getInstance()
                    .getAllGlobalVariableCollections(projectEntity);
            
            return sourceExecutionProfiles.stream().map(gl -> new ExecutionProfileEntityImpl(gl)).collect(Collectors.toList());
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.ExecutionProfileEntity getProfile(
            com.katalon.platform.api.model.ProjectEntity project, String projectName) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }

            ExecutionProfileEntity sourceProfile = GlobalVariableController.getInstance().getExecutionProfile(projectName, projectEntity);
            if (sourceProfile == null) {
                throw new ResourceException(MessageFormat.format("Profile {0} not found", projectName));
            }
            return new ExecutionProfileEntityImpl(sourceProfile);
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.ExecutionProfileEntity newProfile(
            com.katalon.platform.api.model.ProjectEntity project, String projectName) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }

            ExecutionProfileEntity sourceProfile = GlobalVariableController.getInstance().newExecutionProfile(projectName, projectEntity);
            return new ExecutionProfileEntityImpl(sourceProfile);
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }
}
