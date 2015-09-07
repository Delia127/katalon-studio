package com.kms.katalon.dal.state;

import com.kms.katalon.entity.project.ProjectEntity;

public class DataProviderState {
    
    private static DataProviderState singleton = null;
    
    public synchronized static DataProviderState getInstance() {
        if (singleton == null) {
            singleton = new DataProviderState();
        }
        return singleton;
    }
    
    public ProjectEntity getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(ProjectEntity currentProject) {
        this.currentProject = currentProject;
    }

    private ProjectEntity currentProject;
}
