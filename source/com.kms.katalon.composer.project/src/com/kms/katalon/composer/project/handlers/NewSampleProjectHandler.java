package com.kms.katalon.composer.project.handlers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.greenrobot.eventbus.EventBus;

import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.ComposerProjectMessageConstants;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.composer.project.views.NewProjectDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.event.EventBusSingleton;
import com.kms.katalon.entity.project.ProjectEntity;

public class NewSampleProjectHandler {

    public static final Map<String, String> DIALOG_TITLES;

    private static final String SAMPLE_PROJECT_TYPE_PARAMETER_ID;

    static {
        DIALOG_TITLES = new HashMap<>();
        DIALOG_TITLES.put(SampleProjectProvider.SAMPLE_WEB_UI,
                ComposerProjectMessageConstants.VIEW_TITLE_NEW_SAMPLE_WEB_UI_PROJ);
        DIALOG_TITLES.put(SampleProjectProvider.SAMPLE_MOBILE,
                ComposerProjectMessageConstants.VIEW_TITLE_NEW_SAMPLE_MOBILE_PROJ);
        DIALOG_TITLES.put(SampleProjectProvider.SAMPLE_WEB_SERVICE,
                ComposerProjectMessageConstants.VIEW_TITLE_NEW_SAMPLE_WS_PROJ);

        SAMPLE_PROJECT_TYPE_PARAMETER_ID = "com.kms.katalon.composer.project.commandparameter.sample";

    }

    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(ParameterizedCommand command, @Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        try {
            String sampleProjectType = command.getCommand().getParameter(SAMPLE_PROJECT_TYPE_PARAMETER_ID).getName();
            doCreateNewSampleProject(activeShell, sampleProjectType, eventBroker);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public static void doCreateNewSampleProject(Shell activeShell, String sampleProjectType, IEventBroker eventBroker)
            throws Exception {
        NewProjectDialog dialog = new NewProjectDialog(activeShell, DIALOG_TITLES.get(sampleProjectType));
        if (dialog.open() != Dialog.OK) {
            return;
        }
        String projectName = dialog.getProjectName();
        String projectParentLocation = dialog.getProjectLocation();
        String projectDescription = dialog.getProjectDescription();

        String projectLocation = new File(projectParentLocation, projectName).getAbsolutePath();
        SampleProjectProvider.getInstance().extractSampleWebUIProject(sampleProjectType, projectLocation);
        FileUtils.forceDelete(ProjectController.getInstance().getProjectFile(projectLocation));

        ProjectEntity newProject = ProjectController.getInstance().newProjectEntity(projectName, projectDescription, 
                projectParentLocation, true);
        if (newProject == null) {
            return;
        }
        eventBroker.send(EventConstants.PROJECT_CREATED, newProject);
        sendEventForTracking(newProject, sampleProjectType);

        // Open created project
        eventBroker.send(EventConstants.PROJECT_OPEN, newProject.getId());
    }
    
    private static void sendEventForTracking(ProjectEntity project, String sampleProjectType) {
        EventBus eventBus = EventBusSingleton.getInstance().getEventBus();
        eventBus.post(new TrackingEvent(UsageActionTrigger.NEW_OBJECT, new HashMap<String, Object>() {{
            put("type", "project");
            put("sampleProjectId", project.getUUID());
            put("sampleProjectType", sampleProjectType);
        }}));
    }
}
