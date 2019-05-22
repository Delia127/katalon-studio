package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.processors.ToolbarProcessor;

public class LoggedInHandler {

    @Inject
    IEventBroker eventBroker;
    
    @Inject
    EModelService modelService;
    
    @Inject
    MApplication application;
    
    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                MToolBar accountToolBar = (MToolBar) modelService.find(ToolbarProcessor.KATALON_ACCOUNT_ID, application);
                MHandledToolItem toolItem = (MHandledToolItem) modelService.find(ToolbarProcessor.KATALON_TOOLITEM_ACCOUNT_ID, accountToolBar);
                MMenu menuItem = toolItem.getMenu();

                MHandledMenuItem handlerMenuItem = (MHandledMenuItem) modelService.find(ToolbarProcessor.KATALON_MENUITEM_ACCOUNT_ID,
                        menuItem);
                
                String userName = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                
                if (userName == null || userName.isEmpty()) {
                    userName = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE);
                }
                
                handlerMenuItem.setLabel("Logged in as " + userName);
            }
        });
    }
}

