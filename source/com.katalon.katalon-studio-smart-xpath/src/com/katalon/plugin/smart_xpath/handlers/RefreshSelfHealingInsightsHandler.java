package com.katalon.plugin.smart_xpath.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.constants.EventConstants;

public class RefreshSelfHealingInsightsHandler {

    @Inject
    private IEventBroker eventBroker;

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
        eventBroker.post(EventConstants.SEFL_HEALING_INSIGHTS_REFRESH, null);
    }
}
