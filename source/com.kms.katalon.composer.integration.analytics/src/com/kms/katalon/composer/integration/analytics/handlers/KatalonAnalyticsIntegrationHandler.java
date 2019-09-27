package com.kms.katalon.composer.integration.analytics.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.integration.analytics.dialog.KatalonTestOpsIntegrationDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;

public class KatalonAnalyticsIntegrationHandler {
    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.ANALYTIC_QUICK_INTEGRATION_DIALOG_OPEN, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                execute();
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute() {
        KatalonTestOpsIntegrationDialog quickStartDialog = new KatalonTestOpsIntegrationDialog(Display.getCurrent().getActiveShell());
        if (quickStartDialog.checkConnection()) {
             quickStartDialog.open();
        }
    }
}
