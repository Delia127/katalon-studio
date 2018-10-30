package com.kms.katalon.composer.project.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.project.constants.ComposerProjectMessageConstants;
import com.kms.katalon.composer.project.dialog.NewProjectDialog;
import com.kms.katalon.composer.project.sample.SampleLocalProject;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.util.internal.JsonUtil;

public class NewSampleLocalProjectHandler {

    public static final Map<String, String> DIALOG_TITLES;

    static {
        DIALOG_TITLES = new HashMap<>();
        DIALOG_TITLES.put(SampleProjectProvider.SAMPLE_WEB_UI,
                ComposerProjectMessageConstants.VIEW_TITLE_NEW_SAMPLE_WEB_UI_PROJ);
        DIALOG_TITLES.put(SampleProjectProvider.SAMPLE_MOBILE,
                ComposerProjectMessageConstants.VIEW_TITLE_NEW_SAMPLE_MOBILE_PROJ);
        DIALOG_TITLES.put(SampleProjectProvider.SAMPLE_WEB_SERVICE,
                ComposerProjectMessageConstants.VIEW_TITLE_NEW_SAMPLE_WS_PROJ);
    }

    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(ParameterizedCommand command, @Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
//        String sampleProjectType = (String) command.getParameterMap()
//                .get(IdConstants.NEW_LOCAL_PROJECT_COMMAND_PARAMETER_TYPE_ID);
        String sampleLocalProjectJson = (String) command.getParameterMap()
              .get(IdConstants.NEW_LOCAL_PROJECT_COMMAND_PARAMETER_TYPE_ID);
        doCreateNewSampleProject(
                activeShell,
                JsonUtil.fromJson(sampleLocalProjectJson, SampleLocalProject.class),
                eventBroker);
    }

    public static void doCreateNewSampleProject(Shell activeShell, SampleLocalProject sampleProject, IEventBroker eventBroker) {
        NewProjectDialog dialog = new NewProjectDialog(activeShell, sampleProject);
        dialog.open();
    }
}
