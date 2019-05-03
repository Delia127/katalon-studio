package com.kms.katalon.composer.integration.analytics.uploadProject;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CustomTitleAreaDialog;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;

public class StoreProjectCodeToCloudDialog extends CustomTitleAreaDialog {
	
	private Combo cbbProjects;

    private Combo cbbTeams;
    
    private Text txtCodeRepoName;

	public StoreProjectCodeToCloudDialog(Shell parentShell) {
		super(parentShell);
	}
	
    @Override
	protected boolean isResizable() {
	    return false;
	}
    
	@Override
	protected void configureShell(Shell newShell)
	{
	  super.configureShell(newShell);
	  newShell.setText(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_TITLE_UPLOAD_CODE);		
	}
	
	@Override
	protected Composite createContentArea(Composite parent) {

        setDialogTitle(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_TITLE_UPLOAD_CODE);
        setMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_UPLOAD_CODE, IMessageProvider.INFORMATION);

        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 15;
        
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
 
        Label lblTeam = new Label(composite, SWT.NONE);
        lblTeam.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEAM);

        cbbTeams = new Combo(composite, SWT.READ_ONLY);
        cbbTeams.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Label lblProject = new Label(composite, SWT.NONE);
        lblProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PROJECT);

        cbbProjects = new Combo(composite, SWT.READ_ONLY);
        cbbProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Label lblCodeRepoName = new Label(composite, SWT.NONE);
        lblCodeRepoName.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_CODE_REPO_NAME);
        
        txtCodeRepoName = new Text(composite, SWT.BORDER);
        txtCodeRepoName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
		return composite;
	}
	
	@Override
	protected void registerControlModifyListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setInput() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void okPressed() {
		System.out.println("Ok pressed");
		String name = txtCodeRepoName.getText();
		
		System.out.println(name);
	}

}
