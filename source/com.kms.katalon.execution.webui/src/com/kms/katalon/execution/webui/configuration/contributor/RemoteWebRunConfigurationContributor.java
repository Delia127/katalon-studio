package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.entity.AbstractConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.execution.webui.constants.StringConstants;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebRunConfigurationContributor extends WebUIRunConfigurationContributor {
    public static final String REMOTE_CONFIGURATION_KEY = DriverFactory.REMOTE_WEB_DRIVER_URL;

    public static final String REMOTE_CONFIGURATION_TYPE_KEY = DriverFactory.REMOTE_WEB_DRIVER_TYPE;

    private static final RemoteWebDriverConnectorType DEFAULT_REMOTE_WEB_DRIVER_CONNECTOR_TYPE = RemoteWebDriverConnectorType.Selenium;

    private String remoteWebDriverUrl = "";

    private RemoteWebDriverConnectorType remoteWebDriverType = DEFAULT_REMOTE_WEB_DRIVER_CONNECTOR_TYPE;

    public static final StringConsoleOption REMOTE_WEB_DRIVER_URL_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return DriverFactory.REMOTE_WEB_DRIVER_URL;
        }

        @Override
        public boolean isRequired() {
            return false;
        };
    };

    public static final ConsoleOption<RemoteWebDriverConnectorType> REMOTE_WEB_DRIVER_CONNECTOR_TYPE_CONSOLE_OPTION = new AbstractConsoleOption<RemoteWebDriverConnectorType>() {
        @Override
        public String getOption() {
            return DriverFactory.REMOTE_WEB_DRIVER_TYPE;
        }

        @Override
        public Class<RemoteWebDriverConnectorType> getArgumentType() {
            return RemoteWebDriverConnectorType.class;
        }

        @Override
        public String getDefaultArgumentValue() {
            return DEFAULT_REMOTE_WEB_DRIVER_CONNECTOR_TYPE.toString();
        }

        @Override
        public boolean isRequired() {
            return false;
        };
    };

    @Override
    public String getId() {
        return WebUIDriverType.REMOTE_WEB_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException {
        RemoteWebRunConfiguration runConfiguration = new RemoteWebRunConfiguration(projectDir);

        remoteWebDriverUrl = StringUtils.isNotBlank(remoteWebDriverUrl) ? remoteWebDriverUrl
                : runConfiguration.getRemoteServerUrl();
        remoteWebDriverType = remoteWebDriverType != null ? remoteWebDriverType
                : runConfiguration.getRemoteWebDriverConnectorType();

        if (StringUtils.isBlank(remoteWebDriverUrl)) {
            throw new ExecutionException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_URL_AVAILABLE);
        }
        if (remoteWebDriverType == null) {
            throw new ExecutionException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_TYPE_AVAILABLE);
        }

        runConfiguration.setRemoteServerUrl(remoteWebDriverUrl);
        runConfiguration.setRemoteWebDriverConnectorType(remoteWebDriverType);
        return runConfiguration;
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir,
            RunConfigurationDescription runConfigurationDescription)
            throws IOException, ExecutionException, InterruptedException {
        if (runConfigurationDescription != null && runConfigurationDescription.getRunConfigurationData() != null) {
            Map<String, String> runConfigurationData = runConfigurationDescription.getRunConfigurationData();
            remoteWebDriverUrl = runConfigurationData.get(REMOTE_CONFIGURATION_KEY);
            remoteWebDriverType = RemoteWebDriverConnectorType
                    .valueOf(runConfigurationData.get(REMOTE_CONFIGURATION_TYPE_KEY));
        }
        return getRunConfiguration(projectDir);
    }

    @Override
    public int getPreferredOrder() {
        return 5;
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(REMOTE_WEB_DRIVER_URL_CONSOLE_OPTION);
        consoleOptionList.add(REMOTE_WEB_DRIVER_CONNECTOR_TYPE_CONSOLE_OPTION);
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (StringUtils.isBlank(argumentValue)) {
            return;
        }
        if (consoleOption == REMOTE_WEB_DRIVER_URL_CONSOLE_OPTION) {
            remoteWebDriverUrl = argumentValue.trim();
            return;
        }
        if (consoleOption == REMOTE_WEB_DRIVER_CONNECTOR_TYPE_CONSOLE_OPTION) {
            remoteWebDriverType = RemoteWebDriverConnectorType.valueOf(argumentValue);
        }
    }
}
