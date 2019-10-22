package com.kms.katalon.composer.integration.analytics.dialog;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.handler.AnalyticsAuthorizationHandler;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class AuthenticationDialog extends Dialog {

    public static final int CONNECT_ID = 2;

    public static final int CANCEL_ID = 1;

    private Text email;

    private Text password;

    private Text serverUrl;

    private Label lblHelp;

    private Button btnConnect;

    private Button btnCancel;

    private AnalyticsSettingStore analyticsSettingStore;

    private AnalyticsTokenInfo tokenInfo;

    private boolean showPassword;

    public AuthenticationDialog(Shell parentShell, boolean showPassword) {
        super(parentShell);
        analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        this.showPassword = showPassword;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        btnConnect = createButton(parent, CONNECT_ID, StringConstants.BTN_CONNECT, true);
        btnCancel = createButton(parent, CANCEL_ID, StringConstants.BTN_CANCEL, false);
        enableLogin();
        addControlListeners();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData bodyGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        bodyGridData.widthHint = 500;
        body.setLayoutData(bodyGridData);
        body.setLayout(new GridLayout(1, false));

        Composite infoComposite = new Composite(body, SWT.NONE);
        infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        infoComposite.setLayout(new GridLayout(2, false));

        Label lblInformation = new Label(infoComposite, SWT.WRAP);
        lblInformation.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
        lblInformation.setText(StringConstants.LBL_INFORMATION);

        lblHelp = new Label(infoComposite, SWT.NONE);
        GridData gdLblHelp = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        lblHelp.setLayoutData(gdLblHelp);
        lblHelp.setImage(ImageManager.getImage(IImageKeys.HELP_16));
        lblHelp.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));

        Composite inputComposite = new Composite(body, SWT.NONE);
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        inputComposite.setLayout(new GridLayout(2, false));

        Label lblServerUrl = new Label(inputComposite, SWT.NONE);
        lblServerUrl.setText(StringConstants.LBL_SERVER_URL);
        serverUrl = new Text(inputComposite, SWT.BORDER);
        serverUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblUsername = new Label(inputComposite, SWT.NONE);
        lblUsername.setText(StringConstants.LBL_EMAIL);
        email = new Text(inputComposite, SWT.BORDER);
        email.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblPassword = new Label(inputComposite, SWT.NONE);
        lblPassword.setText(StringConstants.LBL_PASSWORD);
        GridData gdLblPassword = new GridData(SWT.FILL, SWT.FILL, false, false);
        lblPassword.setLayoutData(gdLblPassword);
        password = new Text(inputComposite, SWT.PASSWORD + SWT.BORDER);
        GridData gdPassword = new GridData(SWT.FILL, SWT.FILL, true, false);
        password.setLayoutData(gdPassword);

        Link linkPolicy = new Link(body, SWT.WRAP);
        linkPolicy.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        linkPolicy.setText(StringConstants.LBL_POLICY);
        linkPolicy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Program.launch(event.text);
            }
        });

        try {
            serverUrl.setText(analyticsSettingStore.getServerEndpoint());
            password.setText(analyticsSettingStore.getPassword());
            email.setText(analyticsSettingStore.getEmail());
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
        }

        gdLblPassword.exclude = !showPassword;
        serverUrl.setEnabled(showPassword);
        email.setEnabled(showPassword);
        lblPassword.setVisible(showPassword);
        gdPassword.exclude = !showPassword;
        password.setVisible(showPassword);

        return body;
    }

    private void updateDataStore(String email, String password) {
        try {
            analyticsSettingStore.enableIntegration(true);
        } catch (IOException e) {
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

        btnConnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleConnect();
            }
        });

        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }
        });

        lblHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                Program.launch(StringConstants.ANALYTICS_DOCUMENTATION_LINK);
            }
        });
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.INTEGRATION_DIALOG_TITLE);
    }

    private void enableLogin() {
        if (!StringUtils.isBlank(email.getText()) && !StringUtils.isBlank(password.getText())) {
            btnConnect.setEnabled(true);
        } else {
            btnConnect.setEnabled(false);
        }
    }

    private void handleConnect() {
        String emailText = email.getText();
        String passwordText = password.getText();
        String serverUrlText = serverUrl.getText();
        updateDataStore(emailText, passwordText);
        setReturnCode(CONNECT_ID);
        AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrlText, emailText, passwordText, analyticsSettingStore);
        setTokenInfo(tokenInfo);
        if (tokenInfo != null) {
            closeDialog();
        }
    }

    private void handleEnteredUsername() {
        enableLogin();
    }

    private void handleEnteredPassword() {
        enableLogin();
    }

    private void closeDialog() {
        this.close();
    }

    public AnalyticsTokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(AnalyticsTokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

}
