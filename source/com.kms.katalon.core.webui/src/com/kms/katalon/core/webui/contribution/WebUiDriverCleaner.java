package com.kms.katalon.core.webui.contribution;

import com.kms.katalon.core.driver.IDriverCleaner;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebMobileDriverFactory;

public class WebUiDriverCleaner implements IDriverCleaner {

    @Override
    public void cleanDriverAfterRunningTestCase() {
        WebMobileDriverFactory.quitServer();
    }

    @Override
    public void cleanDriverAfterRunningTestSuite() {
        DriverFactory.closeWebDriver();
    }

}
