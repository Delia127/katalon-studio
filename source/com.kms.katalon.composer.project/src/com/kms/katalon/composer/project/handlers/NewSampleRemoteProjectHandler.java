package com.kms.katalon.composer.project.handlers;

import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.dialog.NewProjectDialog;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.util.internal.JsonUtil;

public class NewSampleRemoteProjectHandler {

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
        NewProjectDialog dialog = new NewProjectDialog(activeShell, sampleProject);
        dialog.open();
    }

}
