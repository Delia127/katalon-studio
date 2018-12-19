package com.kms.katalon.platform.internal.service.impl;

import com.katalon.platform.api.model.Project;
import com.katalon.platform.api.service.ProjectManager;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class ProjectManagerImpl implements ProjectManager {

    @Override
    public Project getCurrentProject() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if (project != null) {
            Project platformProject = new Project() {

                @Override
                public String getId() {
                    return project.getId();
                }

                @Override
                public String getName() {
                    return project.getName();
                }

                @Override
                public String getFolderLocation() {
                    return project.getFolderLocation();
                }

                @Override
                public String getFileLocation() {
                    return project.getLocation();
                }
            };
            return platformProject;
        } else {
            return null;
        }
    }

}
