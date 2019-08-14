package com.kms.katalon.composer.handlers;
import java.io.File;
import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.webui.configuration.WebDriverManagerRunConfiguration;
import com.kms.katalon.execution.webui.driver.SeleniumWebDriverProvider;

public class UpdateChromeWebdriverHandler {
	 
	@Execute
	public void execute(Shell shell) {
//		try {
//			String chromeDriverPath = SeleniumWebDriverProvider.getChromeDriverPath();
//			
//			WebDriverManagerRunConfiguration webDriverManagerRunConfiguration = new WebDriverManagerRunConfiguration();
//			webDriverManagerRunConfiguration.downloadDriver(WebUIDriverType.CHROME_DRIVER,
//					 new File(chromeDriverPath).getParentFile());
//               
//			System.out.print(chromeDriverPath);
//			MessageDialog.openInformation(shell, "Path", chromeDriverPath);
//		} catch (IOException | InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		DriverDownloadManager.downloadDriver("Chrome");
		
	}
    

}
