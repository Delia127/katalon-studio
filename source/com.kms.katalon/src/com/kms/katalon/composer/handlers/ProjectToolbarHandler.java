package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;
import org.openqa.selenium.Platform;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.processors.ToolbarProcessor;
import com.kms.katalon.constants.IdConstants;

public class ProjectToolbarHandler {
    
    
    @Inject
    IEventBroker eventBroker;
    
    @Inject
    EModelService modelService;
    
    @Inject
    MApplication application;
    
    @PostConstruct
    public void registerEventListener() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventServiceAdapter() {
            
            @Override
            public void handleEvent(Event event) {
                ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                MUIElement genericToolbar = modelService.find(ToolbarProcessor.KATALON_MAIN_GENERIC_TOOLBAR_ID, application);
                MUIElement webserviceToolbar = modelService.find(ToolbarProcessor.KATALON_MAIN_WEBSERVICE_TOOLBAR_ID, application);
                setToolTipExecution();          
                if (currentProject.getType() == ProjectType.WEBSERVICE) {
                    if (genericToolbar != null) {
                        genericToolbar.setVisible(false);
                        genericToolbar.setToBeRendered(false);
                    }
                    if (webserviceToolbar != null) {
                        webserviceToolbar.setVisible(true);
                        webserviceToolbar.setToBeRendered(true);
                    }
                } else {
                    if (genericToolbar != null) {
                        genericToolbar.setVisible(true);
                        genericToolbar.setToBeRendered(true);
                    }
                    if (webserviceToolbar != null) {
                        webserviceToolbar.setVisible(false);
                        webserviceToolbar.setToBeRendered(false);
                    }
                }
            }
        });
    }
    
    private void setToolTipExecution() {
        if (Platform.getCurrent().is(Platform.MAC)) {
            MToolBar executionToolBar = (MToolBar) modelService.find(ToolbarProcessor.KATALON_EXECUTION_TOOLBAR_ID,
                    application);
            MHandledToolItem executionToolItemRun = (MHandledToolItem) modelService.find(IdConstants.RUN_TOOL_ITEM_ID,
                    executionToolBar);
            executionToolItemRun.setLabel("Run (Command + Shift + A)");
            MHandledToolItem executionToolItemDebug = (MHandledToolItem) modelService
                    .find(IdConstants.DEBUG_TOOL_ITEM_ID, executionToolBar);
            executionToolItemDebug.setLabel("Run (Command + Shift + D)");
        } else if (Platform.getCurrent().is(Platform.WINDOWS)) {
            MToolBar executionToolBar = (MToolBar) modelService.find(ToolbarProcessor.KATALON_EXECUTION_TOOLBAR_ID,
                    application);
            MHandledToolItem executionToolItemRun = (MHandledToolItem) modelService.find(IdConstants.RUN_TOOL_ITEM_ID,
                    executionToolBar);
            executionToolItemRun.setLabel("Run (Ctrl  + Shift + A)");
            MHandledToolItem executionToolItemDebug = (MHandledToolItem) modelService
                    .find(IdConstants.DEBUG_TOOL_ITEM_ID, executionToolBar);
            executionToolItemDebug.setLabel("Run (Ctrl + Shift + D)");
        }
    }

}
