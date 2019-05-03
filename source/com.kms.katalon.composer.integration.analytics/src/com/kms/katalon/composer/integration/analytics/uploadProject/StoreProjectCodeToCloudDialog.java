package com.kms.katalon.composer.integration.analytics.uploadProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.integration.analytics.handlers.AnalyticsAuthorizationHandler;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.entity.AnalyticsUploadInfo;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;


public class StoreProjectCodeToCloudDialog extends Dialog {
	
	private Combo cbbProjects;

    private Combo cbbTeams;
    
    private Text txtCodeRepoName;
    
    private AnalyticsSettingStore analyticsSettingStore;
    
    private List<AnalyticsProject> projects = new ArrayList<>();

    private List<AnalyticsTeam> teams = new ArrayList<>();
    
    private String serverUrl, email, password;
    
    private ProjectEntity currentProject;
    private ProjectController pController = ProjectController.getInstance();
    
	public StoreProjectCodeToCloudDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, ComposerIntegrationAnalyticsMessageConstants.BTN_UPLOAD, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
    @Override
	protected boolean isResizable() {
	    return true;
	}
    
	@Override
	protected void configureShell(Shell newShell)
	{
	  super.configureShell(newShell);
	  newShell.setText(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_TITLE_UPLOAD_CODE);		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);

      GridLayout layout = new GridLayout(2, false);
      layout.horizontalSpacing = 15;
      
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

      Label lblGetting = new Label(composite, SWT.NONE);
      lblGetting.setText(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_UPLOAD_CODE);
      GridData lblGettingGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 2);
      lblGetting.setLayoutData(lblGettingGridData);
      
      Group grpUploadProject = new Group(composite, SWT.NONE);
      grpUploadProject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
      GridLayout glGrpAuthentication = new GridLayout(2, false);
      glGrpAuthentication.horizontalSpacing = 15;
      grpUploadProject.setLayout(glGrpAuthentication);
//      grpUploadProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_AUTHENTICATE_GROUP);
      
      Label lblTeam = new Label(grpUploadProject, SWT.NONE);
      lblTeam.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEAM);

      cbbTeams = new Combo(grpUploadProject, SWT.READ_ONLY);
      GridData cbbTeamsGritData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
      cbbTeams.setLayoutData(cbbTeamsGritData);
      
      Label lblProject = new Label(grpUploadProject, SWT.NONE);
      lblProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PROJECT);

      cbbProjects = new Combo(grpUploadProject, SWT.READ_ONLY);
      GridData cbbProjectsGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
      cbbProjects.setLayoutData(cbbProjectsGridData);
      
      Label lblCodeRepoName = new Label(grpUploadProject, SWT.NONE);
      lblCodeRepoName.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_CODE_REPO_NAME);
      
      txtCodeRepoName = new Text(grpUploadProject, SWT.BORDER);
      GridData txtCodeRepoNameGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
      txtCodeRepoName.setLayoutData(txtCodeRepoNameGridData);
      
      addListener();
      fillData();
      
		return composite;
	}
	
	private void fillData() {
		try {
			cbbTeams.setItems();
			cbbProjects.setItems();
			
			analyticsSettingStore = new AnalyticsSettingStore(
	                ProjectController.getInstance().getCurrentProject().getFolderLocation());
			
			boolean encryptionEnabled = analyticsSettingStore.isEncryptionEnabled();
			
            password = analyticsSettingStore.getPassword(analyticsSettingStore.isEncryptionEnabled());
            serverUrl = analyticsSettingStore.getServerEndpoint(analyticsSettingStore.isEncryptionEnabled());
            email = analyticsSettingStore.getEmail(analyticsSettingStore.isEncryptionEnabled());
            
            AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(
                    analyticsSettingStore.getServerEndpoint(encryptionEnabled),
                    analyticsSettingStore.getEmail(encryptionEnabled), 
                    password, 
                    analyticsSettingStore);
            
            if (tokenInfo == null){
                return;
            }
            
            teams = AnalyticsAuthorizationHandler.getTeams(analyticsSettingStore.getServerEndpoint(encryptionEnabled),
                    analyticsSettingStore.getEmail(encryptionEnabled), password, tokenInfo,
                    new ProgressMonitorDialog(getShell()));
            projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password,
                    teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams)), tokenInfo,
                    new ProgressMonitorDialog(getShell()));
            
            if (teams != null && !teams.isEmpty()) {
                cbbTeams.setItems(AnalyticsAuthorizationHandler.getTeamNames(teams).toArray(new String[teams.size()]));
                cbbTeams.select(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
            }

            if (teams != null && teams.size() > 0) {
                setProjectsBasedOnTeam(teams, projects, analyticsSettingStore.getServerEndpoint(encryptionEnabled),
                        analyticsSettingStore.getEmail(encryptionEnabled), password);
            }
            
		} catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
		}
	}
	
    private void setProjectsBasedOnTeam(List<AnalyticsTeam> teams, List<AnalyticsProject> projects, String serverUrl,
            String email, String password) {

        if (projects != null && !projects.isEmpty()) {
            cbbProjects.setItems(AnalyticsAuthorizationHandler.getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
        } else {
        	cbbProjects.setItems(AnalyticsAuthorizationHandler.getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
        }
    }
    
    private void addListener() {
    	cbbTeams.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
//                	password = analyticsSettingStore.getPassword(analyticsSettingStore.isEncryptionEnabled());
//                    serverUrl = analyticsSettingStore.getServerEndpoint(analyticsSettingStore.isEncryptionEnabled());
//                    email = analyticsSettingStore.getEmail(analyticsSettingStore.isEncryptionEnabled());
                    AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email, password, analyticsSettingStore);
                    projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password,
                            teams.get(cbbTeams.getSelectionIndex()), tokenInfo, new ProgressMonitorDialog(getShell()));
                    analyticsSettingStore.setTeam(teams.get(cbbTeams.getSelectionIndex()));
                    setProjectsBasedOnTeam(teams, projects, serverUrl, email, password);

                } catch (IOException ex) {
                    LoggerSingleton.logError(ex);
                    MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR, ex.getMessage());
                }
            }
        });
    }
	
	private void Compress(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWrite = null;
		
		fileWrite = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWrite);
		
		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}
	
	private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFile);
		
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}
	
	private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);
		
		for (String fileName : folder.list()) {
			if (fileName.equals(".git")) 
				continue;
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}
	
	@Override
	protected void okPressed() {
		
		String name = txtCodeRepoName.getText();
		int currentIndexProject = cbbProjects.getSelectionIndex();
		AnalyticsProject sellectProject = projects.get(currentIndexProject);
		
		currentProject = pController.getCurrentProject();
		
		System.out.println("Project" + sellectProject.getName());
		System.out.println("Name" + name);
		String folderCurrentProject = currentProject.getFolderLocation();
		
		String zipFile = "D:\\Katalon Example\\fileZip.zip";
		
		try {
			Compress(folderCurrentProject, zipFile);
			AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
			
			AnalyticsUploadInfo uploadInfo = AnalyticsApiProvider.getUploadInfo(serverUrl, token.getAccess_token(), sellectProject.getId());
			
			File file = new File(zipFile);
			AnalyticsApiProvider.uploadFile(uploadInfo.getUploadUrl(), file);
			long timestamp = System.currentTimeMillis();
			AnalyticsApiProvider.uploadTestProject(serverUrl,
					sellectProject.getId(), 
					sellectProject.getTeamId(), 
					timestamp, 
					name, 
					zipFile, file.getName().toString(), uploadInfo.getPath(), token.getAccess_token());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.okPressed();
	}
	
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(550, 280);
	}	
}
