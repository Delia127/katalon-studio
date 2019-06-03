package com.kms.katalon.composer.integration.analytics.uploadProject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;

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
        StoreProjectCodeToCloudDialog dialog = new StoreProjectCodeToCloudDialog(parentShell);
        dialog.open();
        return;
    }
}
