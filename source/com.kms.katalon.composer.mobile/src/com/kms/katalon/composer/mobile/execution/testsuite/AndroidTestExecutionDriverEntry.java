package com.kms.katalon.composer.mobile.execution.testsuite;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.configuration.contributor.MobileRunConfigurationContributor;

public class AndroidTestExecutionDriverEntry extends MobileTestExecutionDriverEntry {
    public AndroidTestExecutionDriverEntry(final String groupName) {
        super(MobileDriverType.ANDROID_DRIVER, groupName, ImageConstants.IMG_URL_16_ANDROID);
    }

    @Override
    public String displayRunConfigurationData(Map<String, String> runConfigurationData) {
        if (runConfigurationData == null) {
            return StringUtils.EMPTY;
        }
        return runConfigurationData.getOrDefault(MobileRunConfigurationContributor.DEVICE_DISPLAY_NAME_CONFIGURATION_KEY,
                StringUtils.EMPTY);
    }
}
