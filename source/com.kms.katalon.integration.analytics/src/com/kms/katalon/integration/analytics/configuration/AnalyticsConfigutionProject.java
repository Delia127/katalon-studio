package com.kms.katalon.integration.analytics.configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.util.CryptoUtil;

public class AnalyticsConfigutionProject {
	private String email, password;
	
    private AnalyticsSettingStore analyticsSettingStore;
        
	public AnalyticsConfigutionProject() {
		this.email = "";
		this.password = "";
		
		this.analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
		getAccount();
	}
	
	private void getAccount() {	
		email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
		String passwordDecode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
		try {
			password = CryptoUtil.decode(CryptoUtil.getDefault(passwordDecode));
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDataStore() {
		try {
			String serverUrl = "https://analytics.katalon.com/";
			analyticsSettingStore.enableIntegration(true);
			analyticsSettingStore.enableEncryption(true);
			analyticsSettingStore.setServerEndPoint(serverUrl, true);
			analyticsSettingStore.setEmail(email, true);
			analyticsSettingStore.setPassword(password, true);			
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
}
