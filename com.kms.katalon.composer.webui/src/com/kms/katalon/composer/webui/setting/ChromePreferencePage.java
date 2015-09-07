package com.kms.katalon.composer.webui.setting;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class ChromePreferencePage extends BrowserPreferencePage {

	@Override
	protected DriverType getDriverType() {
		return WebUIDriverType.CHROME_DRIVER;
	}

}
