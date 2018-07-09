package com.kms.katalon.composer.project.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.sample.NewSampleRemoteProjectDialog;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.util.internal.JsonUtil;

public class NewSampleRemoteProjectHandler {

    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(ParameterizedCommand command, @Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        try {
            String sampleRemoteProjectJson = (String) command.getParameterMap()
                    .get(IdConstants.NEW_REMOTE_PROJECT_COMMAND_PARAMETER_ID);
            doCreateNewRemoteSampleProject(activeShell,
                    JsonUtil.fromJson(sampleRemoteProjectJson, SampleRemoteProject.class));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void doCreateNewRemoteSampleProject(Shell activeShell, SampleRemoteProject sampleProject) {
        NewSampleRemoteProjectDialog dialog = new NewSampleRemoteProjectDialog(activeShell, sampleProject);
        if (dialog.open() != NewSampleRemoteProjectDialog.OK) {
            return;
        }
        String projectLocation = dialog.getSelectedProjectLocation();
        eventBroker.post(EventConstants.GIT_CLONE_REMOTE_PROJECT, new Object[] { sampleProject, projectLocation });
    }

}
