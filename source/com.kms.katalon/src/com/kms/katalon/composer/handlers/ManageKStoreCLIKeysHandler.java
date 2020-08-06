package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.activation.plugin.models.KStoreBasicCredentials;
import com.kms.katalon.activation.plugin.service.KStoreRestClient;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class ManageKStoreCLIKeysHandler extends RequireAuthorizationHandler {
    
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
        try {
            KStoreBasicCredentials credentials = getBasicCredentials();
            if (credentials != null) {
                KStoreRestClient restClient = new KStoreRestClient(credentials);
                restClient.goToManageApiKeysPage();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, "Error", "Cannot open Manage API Keys page");
        }
    }
}
