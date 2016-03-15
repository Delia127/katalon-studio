package com.kms.katalon.composer.webui.execution.handler;

import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;

public class RemoteWebDriverExecutionHandler extends AbstractExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException {
        RemoteWebRunConfiguration runConfiguration = new RemoteWebRunConfiguration(projectDir);
        if (runConfiguration.getRemoteServerUrl() == null || runConfiguration.getRemoteServerUrl().isEmpty()) {
            String remoteWebDriverServerUrl = getRemoteWebDriverServerUrl();
            if (remoteWebDriverServerUrl != null && !remoteWebDriverServerUrl.isEmpty()) {
                runConfiguration.setRemoteServerUrl(remoteWebDriverServerUrl);
                return runConfiguration;
            } else {
                return null;
            }
        } else {
            return runConfiguration;
        }
    }

    private String getRemoteWebDriverServerUrl() {
        InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(),
                StringConstants.DIA_REMOTE_SERVER_URL_TITLE, StringConstants.DIA_REMOTE_SERVER_URL_MESSAGE, null, null);
        int returnValue = dialog.open();
        if (returnValue == Dialog.OK) {
            return dialog.getValue();
        }
        return null;
    }
}