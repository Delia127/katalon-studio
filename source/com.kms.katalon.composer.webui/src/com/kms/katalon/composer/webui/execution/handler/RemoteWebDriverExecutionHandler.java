package com.kms.katalon.composer.webui.execution.handler;

import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteWebDriverExecutionHandler extends AbstractExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
        if (testCase == null) {
            return null;
        }
        String remoteWebDriverServerUrl = getDefaultRemoteWebDriverServerUrl(testCase);
        if (remoteWebDriverServerUrl == null || remoteWebDriverServerUrl.isEmpty()) {
            remoteWebDriverServerUrl = getRemoteWebDriverServerUrl();
            if (remoteWebDriverServerUrl != null && !remoteWebDriverServerUrl.isEmpty()) {
                return new RemoteWebRunConfiguration(testCase, remoteWebDriverServerUrl);
            }
        } else {
            return new RemoteWebRunConfiguration(testCase);
        }
        return null;
    }

    private String getDefaultRemoteWebDriverServerUrl(FileEntity fileEntity) {
        RemoteWebDriverConnector remoteWebDriverConnector = null;
        try {
            remoteWebDriverConnector = new RemoteWebDriverConnector(fileEntity.getProject().getFolderLocation());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }

        if (remoteWebDriverConnector != null) {
            return remoteWebDriverConnector.getRemoteServerUrl();
        }
        return null;
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

    protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
        if (testSuite == null) {
            return null;
        }
        String remoteWebDriverServerUrl = getDefaultRemoteWebDriverServerUrl(testSuite);
        if (remoteWebDriverServerUrl == null || remoteWebDriverServerUrl.isEmpty()) {
            remoteWebDriverServerUrl = getRemoteWebDriverServerUrl();
            if (remoteWebDriverServerUrl != null && !remoteWebDriverServerUrl.isEmpty()) {
                return new RemoteWebRunConfiguration(testSuite, remoteWebDriverServerUrl);
            }
        } else {
            return new RemoteWebRunConfiguration(testSuite);
        }
        return null;
    }
}