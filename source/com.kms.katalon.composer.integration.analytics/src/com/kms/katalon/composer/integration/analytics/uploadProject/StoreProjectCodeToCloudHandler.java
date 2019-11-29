package com.kms.katalon.composer.integration.analytics.uploadProject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.plugin.dialog.KStoreLoginDialog;
import com.kms.katalon.util.CryptoUtil;

public class StoreProjectCodeToCloudHandler {

    @Inject
    IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.STORE_PROJECT_CODE_TO_CLOUD, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (!canExecute()) {
                    return;
                }
                execute(Display.getCurrent().getActiveShell());
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();

            AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(currentProject.getFolderLocation());
            String password = analyticsSettingStore.getPassword();
            String email = analyticsSettingStore.getEmail();

            if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
                Shell shell = Display.getCurrent().getActiveShell();
                KStoreLoginDialog dialog = new KStoreLoginDialog(shell);
                if (dialog.open() == Dialog.OK) {
                    email = dialog.getUsername();
                    password = dialog.getPassword();

                    ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, email, true);
                    String encryptedPassword = CryptoUtil.encode(CryptoUtil.getDefault(password));
                    ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PASSWORD, encryptedPassword, true);
                    dialog.close();
                    StoreProjectCodeToCloudDialog storeCodeProjectDialog = new StoreProjectCodeToCloudDialog(parentShell);
                    storeCodeProjectDialog.open();
               }
            } else {
                StoreProjectCodeToCloudDialog storeCodeProjectDialog = new StoreProjectCodeToCloudDialog(parentShell);
                storeCodeProjectDialog.open();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return;
    }
}
