package com.kms.katalon.composer.integration.qtest.dialog;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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

import com.kms.katalon.composer.components.impl.control.GifCLabel;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;
import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.integration.qtest.QTestIntegrationAuthenticationManager;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.credential.impl.QTestCredentialImpl;
import com.kms.katalon.integration.qtest.exception.QTestException;

public class GenerateNewTokenDialog extends AbstractDialog {
    private Text txtServerUrl;
    private Text txtUsername;
    private Text txtPassword;

    private IQTestCredential fCredential;
    private GifCLabel connectingLabel;
    private Label lblConnecting;
    private Composite connectingComposite;
    private InputStream inputStream;
    private Job connectingJob;
    private Composite passwordComposite;
    private Button btnShowPassword;
    private Button chckEncryptPassword;

    public GenerateNewTokenDialog(Shell parentShell, IQTestCredential credential) {
        super(parentShell);
        fCredential = credential;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, StringConstants.DIA_TITLE_GENERATE, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        generateNewToken();
    }

    @Override
    protected void cancelPressed() {        
        if (connectingJob != null && connectingJob.getState() == Job.RUNNING) {
            connectingJob.cancel();
        }
        super.cancelPressed();
    }

    private boolean isDisposed() {
        Shell shell = getShell();
        return shell == null || shell.isDisposed();
    }

    private boolean generateNewToken() {
        final String newServerUrl = txtServerUrl.getText().trim();
        final String newUsername = txtUsername.getText();
        final String newPassword = txtPassword.getText();
        final boolean passwordEncryptionEnabled = chckEncryptPassword.getSelection();

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

        setConnectingCompositeVisible(true);
        getButton(OK).setEnabled(false);

        connectingJob = new Job(StringConstants.DIA_JOB_GENERATE_TOKEN) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    QTestCredentialImpl credentials = new QTestCredentialImpl().setServerUrl(newServerUrl)
                            .setUsername(newUsername).setPassword(newPassword).setVersion(fCredential.getVersion())
                            .setPasswordEncryptionEnabled(passwordEncryptionEnabled);;

                    credentials.setToken(QTestIntegrationAuthenticationManager.getToken(credentials));

                    fCredential = credentials;

                    return Status.OK_STATUS;
                } catch (final QTestException e) {
                    if (!monitor.isCanceled() && !isDisposed()) {
                        UISynchronizeService.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR,
                                        e.getLocalizedMessage());
                            }
                        });
                    }
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };

        connectingJob.setUser(false);
        connectingJob.schedule();
        connectingJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (isDisposed()) {
                    return;
                }
                
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        getButton(OK).setEnabled(true);
                        setConnectingCompositeVisible(false);
                    }                    
                });
                
                if (!event.getResult().isOK()) {
                    return;
                }                

                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        setReturnCode(OK);
                        close();
                    }
                });
            }
        });
        return true;
    }

    private void setConnectingCompositeVisible(boolean isConnectingCompositeVisible) {
        if (isConnectingCompositeVisible) {
            try {
                inputStream = ImageConstants.URL_16_LOADING.openStream();
                connectingLabel.setGifImage(inputStream);
                connectingComposite.layout(true, true);
            } catch (IOException ex) {
            } finally {
                if (inputStream != null) {
                    closeQuietlyWithLog(inputStream);
                    inputStream = null;
                }
            }
        } else {
            if (inputStream != null) {
                closeQuietlyWithLog(inputStream);
                inputStream = null;
            }
        }

        connectingComposite.setVisible(isConnectingCompositeVisible);
    }

    private void closeQuietlyWithLog(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    public IQTestCredential getNewCredential() {
        return fCredential;
    }

    private void setText(Text text, String value) {
        text.setText(value != null ? value : "");
    }

    @Override
    protected void registerControlModifyListeners() {
        btnShowPassword.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updatePasswordField();
            }
        });
    }

    private void updatePasswordField() {
        if (btnShowPassword.getSelection()) {
            // show password
            txtPassword.setEchoChar('\0');
        } else {
            txtPassword.setEchoChar(GlobalStringConstants.CR_ECO_PASSWORD.charAt(0));
        }
    }

    @Override
    protected void setInput() {
        if (fCredential == null) {
            fCredential = new QTestCredentialImpl();
        }

        setText(txtServerUrl, fCredential.getServerUrl());
        setText(txtUsername, fCredential.getUsername());
        setText(txtPassword, fCredential.getPassword());
        chckEncryptPassword.setSelection(fCredential.isEncryptionEnabled());
        updatePasswordField();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.horizontalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        container.setLayout(glContainer);

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

        passwordComposite = new Composite(container, SWT.NONE);
        passwordComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glPasswordComposite = new GridLayout(2, false);
        glPasswordComposite.marginHeight = 0;
        glPasswordComposite.marginWidth = 0;
        passwordComposite.setLayout(glPasswordComposite);

        txtPassword = new Text(passwordComposite, SWT.BORDER);
        GridData gdTxtData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtData.widthHint = 200;
        txtPassword.setLayoutData(gdTxtData);

        btnShowPassword = new Button(passwordComposite, SWT.CHECK);
        btnShowPassword.setText(StringConstants.WZ_P_AUTHENTICATION_SHOW_PASSWORD);
        
        chckEncryptPassword = new Button(container, SWT.CHECK);
        chckEncryptPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 2, 1));
        chckEncryptPassword.setText(ComposerIntegrationQtestMessageConstants.WZ_P_AUTHENTICATION_ENCRYPT_AUTHENTICATION_DATA);

        connectingComposite = new Composite(container, SWT.NONE);
        connectingComposite.setLayout(new GridLayout(2, false));
        connectingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

        connectingLabel = new GifCLabel(connectingComposite, SWT.DOUBLE_BUFFERED);
        connectingLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        lblConnecting = new Label(connectingComposite, SWT.NONE);
        lblConnecting.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblConnecting.setText(StringConstants.CM_CONNECTING);
        setConnectingCompositeVisible(false);

        return container;
    }

    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_GENERATE_TOKEN;
    }
}
