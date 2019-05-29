package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.program.Program;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.logging.LogUtil;

public class ViewDashboardHandler {
    
    @CanExecute
    public void canExecute() {
        return;
    }
    
    @Execute
    public void execute() {
        try {
            AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
                    ProjectController.getInstance().getCurrentProject().getFolderLocation());
            String serverUrl = analyticsSettingStore.getServerEndpoint(true);
            long teamId = analyticsSettingStore.getTeam().getId();
            long projectId = analyticsSettingStore.getProject().getId();

            String url = serverUrl + "/team/" + teamId + "/project/" + projectId;
            Program.launch(url);
        } catch (IOException | GeneralSecurityException e) {
            LogUtil.logError(e);
        }
    }
}
