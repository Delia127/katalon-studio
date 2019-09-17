package com.kms.katalon.activation.dialog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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

import com.kms.katalon.activation.dialog.SignupDialog.AuthenticationInfo;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.ActivationPreferenceConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.license.LicenseService;
import com.kms.katalon.license.models.License;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.util.CryptoUtil;

public class ActivationDialog extends Dialog {

    private Button btnActivate;

    private Button btnClear;

    private Text txtUserName;

    private Text txtPassword;

    private Label lblError;
    
    private boolean allowOfflineActivation = true;

    public ActivationDialog(Shell parentShell) {
        super(parentShell);
    }

    public boolean isAllowOfflineActivation() {
        return allowOfflineActivation;
    }

    public void setAllowOfflineActivation(boolean allowOfflineActivation) {
        this.allowOfflineActivation = allowOfflineActivation;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.marginRight = 10;
        glContainer.marginTop = 10;
        glContainer.marginLeft = 10;
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        glContainer.horizontalSpacing = 0;
        glContainer.verticalSpacing = 0;
        container.setLayout(glContainer);

        Label lblUsername = new Label(container, SWT.NONE);
        GridData gdLblUsername = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdLblUsername.widthHint = 73;
        lblUsername.setLayoutData(gdLblUsername);
        lblUsername.setText(StringConstants.USERNAME_TITLE);

        txtUserName = new Text(container, SWT.BORDER);
        GridData gdTxtUserName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtUserName.heightHint = 22;
        txtUserName.setLayoutData(gdTxtUserName);

        Composite compSeparate = new Composite(container, SWT.NONE);
        GridData gdCompSeparate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdCompSeparate.heightHint = 5;
        compSeparate.setLayoutData(gdCompSeparate);

        Label lblPassword = new Label(container, SWT.NONE);
        lblPassword.setText(StringConstants.PASSSWORD_TITLE);

        txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
        GridData gdTxtPassword = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
        gdTxtPassword.heightHint = 22;
        txtPassword.setLayoutData(gdTxtPassword);

        lblError = new Label(container, SWT.NONE);
        lblError.setAlignment(SWT.CENTER);
        GridData gdLblError = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        gdLblError.verticalIndent = 5;
        gdLblError.heightHint = 22;
        lblError.setLayoutData(gdLblError);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        Composite composite = new Composite(container, SWT.NONE);
        GridData gdComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        composite.setLayoutData(gdComposite);
        composite.setLayout(new GridLayout(9, false));

        btnClear = new Button(composite, SWT.NONE);
        GridData gdBtnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnClear.heightHint = 26;
        gdBtnClear.widthHint = 62;
        btnClear.setLayoutData(gdBtnClear);
        btnClear.setText(StringConstants.BTN_CLEAR_TILE);

        btnActivate = new Button(composite, SWT.NONE);
        GridData gdBtnActivate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnActivate.heightHint = 26;
        gdBtnActivate.widthHint = 72;
        btnActivate.setLayoutData(gdBtnActivate);
        btnActivate.setText(StringConstants.BTN_ACTIVATE_TITLE);

        Link linkForgotPass = new Link(composite, SWT.NONE);
        linkForgotPass.setText(StringConstants.LINK_LABEL_FORGOT_PASS_TEXT);

        Label lblVertialSeparate = new Label(composite, SWT.NONE);
        lblVertialSeparate.setText(StringConstants.SEPARATE_LINK);

        Link linkRegister = new Link(composite, SWT.NONE);
        linkRegister.setText(StringConstants.LINK_LABEL_REGISTER_TEXT);

        if (isAllowOfflineActivation()) {
            Label lblNewLabel = new Label(composite, SWT.NONE);
            lblNewLabel.setText(StringConstants.SEPARATE_LINK);
            addOfflineActivationLink(composite);
        }
        
        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setText(StringConstants.SEPARATE_LINK);

        Link linkConfigProxy = new Link(composite, SWT.NONE);
        linkConfigProxy.setText(MessageConstants.CONFIG_PROXY);
        linkConfigProxy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                new ProxyConfigurationDialog(ActivationDialog.this.getShell()).open();
            }
        });

        ActivateDialogKeyAdapter keyAdapter = new ActivateDialogKeyAdapter();
        txtUserName.addKeyListener(keyAdapter);
        txtPassword.addKeyListener(keyAdapter);

        ActivateDialogTextChanged textChangedListener = new ActivateDialogTextChanged();
        txtUserName.addModifyListener(textChangedListener);
        txtPassword.addModifyListener(textChangedListener);

        btnClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtPassword.setText("");
                txtUserName.setText("");
            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processActivate();
            }
        });
        linkForgotPass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                Program.launch(StringConstants.FORGOT_PASS_LINK);
            }
        });
        linkRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                try {
                    SignupDialog signupDialog = new SignupDialog(getShell());
                    if (signupDialog.open() == SignupDialog.OK) {
                        AuthenticationInfo authenticationInfo = signupDialog.getAuthenticationInfo();
                        setInitialKASettings(authenticationInfo.getEmail(), authenticationInfo.getPassword());
                        setReturnCode(Window.OK);
                        close();
                    }
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });

        enableActivateButton();

        return container;
    }

    private void addOfflineActivationLink(Composite composite) {
        Link linkOfflineActivation = new Link(composite, SWT.NONE);
        linkOfflineActivation.setText(StringConstants.LINK_OPEN_ACTIVATE_FORM_OFFLINE);
        linkOfflineActivation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                try {
                    int result = new ActivationOfflineDialog(ActivationDialog.this.getShell()).open();
                    if (result == Dialog.OK) {
                        setReturnCode(Window.OK);
                        close();
                    }
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });
    }

    private boolean isFullFillActivateInfo() {
        return txtUserName.getText().trim().length() > 0 && txtPassword.getText().trim().length() > 0;
    }

    protected void processActivate() {
        if (!isFullFillActivateInfo()) {
            return;
        }
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        lblError.setText(StringConstants.WAITTING_MESSAGE);

        String username = txtUserName.getText();
        String password = txtPassword.getText();
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                StringBuilder errorMessage = new StringBuilder();
                String machineId = MachineUtil.getMachineId();
                License license = ActivationInfoCollector.activate(username, password, machineId,
                        errorMessage);
                lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                if (license != null) {
                    setReturnCode(Window.OK);
                    close();
                } else {
                    lblError.setText(errorMessage.toString());
                }
            }
        });

        setInitialKASettings(username, password);
    }
    
    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ActivationPreferenceConstants.ACTIVATION_INFO_STORAGE);
    }

    
    public void setInitialKASettings(String txtUsername, String txtPassword) {
        ScopedPreferenceStore preferenceStore = getPreferenceStore();

            String email;
            try {
                email = CryptoUtil.encode(CryptoUtil.getDefault(txtUsername));
                String password = CryptoUtil.encode(CryptoUtil.getDefault(txtPassword));
                preferenceStore.setValue(ActivationPreferenceConstants.ACTIVATION_INFO_EMAIL,
                        email);
                preferenceStore.setValue(ActivationPreferenceConstants.ACTIVATION_INFO_PASSWORD,
                       password);
            } catch (UnsupportedEncodingException | GeneralSecurityException e1) {
                LoggerSingleton.logError(e1);
            }

        try {
            preferenceStore.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void enableActivateButton() {
        boolean enable = isFullFillActivateInfo();
        lblError.setText(isFullFillActivateInfo() ? "" : StringConstants.PROMT_ENTER_USERNAME_PASSWORD);
        btnActivate.setEnabled(enable);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConstants.DIA_TITLE_KS_ACTIVATION);
        newShell.setImage(ImageConstants.KATALON_IMAGE);
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.marginHeight = 0;
        parent.getShell().setDefaultButton(btnActivate);
    }

    private class ActivateDialogKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (((e.character) == SWT.CR) || ((e.character) == SWT.KEYPAD_CR)) {
                processActivate();
            }
        }
    }

    private class ActivateDialogTextChanged implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            enableActivateButton();
        }
    }
}
