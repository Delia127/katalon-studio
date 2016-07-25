package com.kms.katalon.core.mobile.contribution;

import com.kms.katalon.core.driver.IDriverCleaner;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;

public class MobileDriverCleaner implements IDriverCleaner{

	@Override
	public void cleanDriverAfterRunningTestCase() {
	    cleanDriverAfterRunningTestSuite();
	}

    @Override
    public void cleanDriverAfterRunningTestSuite() {
        MobileDriverFactory.closeDriver();
    }

}
