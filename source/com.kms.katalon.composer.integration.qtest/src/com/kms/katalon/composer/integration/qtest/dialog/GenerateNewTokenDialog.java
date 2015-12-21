package com.kms.katalon.composer.integration.qtest.dialog;

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
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.integration.qtest.QTestIntegrationAuthenticationManager;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.credential.impl.QTestCredentialImpl;
import com.kms.katalon.integration.qtest.exception.QTestException;

public class GenerateNewTokenDialog extends Dialog {
    private Text txtServerUrl;
    private Text txtUsername;
    private Text txtPassword;

    private IQTestCredential fCredential;

    public GenerateNewTokenDialog(Shell parentShell, IQTestCredential credential) {
        super(parentShell);
        fCredential = credential;
    }

    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 2;

        Label lblServerUrl = new Label(container, SWT.NONE);
        lblServerUrl.setText(StringConstants.CM_SERVER_URL);

        txtServerUrl = new Text(container, SWT.BORDER);
        txtServerUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblUsername = new Label(container, SWT.NONE);
        lblUsername.setText(StringConstants.CM_USERNAME);

        txtUsername = new Text(container, SWT.BORDER);
        txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(container, SWT.NONE);
        lblPassword.setText(StringConstants.CM_PASSWORD);

        txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        initialize();

        return container;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, StringConstants.DIA_TITLE_GENERATE, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected void okPressed() {
        if (generateNewToken()) {
            super.okPressed();
        }
    }

    private boolean generateNewToken() {
        String newServerUrl = txtServerUrl.getText();
        String newUsername = txtUsername.getText();
        String newPassword = txtPassword.getText();

        if (newServerUrl.isEmpty()) {
            MessageDialog.openInformation(null, StringConstants.INFORMATION, StringConstants.DIA_MSG_ENTER_SERVER_URL);
            return false;
        }

        if (newUsername.isEmpty()) {
            MessageDialog.openInformation(null, StringConstants.INFORMATION, StringConstants.DIA_MSG_ENTER_USERNAME);
            return false;
        }

        if (newPassword.isEmpty()) {
            MessageDialog.openInformation(null, StringConstants.INFORMATION, StringConstants.DIA_MSG_ENTER_PASSWORD);
            return false;
        }

        try {
            QTestCredentialImpl credentials = new QTestCredentialImpl()
                    .setServerUrl(newServerUrl)
                    .setUsername(newUsername)
                    .setPassword(newPassword)
                    .setVersion(fCredential.getVersion());
            
            credentials.setToken(QTestIntegrationAuthenticationManager.getToken(credentials));
            fCredential = credentials;
            return true;
        } catch (QTestException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_UNABLE_TO_GET_TOKEN, e.getClass()
                    .getSimpleName());
        }

        return false;
    }
    
    public IQTestCredential getNewCredential() {
        return fCredential;
    }

    private void initialize() {
        if (fCredential == null) {
            fCredential = new QTestCredentialImpl();
        }

        setText(txtServerUrl, fCredential.getServerUrl());
        setText(txtUsername, fCredential.getUsername());
        setText(txtPassword, fCredential.getPassword());
    }
    
    private void setText(Text text, String value) {
        text.setText(value != null ? value : "");
    }
    

    @Override
    protected Point getInitialSize() {
        return new Point(400, 200);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_TITLE_GENERATE_TOKEN);
    }

}
