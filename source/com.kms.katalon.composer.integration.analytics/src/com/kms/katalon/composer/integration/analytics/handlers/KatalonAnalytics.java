package com.kms.katalon.composer.integration.analytics.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;

public class KatalonAnalytics {
    public static final String KATALON_ANALYTICS_SETTING_PAGE_ID = "com.kms.katalon.composer.integration.analytics.page";
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
      EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.PROJECT_SETTINGS_PAGE ,KATALON_ANALYTICS_SETTING_PAGE_ID);
        
    }
}
