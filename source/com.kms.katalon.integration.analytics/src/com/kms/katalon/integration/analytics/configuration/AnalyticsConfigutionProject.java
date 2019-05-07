package com.kms.katalon.integration.analytics.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class AnalyticsConfigutionProject {
	private String email, password;
	
    private AnalyticsSettingStore analyticsSettingStore;
    
    private static Properties appProperties;
    
	public AnalyticsConfigutionProject() {
		this.email = "";
		this.password = "";
		
		this.analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
		getAccount();
	}
	
	private void getAccount() {
		Properties appProps = getAppProperties();
		
		email = appProps.getProperty("email");
		String passwordDecode = appProps.getProperty("password");
		try {
			password = CryptoUtil.decode(CryptoUtil.getDefault(passwordDecode));
			System.out.println(password);
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateDataStore() {
		try {
			String severEndpoint = "https://analytics.katalon.com/";
			analyticsSettingStore.enableEncryption(true);
			analyticsSettingStore.setServerEndPoint(severEndpoint, true);
			analyticsSettingStore.setEmail(email, true);
			analyticsSettingStore.setPassword(password, true);
			
		} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    public static String userDirLocation() {
        return ApplicationStringConstants.APP_USER_DIR_LOCATION;
    }
	
    private static Properties getAppProperties() {
        if (appProperties != null) {
            return appProperties;
        }

        File appPropFile = new File(ApplicationStringConstants.APP_INFO_FILE_LOCATION);
        File katalonDir = new File(userDirLocation());
        if (!appPropFile.exists()) {
            if (!katalonDir.exists()) {
                katalonDir.mkdir();
            }
            try {
                appPropFile.createNewFile();
            } catch (Exception ex) {
                LogUtil.logError(ex);
            }
        }
        try (FileInputStream in = new FileInputStream(appPropFile)) {
            appProperties = new Properties();
            appProperties.load(in);
        } catch (Exception ex) {
            appProperties = null;
            LogUtil.logError(ex);
        }

        return appProperties;
    }
}
