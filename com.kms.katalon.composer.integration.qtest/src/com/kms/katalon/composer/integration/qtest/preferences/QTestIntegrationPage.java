package com.kms.katalon.composer.integration.qtest.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtil;
import com.kms.katalon.composer.integration.qtest.dialog.GenerateNewTokenDialog;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationPage extends PreferencePage {
	private Text txtToken;
	private Button chckAutoSubmitTestRun;
	private Button chckEnableIntegration;
	private String projectDir;

	private Button btnOpenGenerateTokenDialog;
	private Composite container;
	private Composite mainComposite;
	private Composite projectComposite;
	private Group grpAuthentication;
	private GridData gdTxtToken;
	private Group grpAttachments;
	private Button chckEnableCheckBeforeUploading;

	public QTestIntegrationPage() {
		projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
	}

	@Override
	protected Control createContents(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		container.setLayout(new GridLayout(1, false));

		chckEnableIntegration = new Button(container, SWT.CHECK);
		chckEnableIntegration.setText("Enable integration");

		mainComposite = new Composite(container, SWT.NONE);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_mainComposite = new GridLayout(1, false);
		gl_mainComposite.marginWidth = 0;
		gl_mainComposite.marginHeight = 0;
		mainComposite.setLayout(gl_mainComposite);

		grpAuthentication = new Group(mainComposite, SWT.NONE);
		grpAuthentication.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpAuthentication.setLayout(new GridLayout(4, false));
		grpAuthentication.setText("Authentication");

		Label lblToken = new Label(grpAuthentication, SWT.NONE);
		GridData gd_lblToken = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_lblToken.widthHint = 100;
		gd_lblToken.verticalIndent = 5;
		lblToken.setLayoutData(gd_lblToken);
		lblToken.setText("Token");

		txtToken = new Text(grpAuthentication, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		gdTxtToken = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gdTxtToken.heightHint = 60;
		txtToken.setLayoutData(gdTxtToken);

		btnOpenGenerateTokenDialog = new Button(grpAuthentication, SWT.NONE);
		btnOpenGenerateTokenDialog.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnOpenGenerateTokenDialog.setText("Generate");

		projectComposite = new Composite(mainComposite, SWT.NONE);
		projectComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl_projectComposite = new GridLayout(3, false);
		gl_projectComposite.verticalSpacing = 10;
		projectComposite.setLayout(gl_projectComposite);
		
		chckEnableCheckBeforeUploading = new Button(projectComposite, SWT.CHECK);
		chckEnableCheckBeforeUploading.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		chckEnableCheckBeforeUploading.setText("Check duplicates before uploading test cases");

		chckAutoSubmitTestRun = new Button(projectComposite, SWT.CHECK);
		chckAutoSubmitTestRun.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		chckAutoSubmitTestRun.setText("Auto-submit test run result");
		
		grpAttachments = new Group(mainComposite, SWT.NONE);
		grpAttachments.setText("Send attachments for test run");
		grpAttachments.setLayout(new GridLayout(4, false));
		grpAttachments.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Button rbtnNotSend = new Button(grpAttachments, SWT.RADIO);
		rbtnNotSend.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rbtnNotSend.setText("Do not send");
		rbtnNotSend.setData(QTestAttachmentSendingType.NOT_SEND);
		
		Button rbtnSendIfPassed= new Button(grpAttachments, SWT.RADIO);
		rbtnSendIfPassed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rbtnSendIfPassed.setText("Send if test case passes");
		rbtnSendIfPassed.setData(QTestAttachmentSendingType.SEND_IF_PASSES);
		
		Button rbtnSendIfFailed = new Button(grpAttachments, SWT.RADIO);
		rbtnSendIfFailed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rbtnSendIfFailed.setText("Send if test case fails");
		rbtnSendIfFailed.setData(QTestAttachmentSendingType.SEND_IF_FAILS);
		
		Button rbtnAlwaysSend = new Button(grpAttachments, SWT.RADIO);
		rbtnAlwaysSend.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rbtnAlwaysSend.setText("Always send");
		rbtnAlwaysSend.setData(QTestAttachmentSendingType.ALWAYS_SEND);

		addToolItemListeners();
		initilize();

		return container;
	}

	private void addToolItemListeners() {

		btnOpenGenerateTokenDialog.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					GenerateNewTokenDialog dialog = new GenerateNewTokenDialog(btnOpenGenerateTokenDialog.getDisplay()
							.getActiveShell(), QTestSettingStore.getServerUrl(projectDir), QTestSettingStore
							.getUsername(projectDir), QTestSettingStore.getPassword(projectDir));
					if (dialog.open() == Dialog.OK) {
						txtToken.setText(dialog.getNewToken());
					}
				} catch (Exception ex) {
					MultiStatusErrorDialog
							.showErrorDialog(ex, "Unable to get qTest projects.", ex.getClass().getSimpleName());
				}
			}
		});

		chckEnableIntegration.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				enableMainComposite();
			}
		});

		txtToken.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				gdTxtToken.widthHint = txtToken.getSize().x;
				container.setSize(container.getParent().getSize().x, container.getSize().y);
			}

		});
		
		chckAutoSubmitTestRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableAttachmentsGroup();
			}
		});
	}

	private void enableAttachmentsGroup() {
		if (chckEnableIntegration.getSelection() && chckAutoSubmitTestRun.getSelection()) {
			ControlUtil.recursiveSetEnabled(grpAttachments, true);
			grpAttachments.setEnabled(true);
		} else {
		    ControlUtil.recursiveSetEnabled(grpAttachments, false);
			grpAttachments.setEnabled(false);
		}
	}

	private void enableMainComposite() {
		if (chckEnableIntegration.getSelection()) {
		    ControlUtil.recursiveSetEnabled(mainComposite, true);
		} else {
		    ControlUtil.recursiveSetEnabled(mainComposite, false);
		}
	}

	private void initilize() {
		String token = QTestSettingStore.getToken(projectDir);
		boolean autoSubmitResult = QTestSettingStore.isAutoSubmitResultActive(projectDir);
		boolean isIntegrationActive = QTestSettingStore.isIntegrationActive(projectDir);
		boolean isEnableCheckBeforeUploading = QTestSettingStore.isEnableCheckBeforeUploading(projectDir);
				
		txtToken.setText(token != null ? token : "");

		chckAutoSubmitTestRun.setSelection(autoSubmitResult);
		chckEnableIntegration.setSelection(isIntegrationActive);
		chckEnableCheckBeforeUploading.setSelection(isEnableCheckBeforeUploading);
		
		//set input for grpAttachments
		QTestAttachmentSendingType sendingType =  QTestSettingStore.getAttachmentSendingType(projectDir);
		for (Control radioButton : grpAttachments.getChildren()) {
			if (radioButton instanceof Button) {
				if (radioButton.getData() == sendingType) {
					((Button) radioButton).setSelection(true);
				} else {
					((Button) radioButton).setSelection(false);
				}
			}
		}

		enableMainComposite();
		enableAttachmentsGroup();
		container.pack();
	}

	@Override
	public boolean performOk() {
		if (container == null)
			return true;
		try {
			QTestSettingStore.saveAutoSubmit(chckAutoSubmitTestRun.getSelection(), projectDir);
			QTestSettingStore.saveToken(txtToken.getText(), projectDir);
			QTestSettingStore.saveEnableIntegration(chckEnableIntegration.getSelection(), projectDir);
			QTestSettingStore.saveEnableCheckBeforeUploading(chckEnableCheckBeforeUploading.getSelection(), projectDir);
			
			for (Control radioButtonControl : grpAttachments.getChildren()) {
				if (radioButtonControl instanceof Button) {
					Button sendingTypeRadioButton = (Button) radioButtonControl;
					if (sendingTypeRadioButton.getSelection()) {
						QTestAttachmentSendingType sendingType = (QTestAttachmentSendingType) sendingTypeRadioButton.getData();
						QTestSettingStore.saveAttachmentSendingType(sendingType, projectDir);
					}
				}
			}

			return true;
		} catch (Exception e) {
			MessageDialog.openError(null, "Error", "Unable to save qTest's settings.");
			return false;
		}
	}

	@Override
	protected void performDefaults() {
		initilize();
	}
}
