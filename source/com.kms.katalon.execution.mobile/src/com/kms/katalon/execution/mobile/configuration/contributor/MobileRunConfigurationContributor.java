package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.appium.constants.AppiumStringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.StringConsoleOption;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.MobileRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.constants.StringConstants;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public abstract class MobileRunConfigurationContributor implements IRunConfigurationContributor {
    public static final String DEVICE_ID_CONFIGURATION_KEY = "deviceId";

    public static final String DEVICE_DISPLAY_NAME_CONFIGURATION_KEY = "deviceName";

    private String deviceName;

    public static final StringConsoleOption DEVICE_NAME_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return com.kms.katalon.core.appium.constants.AppiumStringConstants.CONF_EXECUTED_DEVICE_ID;
        }
    };

    @Override
    public String getId() {
        return getMobileDriverType().toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        deviceName = StringUtils.isNotBlank(deviceName) ? deviceName
                : getDefaultDeviceId(projectDir, getMobileDriverType());
        if (StringUtils.isBlank(deviceName)) {
            throw new ExecutionException(StringConstants.MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE);
        }
        MobileDeviceInfo device = null;
        try {
            device = MobileDeviceProvider.getDevice(getMobileDriverType(), deviceName);
        } catch (MobileSetupException e) {
            throw new ExecutionException(e.getMessage());
        }
        if (device == null) {
            throw new ExecutionException(
                    MessageFormat.format(StringConstants.MOBILE_ERR_CANNOT_FIND_DEVICE_WITH_NAME_X, deviceName));
        }
        MobileRunConfiguration runConfiguration = getMobileRunConfiguration(projectDir);
        runConfiguration.setDevice(device);
        return runConfiguration;
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir,
            RunConfigurationDescription runConfigurationDescription)
            throws IOException, ExecutionException, InterruptedException {
        deviceName = getDeviceId(runConfigurationDescription);
        return IRunConfigurationContributor.super.getRunConfiguration(projectDir, runConfigurationDescription);
    }

    private String getDeviceId(RunConfigurationDescription runConfigurationDescription) {
        if (runConfigurationDescription != null && runConfigurationDescription.getRunConfigurationData() != null) {
            return runConfigurationDescription.getRunConfigurationData().get(DEVICE_ID_CONFIGURATION_KEY);
        }
        return StringUtils.EMPTY;
    }

    protected abstract MobileRunConfiguration getMobileRunConfiguration(String projectDir) throws IOException;

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
        consoleOptionList.add(DEVICE_NAME_CONSOLE_OPTION);
        return consoleOptionList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (StringUtils.isBlank(argumentValue)) {
            return;
        }
        if (consoleOption == DEVICE_NAME_CONSOLE_OPTION) {
            deviceName = argumentValue.trim();
        }
    }

    protected abstract MobileDriverType getMobileDriverType();

    public static String getDefaultDeviceId(String projectDir, MobileDriverType platform) throws IOException {
        String deviceId = null;
        switch (platform) {
            case ANDROID_DRIVER: {
                deviceId = new AndroidDriverConnector(
                        projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME)
                                .getDefaultDeviceId();
                break;
            }
            case IOS_DRIVER: {
                deviceId = new IosDriverConnector(
                        projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME)
                                .getDefaultDeviceId();
                break;
            }
        }
        return deviceId;
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptions(RunConfigurationDescription description) {
        return Arrays.asList(new StringConsoleOption() {
            @Override
            public String getOption() {
                return AppiumStringConstants.CONF_EXECUTED_DEVICE_ID;
            }

            @Override
            public String getValue() {
                return getDeviceId(description);
            }
        });
    }
}
