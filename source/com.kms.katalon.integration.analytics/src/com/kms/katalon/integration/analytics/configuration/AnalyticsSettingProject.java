package com.kms.katalon.integration.analytics.configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.dialog.PermissionAccessAnalyticsDialog;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.handler.AnalyticsAuthorizationHandler;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class AnalyticsSettingProject {
    private String email, password;

    private AnalyticsSettingStore analyticsSettingStore;

    private String serverUrl = AnalyticsStringConstants.ANALYTICS_SERVER_TARGET_ENDPOINT;

    public AnalyticsSettingProject() {
        this.email = "";
        this.password = "";

        this.analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        getAccount();
    }

    private void getAccount() {
        email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
        String passwordDecode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
        try {
            password = CryptoUtil.decode(CryptoUtil.getDefault(passwordDecode));
        } catch (GeneralSecurityException | IOException error) {
            LogUtil.logError(error);
        }
    }

    public void setDataStore() {
        try {
            analyticsSettingStore.enableIntegration(true);
            analyticsSettingStore.enableEncryption(true);
            analyticsSettingStore.setServerEndPoint(serverUrl, true);
            analyticsSettingStore.setEmail(email, true);
            analyticsSettingStore.setPassword(password, true);
            analyticsSettingStore.setAutoSubmit(false);
            analyticsSettingStore.setAttachScreenshot(false);
            analyticsSettingStore.setAttachCapturedVideos(false);
            getDeaultTeamAndProject();
        } catch (IOException | GeneralSecurityException error) {
            LogUtil.logError(error);
        }
    }

    private void getDeaultTeamAndProject() throws IOException {
        List<AnalyticsTeam> teams = new ArrayList<>();
        List<AnalyticsProject> projects = new ArrayList<>();
        AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email, password,
                analyticsSettingStore);

        teams = AnalyticsAuthorizationHandler.getTeams(serverUrl, email, password, tokenInfo,
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));
        AnalyticsTeam team = teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));

        projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password, team, tokenInfo,
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));

        analyticsSettingStore.setTeam(team);
        
        if (!projects.isEmpty()) {
            analyticsSettingStore.setProject(projects.get(0));
        } else {
            analyticsSettingStore.setProject(null);
        }
    }

    public void checkUserAccessProject() {
        try {
            List<AnalyticsTeam> teams = new ArrayList<>();
            String server = analyticsSettingStore.getServerEndpoint(true);
            AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(server, email, password,
                    analyticsSettingStore);
            if (tokenInfo == null) {
                return;
            }
            teams = AnalyticsAuthorizationHandler.getTeams(server, email, password, tokenInfo,
                    new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));
            AnalyticsTeam currentTeam = analyticsSettingStore.getTeam();
            long currentTeamId = currentTeam.getId();
            for (AnalyticsTeam team : teams) {
                long teamId = team.getId();
                if (teamId == currentTeamId) {
                    return;
                }
            }
            PermissionAccessAnalyticsDialog.showErrorDialog(GlobalStringConstants.WARN,
                    IntegrationAnalyticsMessages.VIEW_ERROR_MSG_PROJ_USER_CAN_NOT_ACCESS_PROJECT);
        } catch (IOException | GeneralSecurityException error) {
            LogUtil.logError(error);
        }
    }
}
