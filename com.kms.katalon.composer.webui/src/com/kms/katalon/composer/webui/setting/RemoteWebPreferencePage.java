package com.kms.katalon.composer.webui.setting;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteWebPreferencePage extends BrowserPreferencePage {

	@Override
	protected DriverType getDriverType() {
		return WebUIDriverType.REMOTE_WEB_DRIVER;
	}

}
