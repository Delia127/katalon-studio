package com.kms.katalon.execution.webui.configuration.contributor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.AbstractConsoleOption;
import com.kms.katalon.execution.entity.ConsoleOption;
import com.kms.katalon.execution.entity.StringConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.execution.webui.constants.StringConstants;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebRunConfigurationContributor extends WebUIRunConfigurationContributor {
    private String remoteWebDriverUrl = "";
    private RemoteWebDriverConnectorType remoteWebDriverType = RemoteWebDriverConnectorType.Selenium;

    private StringConsoleOption remoteWebDriverUrlConsoleOption = new StringConsoleOption() {
        @Override
        public void setArgumentValue(String value) {
            if (StringUtils.isBlank(value)) {
                return;
            }
            remoteWebDriverUrl = value;
        }

        @Override
        public String getOption() {
            return DriverFactory.REMOTE_WEB_DRIVER_URL;
        }
    };

    private AbstractConsoleOption<RemoteWebDriverConnectorType> remoteWebDriverTypeConsoleOption = new AbstractConsoleOption<RemoteWebDriverConnectorType>() {
        @Override
        public void setArgumentValue(String value) {
            if (StringUtils.isBlank(value)) {
                return;
            }
            remoteWebDriverType = RemoteWebDriverConnectorType.valueOf(value);
        }

        @Override
        public boolean hasArgument() {
            return true;
        }

        @Override
        public String getOption() {
            return DriverFactory.REMOTE_WEB_DRIVER_TYPE;
        }

        @Override
        public Class<RemoteWebDriverConnectorType> getArgumentType() {
            return RemoteWebDriverConnectorType.class;
        }
    };

    @Override
    public String getId() {
        return WebUIDriverType.REMOTE_WEB_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException {
        if (StringUtils.isBlank(remoteWebDriverUrl)) {
            throw new ExecutionException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_URL_AVAILABLE);
        }
        if (remoteWebDriverType == null) {
            throw new ExecutionException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_TYPE_AVAILABLE);
        }
        RemoteWebRunConfiguration runConfiguration = new RemoteWebRunConfiguration(projectDir);
        runConfiguration.setRemoteServerUrl(remoteWebDriverUrl);
        runConfiguration.setRemoteWebDriverConnectorType(remoteWebDriverType);
        return runConfiguration;
    }

    @Override
    public int getPreferredOrder() {
        return 5;
    }

    @Override
    public List<ConsoleOption<?>> getRequiredArguments() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(remoteWebDriverUrlConsoleOption);
        consoleOptionList.add(remoteWebDriverTypeConsoleOption);
        return consoleOptionList;
    }
}
