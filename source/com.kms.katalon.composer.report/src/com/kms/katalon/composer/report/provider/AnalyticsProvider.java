package com.kms.katalon.composer.report.provider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.report.constants.ComposerReportMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class AnalyticsProvider {
    
    public static AnalyticsTokenInfo getToken(String serverUrl, String email, String password, AnalyticsSettingStore settingStore) {
        try {
            boolean encryptionEnabled = true;
            AnalyticsTokenInfo tokenInfo = AnalyticsApiProvider.requestToken(serverUrl, email, password);
            settingStore.setToken(tokenInfo.getAccess_token(), encryptionEnabled);
            return tokenInfo;
        } catch(Exception ex) {
            LoggerSingleton.logError(ex);
            try {
                settingStore.setPassword(StringUtils.EMPTY, true);
                settingStore.enableIntegration(false);
            } catch (IOException | GeneralSecurityException e) {
                LoggerSingleton.logError(e);
            }
            MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR,
                    ComposerIntegrationAnalyticsMessageConstants.MSG_REQUEST_TOKEN_ERROR);
        }
        return null;    
    } 
    
    public static List<AnalyticsProject> getProjects(final String serverUrl, final String email, final String password,
            final AnalyticsTeam team, AnalyticsTokenInfo tokenInfo, ProgressMonitorDialog monitorDialog) {
        final List<AnalyticsProject> projects = new ArrayList<>();
        try {
            monitorDialog.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(IntegrationAnalyticsMessages.MSG_DLG_PRG_RETRIEVING_PROJECTS, 2);
                        monitor.subTask(IntegrationAnalyticsMessages.MSG_DLG_PRG_GETTING_PROJECTS);
                        final List<AnalyticsProject> loaded = AnalyticsApiProvider.getProjects(serverUrl, team,
                                tokenInfo.getAccess_token());
                        if (loaded != null && !loaded.isEmpty()) {
                            projects.addAll(loaded);
                        }
                        monitor.worked(1);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            return projects;
        } catch (InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof AnalyticsApiExeception) {
                MultiStatusErrorDialog.showErrorDialog(exception, ComposerAnalyticsStringConstants.ERROR,
                        cause.getMessage());
            } else {
                LoggerSingleton.logError(cause);
            }
        } catch (InterruptedException e) {
            // Ignore this
        }
        return projects;
    }
    
    public static List<AnalyticsTeam> getTeams(final String serverUrl, final String email, final String password,
            AnalyticsTokenInfo tokenInfo, ProgressMonitorDialog monitorDialog) {
        final List<AnalyticsTeam> teams = new ArrayList<>();
        try {
            monitorDialog.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(IntegrationAnalyticsMessages.MSG_DLG_PRG_RETRIEVING_TEAMS, 2);
                        monitor.subTask(IntegrationAnalyticsMessages.MSG_DLG_PRG_GETTING_TEAMS);
                        final List<AnalyticsTeam> loaded = AnalyticsApiProvider.getTeams(serverUrl,
                                tokenInfo.getAccess_token());
                        if (loaded != null && !loaded.isEmpty()) {
                            teams.addAll(loaded);
                        }
                        monitor.worked(1);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            return teams;
        } catch (InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof AnalyticsApiExeception) {
                MultiStatusErrorDialog.showErrorDialog(exception, ComposerAnalyticsStringConstants.ERROR,
                        cause.getMessage());
            } else {
                LoggerSingleton.logError(cause);
            }
        } catch (InterruptedException e) {
            // Ignore this
        }
        return teams;
    }
    
    public static void createDefaultProject(AnalyticsSettingStore analyticsSettingStore, String serverUrl, AnalyticsTeam team, 
            AnalyticsTokenInfo tokenInfo, ProgressMonitorDialog monitorDialog) {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        String currentProjectName = project.getName();
        try {
            monitorDialog.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) {
                    try {
                        monitor.beginTask(ComposerReportMessageConstants.REPORT_MSG_UPLOADING_TO_ANALYTICS, 2);
                        monitor.subTask(ComposerReportMessageConstants.REPORT_MSG_UPLOADING_TO_ANALYTICS_SENDING);
                        AnalyticsProject analyticsProject = AnalyticsApiProvider.createProject(serverUrl,
                                currentProjectName, team, tokenInfo.getAccess_token());
                        analyticsSettingStore.setProject(analyticsProject);
                        analyticsSettingStore.setTeam(team);
                        monitor.worked(1);
                    } catch (AnalyticsApiExeception | IOException ex) {
                        LoggerSingleton.logError(ex);
                        MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                ComposerAnalyticsStringConstants.ERROR,
                                ComposerReportMessageConstants.REPORT_ERROR_MSG_UNABLE_TO_UPLOAD_REPORT);
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException ex) {
            LoggerSingleton.logError(ex);
            MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR,
                    ComposerReportMessageConstants.REPORT_ERROR_MSG_UNABLE_TO_UPLOAD_REPORT);
        }
    }
}
