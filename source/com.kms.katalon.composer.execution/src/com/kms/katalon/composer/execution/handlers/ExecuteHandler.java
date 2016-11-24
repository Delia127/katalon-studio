package com.kms.katalon.composer.execution.handlers;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.util.ExecutionUtil;

public class ExecuteHandler extends AbstractExecutionHandler {

    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException {
        return null;
    }

    @Execute
    public void execute(ParameterizedCommand command) {
        try {
            IRunConfigurationContributor defaultRunContributor = ExecutionUtil.getDefaultExecutionConfiguration();
            if (defaultRunContributor == null) {
                return;
            }
            String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();

            IRunConfiguration runConfiguration = defaultRunContributor.getRunConfiguration(projectDir);
            if (runConfiguration == null) {
                return;
            }
            execute(getLaunchMode(command), runConfiguration);
        } catch (ExecutionException e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR, e.getMessage());
        } catch (SWTException e) {
            // Ignore it
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR, MessageFormat
                    .format(StringConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_TEST_SCRIPT_ROOT_CAUSE, e.getMessage()));
            LoggerSingleton.logError(e);
        }
    }
}
