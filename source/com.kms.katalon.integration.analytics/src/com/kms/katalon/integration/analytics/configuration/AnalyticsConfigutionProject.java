package com.kms.katalon.integration.analytics.configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.handler.AnalyticsAuthorizationHandler;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.util.CryptoUtil;

public class AnalyticsConfigutionProject {
	private String email, password;
	
    private AnalyticsSettingStore analyticsSettingStore;
    
    private String serverUrl = "https://analytics.katalon.com/";
        
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
			analyticsSettingStore.enableIntegration(true);
			analyticsSettingStore.enableEncryption(true);
			analyticsSettingStore.setServerEndPoint(serverUrl, true);
			analyticsSettingStore.setEmail(email, true);
			analyticsSettingStore.setPassword(password, true);		
			analyticsSettingStore.setAutoSubmit(false);
			analyticsSettingStore.setAttachScreenshot(false);
			analyticsSettingStore.setAttachCapturedVideos(false);
			getDeaultTeamAndProject();
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	private void getDeaultTeamAndProject() throws IOException {
		List<AnalyticsTeam> teams = new ArrayList<>();
		List<AnalyticsProject> projects = new ArrayList<>();
		AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(
                serverUrl,
                email,  
                password, 
                analyticsSettingStore);
		
		teams = AnalyticsAuthorizationHandler.getTeams(serverUrl, email, password, tokenInfo, new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));
		
		AnalyticsTeam team = teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
		
		projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password,
				team, tokenInfo,
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));
		
		analyticsSettingStore.setTeam(team);
		analyticsSettingStore.setProject(projects.get(0));
	}	
	
	public boolean checkUserAccessProject() throws IOException {
		List<AnalyticsTeam> teams = new ArrayList<>();
		AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(
                serverUrl,
                email,  
                password, 
                analyticsSettingStore);
		
		teams = AnalyticsAuthorizationHandler.getTeams(serverUrl, email, password, tokenInfo, new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));
		
		AnalyticsTeam currentTeam = analyticsSettingStore.getTeam();
		long currentTeamId = currentTeam.getId();
    	
    	boolean userCanAccessProject = false;
    	
    	for (AnalyticsTeam team : teams) {
    		long teamId = team.getId();
    		if (teamId == currentTeamId) {
    			userCanAccessProject = true;
    			break;
    		}
    	}
    	return userCanAccessProject;
	}
}
