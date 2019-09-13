package com.kms.katalon.composer.project.handlers;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class CloseProjectHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.PROJECT_CLOSE, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                eventBroker.send(EventConstants.PROJECT_SAVE_SESSION, null);
                execute(Display.getCurrent().getActiveShell());
            }

        });
    }

    public static boolean closeCurrentProject(EPartService partService, EModelService modelService,
            MApplication application, IEventBroker eventBroker) {
        final ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if (project != null) {
            if (partService.saveAll(true)) {
                closeProject(partService, eventBroker, project);

                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null
                && !LauncherManager.getInstance().isAnyLauncherRunning();
    }

    // TODO: Need to keep part stack in eclipse context
    @Execute
    public void execute(Shell shell) {
        if (partService.saveAll(true)) {
            closeProject(partService, eventBroker, ProjectController.getInstance().getCurrentProject());

            eventBroker.send(EventConstants.EXPLORER_RELOAD_INPUT, Collections.emptyList());
            eventBroker.send(EventConstants.GLOBAL_VARIABLE_REFRESH, null);
            eventBroker.post(EventConstants.CONSOLE_LOG_RESET, null);

            // minimize console part stack
            MPartStack consolePartStack = (MPartStack) modelService.find(IdConstants.CONSOLE_PART_STACK_ID,
                    application);
            consolePartStack.getTags().add(IPresentationEngine.MINIMIZED);

            // minimize job progress
            MPartStack rightPartStack = (MPartStack) modelService.find(IdConstants.OUTLINE_PARTSTACK_ID, application);
            rightPartStack.getTags().add(IPresentationEngine.MINIMIZED);

            // open welcome page
            partService.activate((MPart) modelService.find(IdConstants.WELCOME_PART_ID, application));
            
            //update window title
            MWindow win = (MWindow) modelService.find(IdConstants.MAIN_WINDOW_ID, application);
            win.setLabel(GlobalStringConstants.APP_NAME);
        }
    }

    private static void closeProject(EPartService partService, IEventBroker eventBroker, ProjectEntity project) {

        LauncherManager.getInstance().removeAllTerminated();
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(false);
        // Find and close all opened editor parts which is managed by PartService
        for (MPart p : partService.getParts()) {
            if (p.getElementId().startsWith("com.kms.katalon.composer.content.") && p.getElementId().endsWith(")")
                    || "org.eclipse.e4.ui.compatibility.editor".equals(p.getElementId())) {
                partService.hidePart(p, true);
            }
        }

        try {
            if (project != null) {
                ProjectController.getInstance().closeProject(project.getId(), null);
                eventBroker.send(EventConstants.PROJECT_CLOSED, project.getId());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    StringConstants.HAND_WARN_MSG_UNABLE_TO_CLOSE_CURRENT_PROJ);
        }
    }
}
