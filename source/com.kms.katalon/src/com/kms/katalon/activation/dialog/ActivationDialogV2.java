package com.kms.katalon.activation.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.logging.LogUtil;

public class ActivationDialogV2 extends AbstractDialog {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public static final int REQUEST_SIGNUP_CODE = 1001;

    public static final int REQUEST_OFFLINE_CODE = 1002;

    private Text txtEmail;

    private Text txtPassword;

    private Label lblProgressMessage;

    private Link lnkSwitchToSignupDialog;

    private Button btnLogIn;

    private Button btnActivate;

    private Combo cbbOrganization;

    private Link lnkConfigProxy;

    private Link lnkOfflineActivation;

    private Link lnkForgotPassword;
    
    private Link lblHelpOrganization;

    private List<AnalyticsOrganization> organizations = new ArrayList<>();

    public ActivationDialogV2(Shell parentShell) {
        super(parentShell, false);
    }

    private boolean validateInput() {
        return validateEmail() && validatePassword();
    }

    @Override
    protected void registerControlModifyListeners() {
        ModifyListener modifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                btnLogIn.setEnabled(validateInput());
            }
        };

        txtEmail.addModifyListener(modifyListener);
        txtPassword.addModifyListener(modifyListener);

        lnkSwitchToSignupDialog.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(MessageConstants.ActivationDialogV2_LNK_SIGNUP);
            }
        });

        lnkConfigProxy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new ProxyConfigurationDialog(getShell()).open();
            }
        });

        lnkForgotPassword.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(StringConstants.FORGOT_PASS_LINK);
            }
        });

        lnkOfflineActivation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(MessageConstants.ActivationDialogV2_LNK_OFFLINE_ACTIVATE);
            }
        });

        btnLogIn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String username = txtEmail.getText();
                String password = txtPassword.getText();
                btnLogIn.setEnabled(false);
                Executors.newFixedThreadPool(1).submit(() -> {
                    UISynchronizeService.syncExec(
                            () -> setProgressMessage(MessageConstants.ActivationDialogV2_MSG_ACTIVATING, false));
                    StringBuilder errorMessage = new StringBuilder();
                    boolean result = ActivationInfoCollector.activate(username, password, errorMessage);

                    UISynchronizeService.syncExec(() -> {
                        btnLogIn.setEnabled(true);
                        if (result) {
                            setReturnCode(Window.OK);
                            txtEmail.setEnabled(false);
                            txtPassword.setEnabled(false);
                            btnLogIn.setEnabled(false);
                            getOrganizations();
                        } else {
                            setProgressMessage(errorMessage.toString(), true);
                        }
                    });
                });
            }
        });
        
        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = cbbOrganization.getSelectionIndex();
                save(index);
            }
        });
        
        lblHelpOrganization.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }

    private void save(int index) {
        try {
            AnalyticsOrganization organization = organizations.get(index);
            String email = txtEmail.getText();
            String password = txtPassword.getText();
            ActivationInfoCollector.markActivated(email, password);
            ApplicationInfo.setAppProperty(ApplicationStringConstants.KA_ORGANIZATION, JsonUtil.toJson(organization), true);
            if (KatalonApplicationActivator.getFeatureActivator() != null) {
                String serverUrl = ApplicationInfo.getTestOpsServer();
                String ksVersion = VersionUtil.getCurrentVersion().getVersion();
                Long orgId = organization.getId();
                ActivationInfoCollector.activateFeatures(serverUrl, email, password, orgId, ksVersion);
            }
            close();
        } catch (Exception e) {
            LogUtil.logError(e, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
        }
    }
    
    private static List<String> getOrganizationNames(List<AnalyticsOrganization> organizations) {
        List<String> names = organizations.stream().map(organization -> organization.getName()).collect(Collectors.toList());
        return names;
    }
    
    private void getOrganizations() {
        Executors.newFixedThreadPool(1).submit(() -> {
            UISynchronizeService.syncExec(
                    () -> setProgressMessage(MessageConstants.ActivationDialogV2_MSG_GETTING_ORGANIZATION, false));
            UISynchronizeService.syncExec(() -> {
                AnalyticsTokenInfo token;
                try {
                    String serverUrl = ApplicationInfo.getTestOpsServer();
                    String email = txtEmail.getText();
                    String password = txtPassword.getText();
                    token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                    organizations = AnalyticsApiProvider.getOrganizations(serverUrl, token.getAccess_token());
                    if (organizations.size() == 1) {
                        save(0);
	                } else {
	                    cbbOrganization.setItems(getOrganizationNames(organizations).toArray(new String[organizations.size()]));
	                    cbbOrganization.select(0);
	                    setProgressMessage("", false);
	                    cbbOrganization.setEnabled(true);
	                    btnActivate.setEnabled(true);
	                }
                } catch (AnalyticsApiExeception e) {
                    LogUtil.logError(e);
                    setProgressMessage("", false);
                    MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(),
                            MessageConstants.ActivationDialogV2_LBL_ERROR, null,
                            MessageConstants.ActivationDialogV2_LBL_ERROR_ORGANIZATION, MessageDialog.ERROR,
                            new String[] { "OK" }, 0);
                    if (dialog.open() == Dialog.OK) {
                        txtEmail.setEnabled(true);
                        txtPassword.setEnabled(true);
                        btnLogIn.setEnabled(true);
                    }
                }
            });
        });
    }

    private void setProgressMessage(String message, boolean isError) {
        lblProgressMessage.setText(message);
        if (isError) {
            lblProgressMessage.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lblProgressMessage.setForeground(ColorUtil.getDefaultTextColor());
        }
        lblProgressMessage.getParent().layout();
    }

    @Override
    protected void setInput() {
        btnLogIn.setEnabled(validateInput());
    }

    private boolean validateEmail() {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail.getText()).find();
    }

    private boolean validatePassword() {
        return txtPassword.getText().length() >= 8;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        Composite contentComposite = new Composite(container, SWT.NONE);
        GridLayout glContent = new GridLayout(2, false);
        glContent.verticalSpacing = 10;
        contentComposite.setLayout(glContent);

        GridData gdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdText.heightHint = 22;

        Label lblEmail = new Label(contentComposite, SWT.NONE);
        GridData gdEmail = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        lblEmail.setLayoutData(gdEmail);
        lblEmail.setText(StringConstants.EMAIL);

        txtEmail = new Text(contentComposite, SWT.BORDER);
        txtEmail.setLayoutData(gdText);

        Label lblPassword = new Label(contentComposite, SWT.NONE);
        GridData gdPassword = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        lblPassword.setLayoutData(gdPassword);
        lblPassword.setText(StringConstants.PASSSWORD_TITLE);

        txtPassword = new Text(contentComposite, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(gdText);

        Composite logInComposite = new Composite(contentComposite, SWT.NONE);
        logInComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        GridLayout gdLogInComposite = new GridLayout(2, false);
        logInComposite.setLayout(gdLogInComposite);
        
        lblProgressMessage = new Label(logInComposite, SWT.NONE);
        lblProgressMessage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        Composite logInRightComposite = new Composite(logInComposite, SWT.NONE);
        logInRightComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, true, false));
        GridLayout gdActivateRight = new GridLayout(1, false);
        logInRightComposite.setLayout(gdActivateRight);
        
        GridData gdBtn = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gdBtn.widthHint = 100;
        
        btnLogIn = new Button(logInRightComposite, SWT.NONE);
        btnLogIn.setLayoutData(gdBtn);
        btnLogIn.setText(StringConstants.BTN_LOG_IN_TITLE);
        getShell().setDefaultButton(btnLogIn);
        
        Label lblOrganization = new Label(contentComposite, SWT.NONE);
        GridData gdOrganization = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        lblOrganization.setLayoutData(gdOrganization);
        lblOrganization.setText(MessageConstants.ActivationDialogV2_LBL_SELECT_ORGANIZATION);
        
        cbbOrganization = new Combo(contentComposite, SWT.READ_ONLY);
        cbbOrganization.setLayoutData(gdText);
        cbbOrganization.setEnabled(false);
        
        Composite activateComposite = new Composite(contentComposite, SWT.NONE);
        activateComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
        GridLayout gdTemp = new GridLayout(2, false);
        activateComposite.setLayout(gdTemp);
        
        Composite activateLeftComposite = new Composite(activateComposite, SWT.NONE);
        activateLeftComposite.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false));
        activateLeftComposite.setLayout(new GridLayout(1, false));
        
        lblHelpOrganization = new Link(activateLeftComposite, SWT.NONE);
        lblHelpOrganization.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        lblHelpOrganization.setText(String.format(MessageConstants.ActivationDialogV2_LNK_SEE_MORE_ORGANIZATION, ApplicationInfo.getTestOpsServer()));
        
        Composite activateRightComposite = new Composite(activateComposite, SWT.NONE);
        activateRightComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, true, false));
        GridLayout gdActivateComposite = new GridLayout(1, false);
        activateRightComposite.setLayout(gdActivateComposite);
        
        btnActivate = new Button(activateRightComposite, SWT.NONE);
        btnActivate.setLayoutData(gdBtn);
        btnActivate.setText(StringConstants.BTN_ACTIVATE_TITLE);
        btnActivate.setEnabled(false);
        
        return container;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite buttonBar = new Composite(parent, SWT.NONE);
        GridLayout glButtonBar = new GridLayout(1, false);
        glButtonBar.marginWidth = 0;
        glButtonBar.verticalSpacing = 10;
        buttonBar.setLayout(glButtonBar);
        buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite bottomBar = new Composite(buttonBar, SWT.NONE);
        bottomBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdBottomBar = new GridLayout(2, false);
        gdBottomBar.marginWidth = 5;
        gdBottomBar.marginHeight = 0;
        bottomBar.setLayout(gdBottomBar);

        Composite bottomLeftComposite = new Composite(bottomBar, SWT.NONE);
        bottomLeftComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottomLeftComposite.setLayout(new GridLayout(2, false));

        Label lblAskForAccount = new Label(bottomLeftComposite, SWT.NONE);
        lblAskForAccount.setText(MessageConstants.ActivationDialogV2_LBL_ASK_FOR_REGISTER);

        lnkSwitchToSignupDialog = new Link(bottomLeftComposite, SWT.NONE);
        lnkSwitchToSignupDialog.setText(String.format("<a>%s</a>", MessageConstants.ActivationDialogV2_LNK_REGISTER));

        Composite linkBar = new Composite(buttonBar, SWT.NONE);
        linkBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        linkBar.setLayout(new GridLayout(5, false));
        
        lnkForgotPassword = new Link(linkBar, SWT.NONE);
        lnkForgotPassword.setText(String.format("<a>%s</a>", MessageConstants.ActivationDialogV2_LNK_RESET_PASSWORD));
        lnkForgotPassword.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        Label label = new Label(linkBar, SWT.SEPARATOR);
        GridData gdSeparator = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gdSeparator.heightHint = 22;
        label.setLayoutData(gdSeparator);

        lnkOfflineActivation = new Link(linkBar, SWT.NONE);
        lnkOfflineActivation
                .setText(String.format("<a>%s</a>", MessageConstants.ActivationDialogV2_LNK_OFFLINE_ACTIVATION));
        lnkOfflineActivation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        Label label2 = new Label(linkBar, SWT.SEPARATOR);
        label2.setLayoutData(gdSeparator);

        lnkConfigProxy = new Link(linkBar, SWT.NONE);
        lnkConfigProxy.setText(MessageConstants.CONFIG_PROXY);
        lnkConfigProxy.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        return buttonBar;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // Do nothing
    }

    @Override
    public String getDialogTitle() {
        return MessageConstants.DIA_TITLE_KS_ACTIVATION;
    }

    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), initialSize.y);
    }
}