package com.kms.katalon.platform.internal.controller;

import java.text.MessageFormat;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.Integration;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;

public class ProjectControllerImpl implements com.katalon.platform.api.controller.ProjectController {

    @Override
    public com.katalon.platform.api.model.ProjectEntity updateIntegration(
            com.katalon.platform.api.model.ProjectEntity project, Integration integration) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());

            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }

            IntegratedEntity newIntegrated = new IntegratedEntity();
            newIntegrated.setProductName(integration.getName());
            newIntegrated.setProperties(integration.getProperties());
            
            ProjectEntity updatedProject = (ProjectEntity) projectEntity.updateIntegratedEntity(newIntegrated);
            return new ProjectEntityImpl(updatedProject);
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

}
