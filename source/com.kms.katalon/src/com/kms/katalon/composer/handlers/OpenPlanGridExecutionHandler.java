package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.logging.LogUtil;

public class OpenPlanGridExecutionHandler {
    
    @CanExecute
    public boolean canExecute() {
        return true;
    }
    
    @Execute
    public void execute() {
        try {
            AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
                    ProjectController.getInstance().getCurrentProject().getFolderLocation());
            String serverUrl = analyticsSettingStore.getServerEndpoint(true);
            long teamId = analyticsSettingStore.getTeam().getId();
            long projectId = analyticsSettingStore.getProject().getId();

            String url = serverUrl + "/team/" + teamId + "/project/" + projectId + "/grid";
            Program.launch(url);
        } catch (IOException | GeneralSecurityException e) {
            MessageDialog.openError(null, GlobalStringConstants.ERROR, "Cannot open Plan Grid Execution");
            LoggerSingleton.logError(e);
        }
    }
}
