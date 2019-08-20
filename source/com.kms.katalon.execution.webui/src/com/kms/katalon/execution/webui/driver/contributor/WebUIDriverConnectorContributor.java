package com.kms.katalon.execution.webui.driver.contributor;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;
import com.kms.katalon.execution.webui.driver.EdgeDriverConnector;
import com.kms.katalon.execution.webui.driver.FirefoxDriverConnector;
import com.kms.katalon.execution.webui.driver.FirefoxHeadlessDriverConnector;
import com.kms.katalon.execution.webui.driver.HeadlessDriverConnector;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;
import com.kms.katalon.execution.webui.driver.SafariDriverConnector;

public class WebUIDriverConnectorContributor implements IDriverConnectorContributor {

	@Override
	public IDriverConnector[] getDriverConnector(String configFolderPath) throws IOException {
		return new IDriverConnector[] { new ChromeDriverConnector(configFolderPath),
				new EdgeDriverConnector(configFolderPath), new FirefoxDriverConnector(configFolderPath),
				new IEDriverConnector(configFolderPath), new SafariDriverConnector(configFolderPath),
				new HeadlessDriverConnector(configFolderPath), new FirefoxHeadlessDriverConnector(configFolderPath) };
	}

	@Override
	public String getName() {
		return DriverFactory.WEB_UI_DRIVER_PROPERTY;
	}
}
