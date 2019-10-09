package com.kms.katalon.composer.mobile.objectspy.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.objectspy.components.KobitonAppComposite;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileObjectSpyDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

public class MobileSpyKobitonDeviceHandler {
    
    @Inject
    IEventBroker eventBroker;

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        openObjectSpyDialog(activeShell);
    }

    private boolean openObjectSpyDialog(Shell activeShell) {
        try {
            
            boolean isKobitonPluginEnabled = KobitonPreferencesProvider.isKobitonPluginInstalled();
            if(!isKobitonPluginEnabled) {
                MessageDialog.openInformation(activeShell, StringConstants.INFO,
                        "Kobiton plugin is not installed yet. Please install the plugin to start using Kobiton integration");
                return false;
            }
            
            boolean isIntegrationEnabled = KobitonPreferencesProvider.isKobitonIntegrationAvailable();
            if (!isIntegrationEnabled) {
                boolean confirmToConfigureKobiton = MessageDialog.openConfirm(activeShell, StringConstants.INFO,
                        "Kobiton integration has not been enabled yet. Would you like to enable now?");
                if (!confirmToConfigureKobiton) {
                    return false;
                }
                eventBroker.post(EventConstants.KATALON_PREFERENCES,
                        "com.kms.katalon.composer.preferences.GeneralPreferencePage/com.kms.katalon.composer.integration.kobiton.preferences");
                return false;
            }
            MobileObjectSpyDialog objectSpyDialog = new MobileObjectSpyDialog(activeShell, new KobitonAppComposite());

            objectSpyDialog.open();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, e.getMessage());
            return false;
        }
    }

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }
}
