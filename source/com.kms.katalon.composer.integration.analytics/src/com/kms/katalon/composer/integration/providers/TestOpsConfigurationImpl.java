package com.kms.katalon.composer.integration.providers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.integration.analytics.dialog.KatalonTestOpsIntegrationDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.feature.TestOpsConfiguration;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.util.CryptoUtil;

public class TestOpsConfigurationImpl implements TestOpsConfiguration {

    @Override
    public void testOpsQuickIntergration() {
        enableTestOpsIntegration();
    }

    private void enableTestOpsIntegration() {
        final ProgressMonitorDialogWithThread dialog = new ProgressMonitorDialogWithThread(
                Display.getCurrent().getActiveShell());
        IRunnableWithProgress enableWorker = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
                        ProjectController.getInstance().getCurrentProject().getFolderLocation());
                String serverUrl = analyticsSettingStore.getServerEndpoint();
                String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
                if (StringUtils.isBlank(email) || StringUtils.isBlank(encryptedPassword)
                        || analyticsSettingStore.getOrganization() == null) {
                    throw new InvocationTargetException(new Exception(),
                            ComposerIntegrationAnalyticsMessageConstants.ERROR_TESTOPS_INTEGRATION_INVALID_CREDENTIAL);
                }

                try {
                    monitor.beginTask(ComposerIntegrationAnalyticsMessageConstants.TASK_MSG_ENABLE_INTEGRATION, 3);
                    monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.TASK_MSG_CHECK_CONNECTION);
                    if (!AnalyticsApiProvider.testConnection(serverUrl)) {
                        throw new InvocationTargetException(new Exception(),
                                ComposerIntegrationAnalyticsMessageConstants.ERROR_TESTOPS_INTEGRATION_CONNECTION);
                    }

                    monitor.worked(1);
                    monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.TASK_MSG_GETTING_TESTOPS_INFO);
                    AnalyticsProject project = getDefaultProject();
                    if (project == null) {
                        monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.TASK_MSG_SETTING_INTEGRATION);
                        UISynchronizeService.asyncExec(() -> {
                            KatalonTestOpsIntegrationDialog quickStartDialog = new KatalonTestOpsIntegrationDialog(
                                    Display.getCurrent().getActiveShell());
                            quickStartDialog.open();
                            EventBrokerSingleton.getInstance()
                                    .getEventBroker()
                                    .post(EventConstants.EXPLORER_RELOAD_DATA, true);
                        });
                        return;
                    }

                    monitor.worked(1);
                    String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                    AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);

                    monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.TASK_MSG_GETTING_TEAMS);
                    List<AnalyticsTeam> teams = AnalyticsApiProvider.getTeams(serverUrl, token.getAccess_token(),
                            analyticsSettingStore.getOrganization().getId());
                    Optional<AnalyticsTeam> defaultTeam = teams.stream()
                            .filter(team -> team.getId().equals(project.getTeamId()))
                            .findFirst();
                    if (!defaultTeam.isPresent()) {
                        throw new InvocationTargetException(new Exception(),
                                ComposerIntegrationAnalyticsMessageConstants.ERROR_TESTOPS_INTEGRATION_INVALID_DATA);
                    }

                    monitor.worked(1);
                    analyticsSettingStore.enableIntegration(true);
                    analyticsSettingStore.setTeam(defaultTeam.get());
                    analyticsSettingStore.setProject(project);
                    analyticsSettingStore.setAutoSubmit(true);
                    EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_RELOAD_DATA, true);
                } catch (AnalyticsApiExeception e) {
                    throw new InvocationTargetException(e,
                            ComposerIntegrationAnalyticsMessageConstants.ERROR_TESTOPS_INTEGRATION_CONNECTION);
                } catch (IOException | GeneralSecurityException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        try {
            dialog.run(true, false, enableWorker);
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e,
                    ComposerIntegrationAnalyticsMessageConstants.ERROR_TESTOPS_INTEGRATION, e.getMessage());
        } catch (InterruptedException e) {
            LoggerSingleton.logError(e);
        }

    }

    private AnalyticsProject getDefaultProject() throws AnalyticsApiExeception, GeneralSecurityException, IOException {
        AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        String serverUrl = analyticsSettingStore.getServerEndpoint();
        String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
        String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
        if (!StringUtils.isBlank(email) && !StringUtils.isBlank(encryptedPassword)) {
            String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
            AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
            return AnalyticsApiProvider.getDefaultNewUserProject(serverUrl, token.getAccess_token());
        }

        return null;
    }

}
