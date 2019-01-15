package com.kms.katalon.composer.webservice.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Oauth2AuthorizationRetrievalDialog extends Dialog {
	private String urlToSendAuthorizationRequest;
	private String urlToGetAccessToken;

	public Oauth2AuthorizationRetrievalDialog(Shell parentShell, String urlToSendAuthorizationRequest) {
		super(parentShell);
		setShellStyle(SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.RESIZE);
		this.urlToSendAuthorizationRequest = urlToSendAuthorizationRequest;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdContainer.heightHint = 200;
		gdContainer.widthHint = 400;
		container.setLayoutData(gdContainer);
		GridLayout glContainer = new GridLayout(1, false);
		container.setLayout(glContainer);

		Label lblHowToGetAuthorizationCode = new Label(container, SWT.WRAP);
		lblHowToGetAuthorizationCode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lblHowToGetAuthorizationCode.setText(
				"Copy the following URL and paste it in your browser's address bar,"
				+ " you will be directed to the authorization page where you must grant access to the inquired resource");
		Text lblUrlToSendAuthorizationRequest = new Text(container, SWT.BORDER);
		lblUrlToSendAuthorizationRequest.setText(urlToSendAuthorizationRequest);
		lblUrlToSendAuthorizationRequest.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblHowToGetAccessToken = new Label(container, SWT.WRAP);
		lblHowToGetAccessToken.setText("Copy the result from your browser's address bar and paste it here");
		lblHowToGetAccessToken.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Text lblUrlToGetAccessToken = new Text(container, SWT.BORDER);
		lblUrlToGetAccessToken.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		lblUrlToGetAccessToken.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				urlToGetAccessToken = lblUrlToGetAccessToken.getText();
			}
		});
		return container;
	}

	public String getAuthorizationCode() {
		return urlToGetAccessToken;
	}

}
