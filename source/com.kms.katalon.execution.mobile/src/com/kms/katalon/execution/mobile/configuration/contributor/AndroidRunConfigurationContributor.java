package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.MobileRunConfiguration;

public class AndroidRunConfigurationContributor extends MobileRunConfigurationContributor {
    @Override
    public int getPreferredOrder() {
        return 6;
    }

    @Override
    protected MobileRunConfiguration getMobileRunConfiguration(String projectDir) throws IOException {
        return new AndroidRunConfiguration(projectDir);
    }

    @Override
    protected MobileDriverType getMobileDriverType() {
        return MobileDriverType.ANDROID_DRIVER;
    }
}
