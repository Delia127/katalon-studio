package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import static com.kms.katalon.core.mobile.constants.StringConstants.*;

import com.kms.katalon.execution.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.IosRunConfiguration;
import com.kms.katalon.execution.mobile.driver.MobileDevice;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public class IosRunConfigurationContributor implements IRunConfigurationContributor {
    public static final String MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE = "No mobile device name available";

    @Override
    public String getId() {
        return MobileDriverType.IOS_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir, Map<String, String> runInput)
            throws IOException, ExecutionException, InterruptedException {
        String deviceId = null;
        if (runInput != null) {
            deviceId = runInput.get(CONF_EXECUTED_DEVICE_ID);
        }
        IosRunConfiguration runConfiguration = new IosRunConfiguration(projectDir);
        MobileDevice device = null;
        if (!StringUtils.isBlank(deviceId)) {
            device = MobileExecutionUtil.getDevice(MobileDriverType.IOS_DRIVER, deviceId);
            runConfiguration.setDevice(device);
        }
        if (device == null) {
            throw new ExecutionException(StringConstants.MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE);
        }
        return runConfiguration;
    }
    @Override
    public int getPreferredOrder() {
        return 7;
    }
}
