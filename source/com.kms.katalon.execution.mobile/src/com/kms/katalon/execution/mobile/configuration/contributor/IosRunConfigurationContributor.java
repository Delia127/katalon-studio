package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.IosRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.MobileRunConfiguration;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public class IosRunConfigurationContributor extends MobileRunConfigurationContributor {
    @Override
    public int getPreferredOrder() {
        return 7;
    }
    
    @Override
    public IRunConfiguration getRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        MobileExecutionUtil.detectInstalledAppiumAndNodeJs();
        return super.getRunConfiguration(projectDir);
    }

    @Override
    protected MobileRunConfiguration getMobileRunConfiguration(String projectDir) throws IOException {
        return new IosRunConfiguration(projectDir);
    }

    @Override
    protected MobileDriverType getMobileDriverType() {
        return MobileDriverType.IOS_DRIVER;
    }
}
