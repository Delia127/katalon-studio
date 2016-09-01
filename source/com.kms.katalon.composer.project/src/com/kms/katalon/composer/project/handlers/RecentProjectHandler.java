package com.kms.katalon.composer.project.handlers;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("restriction")
public class RecentProjectHandler {
    @Inject
    EventBroker eventBroker;

    @Inject
    private EPartService partService;

    @Execute
    public void execute(@Optional @Named(IdConstants.OPEN_RECENT_PROJECT_COMMAND_PARAMETER_ID) String projectPk)
            throws IOException {
        if (projectPk != null) {
            if (ProjectController.getInstance().getCurrentProject() != null) {
                // Send a notice of switching project
                eventBroker.post(EventConstants.PROJECT_SAVE_SESSION, projectPk);
            }
            eventBroker.post(EventConstants.PROJECT_OPEN, projectPk);
        }
    }

    /**
     * Open last recent project when application started by listening to
     * {@link com.kms.katalon.controller.constants.EventConstants.WORKSPACE_CREATED}
     * 
     * @param object Can be any. This Object is not important.
     */
    @Inject
    @Optional
    private void openLastRecentProject(@UIEventTopic(EventConstants.WORKSPACE_CREATED) Object object) {
        if (ProjectController.getInstance().getCurrentProject() != null) return;

        try {
            List<ProjectEntity> recentProjects = ProjectController.getInstance().getRecentProjects();
            if (recentProjects == null || recentProjects.isEmpty()) return;

            if (PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION)) {
                // If the Tests Explorer part is minimized or hidden, we have to activate it.
                // So that the tree entities can be loaded.
                MPart explorerPart = partService.findPart(IdConstants.EXPLORER_PART_ID);
                if (explorerPart != null) partService.activate(explorerPart, true);

                // Open project
                eventBroker.post(EventConstants.PROJECT_OPEN, recentProjects.get(0).getId());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
