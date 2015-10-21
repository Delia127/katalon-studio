package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.execution.webui.constants.StringConstants;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebRunConfigurationContributor implements IRunConfigurationContributor {

    @Override
    public String getId() {
        return WebUIDriverType.REMOTE_WEB_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput)
            throws IOException, ExecutionException {
        String remoteWebDriverUrl = null;
        RemoteWebDriverConnectorType remoteWebDriverType = null;
        if (runInput != null) {
            remoteWebDriverUrl = (runInput.get(DriverFactory.REMOTE_WEB_DRIVER_URL) instanceof String) ? (String) runInput
                    .get(DriverFactory.REMOTE_WEB_DRIVER_URL) : null;
            remoteWebDriverType = (runInput.get(DriverFactory.REMOTE_WEB_DRIVER_TYPE) instanceof String) ? RemoteWebDriverConnectorType
                    .valueOf((String) runInput.get(DriverFactory.REMOTE_WEB_DRIVER_URL)) : null;
        }
        RemoteWebRunConfiguration runConfiguration = new RemoteWebRunConfiguration(testCase);
        if (remoteWebDriverUrl != null) {
            runConfiguration.setRemoteServerUrl(remoteWebDriverUrl);
        }
        if (remoteWebDriverType != null) {
            runConfiguration.setRemoteWebDriverConnectorType(remoteWebDriverType);
        }
        if (runConfiguration.getRemoteServerUrl() == null || runConfiguration.getRemoteServerUrl().isEmpty()) {
            throw new ExecutionException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_URL_AVAILABLE);
        }
        return runConfiguration;
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput)
            throws IOException, ExecutionException {
        String remoteWebDriverUrl = null;
        RemoteWebDriverConnectorType remoteWebDriverType = null;
        if (runInput != null) {
            remoteWebDriverUrl = (runInput.get(DriverFactory.REMOTE_WEB_DRIVER_URL) instanceof String) ? (String) runInput
                    .get(DriverFactory.REMOTE_WEB_DRIVER_URL) : null;
            remoteWebDriverType = (runInput.get(DriverFactory.REMOTE_WEB_DRIVER_TYPE) instanceof String) ? RemoteWebDriverConnectorType
                    .valueOf((String) runInput.get(DriverFactory.REMOTE_WEB_DRIVER_URL)) : null;
        }
        RemoteWebRunConfiguration runConfiguration = new RemoteWebRunConfiguration(testSuite);
        if (remoteWebDriverUrl != null) {
            runConfiguration.setRemoteServerUrl(remoteWebDriverUrl);
        }
        if (remoteWebDriverType != null) {
            runConfiguration.setRemoteWebDriverConnectorType(remoteWebDriverType);
        }
        if (runConfiguration.getRemoteServerUrl() == null || runConfiguration.getRemoteServerUrl().isEmpty()) {
            throw new ExecutionException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_URL_AVAILABLE);
        }
        return runConfiguration;
    }

}
