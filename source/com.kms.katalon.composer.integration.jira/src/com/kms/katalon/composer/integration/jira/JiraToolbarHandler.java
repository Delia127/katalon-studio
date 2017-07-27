package com.kms.katalon.composer.integration.jira;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.CanExecute;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class JiraToolbarHandler implements JiraUIComponent {

    @CanExecute
    public boolean canExecute() {
        try {
            return getCurrentProject() != null && getSettingStore().isIntegrationEnabled();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }
}
