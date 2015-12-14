package com.kms.katalon.core.mobile.contribution;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.kms.katalon.core.driver.IDriverCleaner;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;

public class MobileDriverCleaner implements IDriverCleaner{

	@Override
	public void cleanDriverAfterRunningTestCase() {
        MobileDriverFactory.quitServer();
	}

    @Override
    public void cleanDriverAfterRunningTestSuite() {
        cleanDriverAfterRunningTestCase();
        if (null != MobileDriverFactory.getDriver() && null != ((RemoteWebDriver) MobileDriverFactory.getDriver()).getSessionId()) {
            MobileDriverFactory.getDriver().quit();
        }
    }

}
