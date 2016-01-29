package com.kms.katalon.composer.project.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

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
                closeCurrentProject(partService, modelService, application, eventBroker);
            }

        });
    }

    public static boolean closeCurrentProject(EPartService partService, EModelService modelService,
            MApplication application, IEventBroker eventBroker) {
        final ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if (project != null && partService.getActivePart() != null) {
            if (partService.saveAll(true)) {
                saveOpenedEntitiesState(partService, project);

                MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                        application);

                while (composerStack.getChildren().size() > 0) {
                    MPart mpart = (MPart) composerStack.getChildren().get(0);
                    partService.hidePart(mpart, true);
                }
                try {
                    if (project != null) {
                        ProjectController.getInstance().closeProject(project.getId(), null);
                        eventBroker.send(EventConstants.PROJECT_CLOSED, project.getId());
                    }
                } catch (Exception e) {
                    MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                            StringConstants.HAND_WARN_MSG_UNABLE_TO_CLOSE_CURRENT_PROJ);
                }
                return true;
            } else {
                return false;
            }
        }
        return true;

    }

    /**
     * Save all opened entities for next working session
     * 
     * @see com.kms.katalon.composer.explorer.parts.ExplorerPart#restoreOpenedEntitiesState()
     * @param partService
     * @param project
     */
    private static void saveOpenedEntitiesState(EPartService partService, ProjectEntity project) {
        try {
            List<ProjectEntity> recentProjects = ProjectController.getInstance().getRecentProjects();
            if (recentProjects != null && !recentProjects.isEmpty()) {
                recentProjects.get(0).setRecentOpenedTreeEntityIds(
                        EntityPartUtil.getOpenedEntityIds(partService.getParts()));
                ProjectController.getInstance().saveRecentProjects(recentProjects);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
