package com.kms.katalon.composer.integration.qtest.dialog;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.integration.qtest.QTestIntegrationAuthenticationManager;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;
import com.kms.katalon.controller.ProjectController;

public class GenerateNewTokenDialog extends Dialog {
	private Text txtServerUrl;
	private Text txtUsername;
	private Text txtPassword;
	
	private String serverUrl;
	private String username;
	private String password;
	
	private String token;

	public GenerateNewTokenDialog(Shell parentShell, String serverUrl, String username, String password) {
		super(parentShell);
		
		this.serverUrl = serverUrl;
		this.username = username;
		this.password = password;
	}

	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		Label lblServerUrl = new Label(container, SWT.NONE);
		lblServerUrl.setText("Server Url");
		
		txtServerUrl = new Text(container, SWT.BORDER);
		txtServerUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setText("Username");
		
		txtUsername = new Text(container, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setText("Password");
		
		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		initialize();
		
		return container;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Generate", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	protected void okPressed() {
		if (generateNewToken()) {
			super.okPressed();
		}
	}
	
	private boolean generateNewToken() {
		if (txtServerUrl.getText().isEmpty()) {
			MessageDialog.openInformation(null, "Information", "Please enter server url.");
			return false;
		}
		
		if (txtUsername.getText().isEmpty()) {
			MessageDialog.openInformation(null, "Information", "Please enter username.");
			return false;
		}
		
		if (txtPassword.getText().isEmpty()) {
			MessageDialog.openInformation(null, "Information", "Please enter password.");
			return false;
		}
		
		try {
			String newServerUrl = txtServerUrl.getText();
			String newUsername = txtUsername.getText();
			String newPassword = txtPassword.getText();
			
			token = QTestIntegrationAuthenticationManager.getToken(newServerUrl, newUsername, newPassword);
			QTestSettingStore.saveUserProfile(newServerUrl, newUsername, newPassword, ProjectController.getInstance()
					.getCurrentProject().getFolderLocation());
			return true;
		} catch (MalformedURLException | UnknownHostException e) {
			MultiStatusErrorDialog.showErrorDialog(e, "Unable to get token", "Server not found.");
		} catch (Exception e) {
			MultiStatusErrorDialog.showErrorDialog(e, "Unable to get token", e.getClass().getSimpleName());
		}
		
		return false;
	}
	
	public String getNewToken() {
		return token;
	}


	private void initialize() {
		if (serverUrl != null) txtServerUrl.setText(serverUrl);
		if (username != null) txtUsername.setText(username);
		if (password != null) txtPassword.setText(password);
	}
	

    @Override
    protected Point getInitialSize() {
        return new Point(400, 200);
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Generate new token");
    }

}
