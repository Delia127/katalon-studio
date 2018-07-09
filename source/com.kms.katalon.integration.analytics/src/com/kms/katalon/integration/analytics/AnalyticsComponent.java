package com.kms.katalon.integration.analytics;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public interface AnalyticsComponent {
    
    default ProjectEntity getCurrentProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    default AnalyticsSettingStore getSettingStore() {
        return new AnalyticsSettingStore(getCurrentProject().getFolderLocation());
    }

}
