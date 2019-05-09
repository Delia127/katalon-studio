package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.MobileRunConfiguration;
import com.kms.katalon.execution.mobile.device.AndroidSDKDownloadManager;
import com.kms.katalon.execution.mobile.device.AndroidSDKManager;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public class AndroidRunConfigurationContributor extends MobileRunConfigurationContributor {
    @Override
    public int getPreferredOrder() {
        return 6;
    }
    
    @Override
    public IRunConfiguration getRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        MobileExecutionUtil.detectInstalledAppiumAndNodeJs();
        AndroidSDKManager sdkManager = new AndroidSDKManager();
        if (!sdkManager.checkSDKExists()) {
            AndroidSDKDownloadManager downloadManager = new AndroidSDKDownloadManager(sdkManager.getSDKLocator());
            downloadManager.downloadAndInstall();
        }
        return super.getRunConfiguration(projectDir);
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
