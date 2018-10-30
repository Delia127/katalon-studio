package com.kms.katalon.composer.webservice.handlers;

import java.io.IOException;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.greenrobot.eventbus.Subscribe;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.execution.configuration.BasicRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.event.ExecutionEvent;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class WebServiceExecutionHandler extends AbstractExecutionHandler {

    @Inject
    protected static IEventBroker eventBroker;

    @Subscribe
    public void doExecute(ExecutionEvent event) {
        if (event.getTopic().equals(EventConstants.WEBSERVICE_EXECUTE)) {
            LaunchMode launchMode = (LaunchMode) event.getData();
            try {
                WebServiceExecutionHandler.this.execute(launchMode);
            } catch (Exception e) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                        MessageFormat.format(StringConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_TEST_SCRIPT_ROOT_CAUSE,
                                e.getMessage()));
                LoggerSingleton.logError(e);
            }
        }
    }

    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new BasicRunConfiguration();
    }

}
