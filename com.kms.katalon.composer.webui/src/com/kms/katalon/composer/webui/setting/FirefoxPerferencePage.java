package com.kms.katalon.composer.webui.setting;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class FirefoxPerferencePage extends BrowserPreferencePage {
	@Override
	protected DriverType getDriverType() {
		return WebUIDriverType.FIREFOX_DRIVER;
	}
	
}
