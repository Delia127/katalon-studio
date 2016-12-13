package com.kms.katalon.core.mobile.contribution;

import com.kms.katalon.core.driver.internal.IDriverCleaner;
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory;

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
