package com.kms.katalon.core.webui.contribution;

import com.kms.katalon.core.driver.IDriverCleaner;
import com.kms.katalon.core.webui.driver.DriverFactory;

public class WebUiDriverCleaner implements IDriverCleaner{

	@Override
	public void cleanDriver() {
		// TODO Auto-generated method stub
		DriverFactory.closeWebDriver();
	}

}
