package com.kms.katalon.plugin.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.ApplicationInfo;
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
    
    private Composite body;
    
    private Button btnConnect;
    
    private Button btnClose;
    
    private Button cbLicenseAgreement;
    
    private Link lnkLicenseAgreement;

    public KStoreLoginDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        body = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        body.setLayout(layout);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.minimumWidth = 400;
        body.setLayoutData(gdBody);
        
        Label lblInstruction = new Label(body, SWT.WRAP);
        lblInstruction.setText(StringConstants.KStoreLoginDialog_LBL_INSTRUCTION);
        lblInstruction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        Composite inputComposite = new Composite(body, SWT.NONE);
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout glInput = new GridLayout(2, false);
        glInput.marginWidth = 0;
        inputComposite.setLayout(glInput);
        
        Label lblUsername = new Label(inputComposite, SWT.NONE);
        lblUsername.setText(StringConstants.KStoreLoginDialog_LBL_USERNAME);

        txtUsername = new Text(inputComposite, SWT.BORDER);
        txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        username = ApplicationInfo.getAppProperty("email");
        txtUsername.setText(username);
        txtUsername.setEditable(false);

        Label lblPassword = new Label(inputComposite, SWT.NONE);
        lblPassword.setText(StringConstants.KStoreLoginDialog_LBL_PASSWORD);

        txtPassword = new Text(inputComposite, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        Composite licenseComposite = new Composite(body, SWT.NONE);
        licenseComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        licenseComposite.setLayout(new GridLayout(2, false));
        
        cbLicenseAgreement = new Button(licenseComposite, SWT.CHECK);
        cbLicenseAgreement.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        
        lnkLicenseAgreement = new Link(licenseComposite, SWT.WRAP);
        lnkLicenseAgreement.setText(StringConstants.KStoreLoginDialog_LICENSE_AGREEMENT_MSG);
        GridData gdLicenseAgreement = new GridData(SWT.FILL, SWT.FILL, true, false);
        gdLicenseAgreement.widthHint = 350;
        lnkLicenseAgreement.setLayoutData(gdLicenseAgreement);

        lblError = new Label(body, SWT.NONE);
        lblError.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        ((GridData) lblError.getLayoutData()).exclude = true;
        lblError.setVisible(false);
        
        Composite buttonComposite = new Composite(body, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        buttonComposite.setLayout(new GridLayout(2, false));
        
        btnClose = new Button(buttonComposite, SWT.FLAT);
        btnClose.setText(IDialogConstants.CLOSE_LABEL);
        
        btnConnect = new Button(buttonComposite, SWT.FLAT);
        btnConnect.setText(StringConstants.KStoreLoginDialog_BTN_CONNECT);
        btnConnect.setEnabled(false);

        registerControlListeners();

        return body;
    }

    private void registerControlListeners() {
        txtUsername.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                hideError();
                username = txtUsername.getText();
                validate();
            }
        });

        txtPassword.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                hideError();
                password = txtPassword.getText();
                validate();
            }
        });
        
        btnConnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                authenticate();
            }
        });
        
        btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(Dialog.CANCEL);
                close();
            }
        });
        
        cbLicenseAgreement.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                validate();
            }
        });
        
        lnkLicenseAgreement.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }
    
    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    private void validate() {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || !cbLicenseAgreement.getSelection()) {
            btnConnect.setEnabled(false);
        } else {
            btnConnect.setEnabled(true);
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

    protected void authenticate() {
        KStoreUsernamePasswordCredentials credentials = new KStoreUsernamePasswordCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        try {
            btnConnect.setEnabled(false);
            KStoreRestClient restClient = new KStoreRestClient(credentials);
            AuthenticationResult authenticateResult = restClient.authenticate();
            if (authenticateResult.isAuthenticated()) {
                token = authenticateResult.getToken();
                super.okPressed();
            } else {
                showError(StringConstants.KStoreLoginDialog_INVALID_ACCOUNT_ERROR);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            showError(StringConstants.KStoreLoginDialog_FAILED_TO_AUTHENTICATE_MSG);
        } finally {
            btnConnect.setEnabled(true);
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

    private void showError(String errorMsg) {
        lblError.setText(errorMsg);
        setErrorMessageVisible(true);
    }

    private void hideError() {
        lblError.setText(StringUtils.EMPTY);
        setErrorMessageVisible(false);
    }
    
    private void setErrorMessageVisible(boolean visible) {
        GridData gridData = (GridData) lblError.getLayoutData();
        gridData.exclude = !visible;
        lblError.setVisible(visible);
        getShell().pack();
    }
}
