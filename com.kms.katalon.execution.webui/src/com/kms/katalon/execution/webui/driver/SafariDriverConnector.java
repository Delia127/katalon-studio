package com.kms.katalon.execution.webui.driver;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.entity.AbstractDriverConnector;

public class SafariDriverConnector extends AbstractDriverConnector {
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.SAFARI_DRIVER;
	}
}
