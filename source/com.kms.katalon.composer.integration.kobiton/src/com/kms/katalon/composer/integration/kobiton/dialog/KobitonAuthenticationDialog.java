package com.kms.katalon.composer.integration.kobiton.dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerIntegrationKobitonMessageConstants;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerKobitonStringConstants;
import com.kms.katalon.integration.kobiton.entity.KobitonApiKey;
import com.kms.katalon.integration.kobiton.entity.KobitonLoginInfo;
import com.kms.katalon.integration.kobiton.exceptions.KobitonApiException;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;
import com.kms.katalon.integration.kobiton.providers.KobitonApiProvider;

public class KobitonAuthenticationDialog extends Dialog {
    private static final String SEPARATE_LINK = "|"; //$NON-NLS-1$

    private static final Character PASSWORD_CHAR_MASK = (char) 0x25cf;

    private Button btnOk;

    private Button btnClear;

    private Text txtUsername;

    private Text txtPassword;

    private Label lblError;

    public KobitonAuthenticationDialog(Shell parentShell) {
        super(parentShell);
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
        lblUsername.setText(ComposerIntegrationKobitonMessageConstants.LBL_DLG_AUTHENTICATE_USERNAME);

        txtUsername = new Text(container, SWT.BORDER);
        GridData gdTxtUserName = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
        gdTxtUserName.widthHint = 356;
        gdTxtUserName.heightHint = 22;
        txtUsername.setLayoutData(gdTxtUserName);

        Composite compSeparate = new Composite(container, SWT.NONE);
        GridData gdCompSeparate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdCompSeparate.heightHint = 5;
        compSeparate.setLayoutData(gdCompSeparate);

        Label lblPassword = new Label(container, SWT.NONE);
        lblPassword.setText(ComposerIntegrationKobitonMessageConstants.LBL_DLG_AUTHENTICATE_PASSWORD);

        txtPassword = new Text(container, SWT.BORDER);
        GridData gdTxtPassword = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
        gdTxtPassword.widthHint = 356;
        gdTxtPassword.heightHint = 22;
        txtPassword.setLayoutData(gdTxtPassword);
        txtPassword.setEchoChar(PASSWORD_CHAR_MASK);

        lblError = new Label(container, SWT.NONE);
        lblError.setAlignment(SWT.CENTER);
        GridData gdLblError = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdLblError.verticalIndent = 5;
        gdLblError.widthHint = 432;
        gdLblError.heightHint = 22;
        lblError.setLayoutData(gdLblError);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        Composite composite = new Composite(container, SWT.NONE);
        GridData gdComposite = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
//        gdComposite.widthHint = 430;
        composite.setLayoutData(gdComposite);
        composite.setLayout(new GridLayout(9, false));

        btnClear = new Button(composite, SWT.NONE);
        GridData gdBtnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnClear.heightHint = 26;
        gdBtnClear.widthHint = 62;
        btnClear.setLayoutData(gdBtnClear);
        btnClear.setText(ComposerKobitonStringConstants.CLEAR);

        btnOk = new Button(composite, SWT.NONE);
        GridData gdBtnActivate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnActivate.heightHint = 26;
        gdBtnActivate.widthHint = 72;
        btnOk.setLayoutData(gdBtnActivate);
        btnOk.setText(ComposerKobitonStringConstants.OK);

        Link linkForgotPass = new Link(composite, SWT.NONE);
        linkForgotPass.setText(ComposerIntegrationKobitonMessageConstants.LBL_LINK_DLG_AUTHENTICATE_FORGOT_PASSWORD);

        Label lblVertialSeparate = new Label(composite, SWT.NONE);
        lblVertialSeparate.setText(SEPARATE_LINK);

        Link linkRegister = new Link(composite, SWT.NONE);
        linkRegister.setText(ComposerIntegrationKobitonMessageConstants.LBL_LINK_DLG_AUTHENTICATE_REGISTER);

        KobitonAuthenticateDialogKeyAdapter keyAdapter = new KobitonAuthenticateDialogKeyAdapter();
        txtUsername.addKeyListener(keyAdapter);
        txtPassword.addKeyListener(keyAdapter);

        KobitonAuthenticateDialogTextChanged textChangedListener = new KobitonAuthenticateDialogTextChanged();
        txtUsername.addModifyListener(textChangedListener);
        txtPassword.addModifyListener(textChangedListener);

        btnClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtPassword.setText(""); //$NON-NLS-1$
                txtUsername.setText(""); //$NON-NLS-1$
            }
        });

        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processLogin();
            }
        });
        SelectionAdapter linkSelectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Program.launch(e.text);
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        };
        linkForgotPass.addSelectionListener(linkSelectionListener);
        linkRegister.addSelectionListener(linkSelectionListener);

        enableOKButton();

        return container;
    }

    private boolean isFullFillAuthenticateInfo() {
        return txtUsername.getText().trim().length() > 0 && txtPassword.getText().trim().length() > 0;
    }

    protected void processLogin() {
        if (!isFullFillAuthenticateInfo()) {
            return;
        }
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        lblError.setText(ComposerIntegrationKobitonMessageConstants.MSG_DLG_AUTHENTICATE_LOGIN_TO_KOBITON);
        final String userName = txtUsername.getText();
        final String password = txtPassword.getText();
        try {
            new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_RETRIEVING_KEYS, 2);
                        monitor.subTask(ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER);
                        KobitonLoginInfo loginInfo = KobitonApiProvider.login(userName, password);
                        KobitonPreferencesProvider.saveKobitonUserName(loginInfo.getUser().getUsername());
                        KobitonPreferencesProvider.saveKobitonPassword(password);
                        KobitonPreferencesProvider.saveKobitonToken(loginInfo.getToken());
                        monitor.worked(1);
                        monitor.subTask(ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_GETTING_KEYS);
                        List<KobitonApiKey> apiKeys = KobitonApiProvider.getApiKeyList(loginInfo.getToken());
                        if (!apiKeys.isEmpty()) {
                            KobitonPreferencesProvider.saveKobitonApiKey(apiKeys.get(0).getKey());
                        }
                        monitor.worked(1);
                    } catch (URISyntaxException | IOException | KobitonApiException e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            setReturnCode(Window.OK);
            close();
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof KobitonApiException) {
                lblError.setText(ComposerKobitonStringConstants.ERROR + ": " + cause.getMessage()); //$NON-NLS-1$
                lblError.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            } else {
                LoggerSingleton.logError(cause);
            }
        } catch (InterruptedException e) {
            // Ignore this
        }
    }

    private void enableOKButton() {
        boolean enable = isFullFillAuthenticateInfo();
        lblError.setText(isFullFillAuthenticateInfo()
                ? "" : ComposerIntegrationKobitonMessageConstants.MSG_INFO_DLG_AUTHENTICATE_ENTER_USERNAME_PASSWORD); //$NON-NLS-1$
        btnOk.setEnabled(enable);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(ComposerIntegrationKobitonMessageConstants.TITLE_DLG_AUTHENTICATE);
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.marginHeight = 0;
        parent.getShell().setDefaultButton(btnOk);
    }

    private class KobitonAuthenticateDialogKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (((e.character) == SWT.CR) || ((e.character) == SWT.KEYPAD_CR)) {
                processLogin();
            }
        }
    }

    private class KobitonAuthenticateDialogTextChanged implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            enableOKButton();
        }
    }
}
