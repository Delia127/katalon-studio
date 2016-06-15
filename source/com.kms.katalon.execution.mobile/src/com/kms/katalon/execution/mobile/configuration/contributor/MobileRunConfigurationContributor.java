package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
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
    private String deviceName;

    public static final StringConsoleOption DEVICE_NAME_CONSOLE_OPTION = new StringConsoleOption() {
        @Override
        public String getOption() {
            return com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_ID;
        }
        
        @Override
        public boolean isRequired() {
            return false;
        };
    };

    @Override
    public String getId() {
        return getMobileDriverType().toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir) throws IOException, ExecutionException,
            InterruptedException {
        deviceName = StringUtils.isNotBlank(deviceName) ? deviceName : getDefaultDeviceId(projectDir,
                getMobileDriverType());
        if (StringUtils.isBlank(deviceName)) {
            throw new ExecutionException(StringConstants.MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE);
        }
        MobileRunConfiguration runConfiguration = getMobileRunConfiguration(projectDir);
        MobileDeviceInfo device = null;
        try {   
            device = MobileDeviceProvider.getDevice(getMobileDriverType(), deviceName);
        } catch (MobileSetupException e) {
            throw new ExecutionException(e.getMessage());
        }
        if (device == null) {
            throw new ExecutionException(MessageFormat.format(
                    StringConstants.MOBILE_ERR_CANNOT_FIND_DEVICE_WITH_NAME_X, deviceName));
        }
        runConfiguration.setDevice(device);
        return runConfiguration;
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
                deviceId = new AndroidDriverConnector(projectDir + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME).getDefaultDeviceId();
                break;
            }
            case IOS_DRIVER: {
                deviceId = new IosDriverConnector(projectDir + File.separator
                        + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME).getDefaultDeviceId();
                break;
            }
        }
        return deviceId;
    }
}
