package com.kms.katalon.plugin.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.service.KStoreRestClient;
import com.kms.katalon.plugin.service.KStoreRestClient.AuthenticationResult;

public class KStoreLoginDialog extends Dialog {

    private Text txtUsername;

    private Text txtPassword;

    private String username;

    private String password;

    private String token;

    private Label lblError;
    
    private Button btnOk;

    public KStoreLoginDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        body.setLayout(layout);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 400;
        body.setLayoutData(gdBody);

        Label lblUsername = new Label(body, SWT.NONE);
        lblUsername.setText(StringConstants.KStoreLoginDialog_LBL_USERNAME);

        txtUsername = new Text(body, SWT.BORDER);
        txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblPassword = new Label(body, SWT.NONE);
        lblPassword.setText(StringConstants.KStoreLoginDialog_LBL_PASSWORD);

        txtPassword = new Text(body, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblEmpty = new Label(body, SWT.NONE);

        lblError = new Label(body, SWT.NONE);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        registerControlListeners();
        return super.createDialogArea(parent);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        
        btnOk = getButton(IDialogConstants.OK_ID);
        btnOk.setEnabled(false); //initially disable button OK
    }

    private void registerControlListeners() {
        txtUsername.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                hideErrorMessage();
                username = txtUsername.getText();
                validate();
            }
        });

        txtPassword.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                hideErrorMessage();
                password = txtPassword.getText();
                validate();
            }
        });
    }

    private void validate() {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.KStoreLoginDialog_DIA_TITLE);
    }

    @Override
    protected void okPressed() {
        KStoreUsernamePasswordCredentials credentials = new KStoreUsernamePasswordCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        try {
            btnOk.setEnabled(false);
            KStoreRestClient restClient = new KStoreRestClient(credentials);
            AuthenticationResult authenticateResult = restClient.authenticate();
            if (authenticateResult.isAuthenticated()) {
                token = authenticateResult.getToken();
                super.okPressed();
            } else {
                showInvalidAccountErrorMessage();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } finally {
            btnOk.setEnabled(true);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    private void showInvalidAccountErrorMessage() {
        displayInvalidAccountErrorMessage(true);
    }

    private void hideErrorMessage() {
        displayInvalidAccountErrorMessage(false);
    }

    private void displayInvalidAccountErrorMessage(boolean display) {
        if (display) {
            lblError.setText(StringConstants.KStoreLoginDialog_INVALID_ACCOUNT_ERROR);
        } else {
            lblError.setText(StringUtils.EMPTY);
        }
        lblError.getParent().layout();
    }
}
