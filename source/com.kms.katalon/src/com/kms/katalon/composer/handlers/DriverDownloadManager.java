package com.kms.katalon.composer.handlers;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.webui.configuration.WebDriverManagerRunConfiguration;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;

public class DriverDownloadManager {
	
	public static int downloadDriver(String typeDriver){
		
		try {
			String chromeDriverPath = SeleniumWebDriverProvider.getChromeDriverPath();
			
			WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
			webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.CHROME_DRIVER,
					 new File(chromeDriverPath).getParentFile());
               
			System.out.print(chromeDriverPath);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

}
