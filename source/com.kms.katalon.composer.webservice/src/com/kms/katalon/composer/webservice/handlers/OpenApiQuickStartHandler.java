package com.kms.katalon.composer.webservice.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.webservice.view.ApiQuickStartDialog;
import com.kms.katalon.constants.EventConstants;

public class OpenApiQuickStartHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.API_QUICK_START_DIALOG_OPEN,
                new EventServiceAdapter() {

                    @Override
                    public void handleEvent(Event event) {
                        openQuickStartDialog();   
                    }           
        });
          
    }
    
    @CanExecute
    public boolean canExecute() {
        return true;
    }
    
    @Execute
    public void execute() {
        openQuickStartDialog();
    }
    
    private void openQuickStartDialog() {
        ApiQuickStartDialog quickStartDialog = new ApiQuickStartDialog(
                Display.getCurrent().getActiveShell());
        quickStartDialog.open();   
    }
}
