package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import static com.kms.katalon.execution.mobile.constants.StringConstants.*;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;
import com.kms.katalon.execution.mobile.driver.MobileDevice;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public class AndroidRunConfigurationContributor implements IRunConfigurationContributor {

    @Override
    public String getId() {
        return MobileDriverType.ANDROID_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(String projectDir, Map<String, String> runInput)
            throws IOException, ExecutionException, InterruptedException {
        String deviceId = null;
        if (runInput != null) {
            deviceId = runInput.get(StringConstants.CONF_EXECUTED_DEVICE_ID);
        }
        AndroidRunConfiguration runConfiguration = new AndroidRunConfiguration(projectDir);
        MobileDevice device = MobileExecutionUtil.getAndroidDevices().get(deviceId);        
        if (StringUtils.isBlank(deviceId)) {
            throw new ExecutionException(MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE);
        }
        runConfiguration.setDevice(device);
        return runConfiguration;
    }
    
    @Override
    public int getPreferredOrder() {
        return 6;
    }

}
