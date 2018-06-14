package com.kms.katalon.composer.integration.analytics.dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class AuthenticationDialog extends Dialog {

    private Text email;

    private Text password;

    private Text serverUrl;

    private Button btnLogin;

    private Button btnCancel;

    private AnalyticsSettingStore analyticsSettingStore;

    private List<AnalyticsTeam> teams = new ArrayList<>();

    private AuthenticationDialog(Shell parentShell) {
        super(parentShell);
        analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
    }

    public static AuthenticationDialog createDefault(Shell parentShell) {
        return new AuthenticationDialog(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData bodyGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        bodyGridData.widthHint = 400;
        body.setLayoutData(bodyGridData);
        body.setLayout(new GridLayout(1, false));

        Composite inputComposite = new Composite(body, SWT.NONE);
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        inputComposite.setLayout(new GridLayout(2, false));

        Label lblServerUrl = new Label(inputComposite, SWT.NONE);
        lblServerUrl.setText(StringConstants.LBL_SERVER_URL);
        serverUrl = new Text(inputComposite, SWT.BORDER);
        serverUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblusername = new Label(inputComposite, SWT.NONE);
        lblusername.setText(StringConstants.LBL_EMAIL);
        email = new Text(inputComposite, SWT.BORDER);
        email.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblpassword = new Label(inputComposite, SWT.NONE);
        lblpassword.setText(StringConstants.LBL_PASSWORD);
        password = new Text(inputComposite, SWT.PASSWORD + SWT.BORDER);
        password.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite buttonComposite = new Composite(body, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        buttonComposite.setLayout(new GridLayout(2, false));

        btnLogin = new Button(buttonComposite, SWT.FLAT);
        btnLogin.setText(StringConstants.BTN_LOGIN);
        btnLogin.setEnabled(false);

        btnCancel = new Button(buttonComposite, SWT.FLAT);
        btnCancel.setText(StringConstants.BTN_CANCEL);

        try {
            serverUrl.setText(analyticsSettingStore.getServerEndpoint(true));
            email.setText(analyticsSettingStore.getEmail(true));
            password.setText(analyticsSettingStore.getPassword(true));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        } catch (GeneralSecurityException e) {
            LoggerSingleton.logError(e);
        }

        enableLogin();
        addControlListeners();
        return body;
    }

    private void updateDataStore(String email, String password) {
        try {
            boolean encryptionEnabled = true;
            analyticsSettingStore.enableIntegration(true);
            analyticsSettingStore.enableEncryption(encryptionEnabled);
            analyticsSettingStore.setServerEndPoint(serverUrl.getText(), encryptionEnabled);
            analyticsSettingStore.setEmail(email, encryptionEnabled);
            analyticsSettingStore.setPassword(password, encryptionEnabled);
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
    }

    private void addControlListeners() {

        email.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                handleEnteredUsername();
            }
        });

        password.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                handleEnteredPassword();
            }
        });

        btnLogin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleLogin();
            }
        });

        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }
        });
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.AUTHENTICATION_DIALOG_TITLE);
    }

    private void enableLogin() {
        if (!StringUtils.isBlank(email.getText()) && !StringUtils.isBlank(password.getText())) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    private void handleLogin() {
        try {
            String emailText = email.getText();
            String passwordText = password.getText();
            new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask("Requesting token...", 2);
                        monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER);
                        // updateDataStore(emailText, passwordText);
                        UISynchronizeService.syncExec(() -> updateDataStore(emailText, passwordText));
                        final AnalyticsTokenInfo tokenInfo = AnalyticsApiProvider
                                .requestToken(analyticsSettingStore.getServerEndpoint(true), emailText, passwordText);

                        // monitor.worked(1);
                        // monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_TEAMS);
                        // final List<AnalyticsTeam> loaded =
                        // AnalyticsApiProvider.getTeams(serverUrl,
                        // tokenInfo.getAccess_token());
                        // if (loaded != null && !loaded.isEmpty()) {
                        // teams.addAll(loaded);
                        // }
                        // open Push dialog here
                        // monitor.worked(1);
                        // closeDialog();
                        System.out.println("correct");
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof AnalyticsApiExeception) {
                MessageDialog.openError(getShell(), ComposerAnalyticsStringConstants.ERROR, cause.getMessage());
            } else {
                LoggerSingleton.logError(cause);
            }
        } catch (InterruptedException e) {
            // Ignore this
        }
    }

    private void handleEnteredUsername() {
        enableLogin();
        // String enteredEmail = email.getText();
        // if (!StringUtils.isEmpty(enteredEmail)) {
        // try {
        //
        // } catch (Exception e) {
        // LoggerSingleton.logError(e);
        // }
        // } else {
        // }

    }

    private void handleEnteredPassword() {
        enableLogin();
        // String enteredPassword = password.getText();
        // if (!StringUtils.isEmpty(enteredPassword)) {
        // try {
        //
        // } catch (Exception e) {
        // LoggerSingleton.logError(e);
        // }
        // } else {
        // }
    }

    private void closeDialog() {
        this.close();
    }

}
