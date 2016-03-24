package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.ConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.MobileRunConfiguration;
import com.kms.katalon.execution.mobile.constants.StringConstants;
import com.kms.katalon.execution.mobile.driver.MobileDevice;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public abstract class MobileRunConfigurationContributor implements IRunConfigurationContributor {
    private DeviceNameConsoleOption deviceNameConsoleOption = new DeviceNameConsoleOption();

    @Override
    public String getId() {
        return getMobileDriverType().toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException,
            InterruptedException {
        if (deviceNameConsoleOption.getName().isEmpty()) {
            throw new ExecutionException(StringConstants.MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE);
        }
        String deviceName = deviceNameConsoleOption.getName();
        MobileRunConfiguration runConfiguration = getMobileRunConfiguration(projectDir);
        MobileDevice device = MobileExecutionUtil.getDevice(getMobileDriverType(), deviceName);
        if (device == null) {
            throw new ExecutionException(MessageFormat.format(
                    StringConstants.MOBILE_ERR_CANNOT_FIND_DEVICE_WITH_NAME_X, deviceName));
        }
        runConfiguration.setDevice(device);
        return runConfiguration;
    }

    protected abstract MobileRunConfiguration getMobileRunConfiguration(String projectDir) throws IOException;

    @Override
    public List<ConsoleOption<?>> getRequiredArguments() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(deviceNameConsoleOption);
        return consoleOptionList;
    }

    protected abstract MobileDriverType getMobileDriverType();
}
