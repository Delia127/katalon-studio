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
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.analytics.entity.AnalyticsLicenseKey;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganizationRole;
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

    private Button btnActivate;

    private Button btnSave;

    private Combo cbbOrganization;

    private Label lblMachineKeyDetail;

    private Link lnkConfigProxy;

    private Link lnkOfflineActivation;

    private Link lnkForgotPassword;
    
    private Link lblHelpOrganization;

    private List<AnalyticsOrganization> organizations = new ArrayList<>();
    
    private AnalyticsLicenseKey licenseKey;
    
    private String machineKey;

    private Link lnkAgreeTerm;
    
    private Composite compositeOrganization;
    
    private boolean isExpanded;
    
    public ActivationDialogV2(Shell parentShell) {
        super(parentShell, false);
        machineKey = MachineUtil.getMachineId();
        isExpanded = false;
    }

    private boolean validateInput() {
        return validateEmail() && validatePassword();
    }

    @Override
    protected void registerControlModifyListeners() {
        ModifyListener modifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                btnActivate.setEnabled(validateInput());
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
        
        lnkAgreeTerm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(StringConstants.AGREE_TERM_URL);
            }
        });

        lnkOfflineActivation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(REQUEST_OFFLINE_CODE);
                close();
            }
        });

        lnkOfflineActivation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(MessageConstants.ActivationDialogV2_LNK_OFFLINE_ACTIVATE);
            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String username = txtEmail.getText();
                String password = txtPassword.getText();
                btnActivate.setEnabled(false);
                Executors.newFixedThreadPool(1).submit(() -> {
                    UISynchronizeService.syncExec(
                            () -> {
                            	setProgressMessage(MessageConstants.ActivationDialogV2_MSG_LOGIN, false);
                            });
                    StringBuilder errorMessage = new StringBuilder();
                    boolean result = ActivationInfoCollector.activate(username, password, errorMessage);
                    UISynchronizeService.syncExec(() -> {
                        btnActivate.setEnabled(true);
                        if (result) {
                            setReturnCode(Window.OK);
                            layoutExecutionCompositeListener();
                            txtEmail.setEnabled(false);
                            txtPassword.setEnabled(false);
                            btnActivate.setEnabled(false);
                            getOrganizations();
                        } else {
                            setProgressMessage(errorMessage.toString(), true);
                        }
                    });
                });
            }
        });
        
        btnSave.addSelectionListener(new SelectionAdapter() {
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
        AnalyticsOrganization organization = organizations.get(index);
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        Executors.newFixedThreadPool(1).submit(() -> {
            UISynchronizeService
                    .syncExec(() -> setProgressMessage(MessageConstants.ActivationDialogV2_MSG_GETTING_FEATURE, false));
            UISynchronizeService.syncExec(() -> {
                try {
                    getLicenseKey(organizations.get(0).getId());
                    if (licenseKey != null) {
                        ActivationInfoCollector.markActivated(email, password);
                        ApplicationInfo.setAppProperty(ApplicationStringConstants.KA_ORGANIZATION,
                                JsonUtil.toJson(organization), true);
                        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE,
                                licenseKey.getValue(), true);
                        if (KatalonApplicationActivator.getFeatureActivator() != null) {
                            String serverUrl = ApplicationInfo.getTestOpsServer();
                            String ksVersion = VersionUtil.getCurrentVersion().getVersion();
                            Long orgId = organization.getId();
                            ActivationInfoCollector.activateFeatures(serverUrl, email, password, orgId, ksVersion);
                        }
                        close();
                        Program.launch(MessageConstants.URL_KATALON_ENTERPRISE);
                    } else {
                        txtEmail.setEnabled(true);
                        txtPassword.setEnabled(true);
                        btnActivate.setEnabled(true);
                        btnSave.setEnabled(false);
                    }
                } catch (Exception e) {
                    LogUtil.logError(e, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
                }
            });
        });
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
                    cbbOrganization.setItems(getOrganizationNames(organizations).toArray(new String[organizations.size()]));
                    cbbOrganization.select(getDefaultOrganizationIndex());

                    setProgressMessage("", false);
                    cbbOrganization.setEnabled(true);
                    btnSave.setEnabled(true);
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
                        btnActivate.setEnabled(true);
                        btnSave.setEnabled(false);
                    }
                }
                if (organizations.size() == 1) {
                    save(0);
                }
            });
        });
    }
    
    private int getDefaultOrganizationIndex() {
        int selectionIndex = 0;
        for (int i = 0; i < organizations.size(); i++) {
            AnalyticsOrganization organization = organizations.get(i);
            if (organization.getRole().equals(AnalyticsOrganizationRole.USER)) {
                selectionIndex = i;
                return selectionIndex;
            }
        }
    	return selectionIndex;
    }

    private void getLicenseKey(long orgId) {
        AnalyticsTokenInfo token;
        try {
            String serverUrl = ApplicationInfo.getTestOpsServer();
            String email = txtEmail.getText();
            String password = txtPassword.getText();
            token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
            licenseKey = AnalyticsApiProvider.getLicenseKey(serverUrl, machineKey, token.getAccess_token());
        } catch (AnalyticsApiExeception e) {
            LogUtil.logError(e);
        }
    }

    private void setProgressMessage(String message, boolean isError) {
        lblProgressMessage.setText(message);
        if (isError) {
            lblProgressMessage.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lblProgressMessage.setForeground(ColorUtil.getTextRunningColor());
        }
        lblProgressMessage.getParent().layout();
    }

    @Override
    protected void setInput() {
        btnActivate.setEnabled(validateInput());
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
        txtEmail.setText("lieuhuynh@kms-technology.com");

        Label lblPassword = new Label(contentComposite, SWT.NONE);
        GridData gdPassword = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        lblPassword.setLayoutData(gdPassword);
        lblPassword.setText(StringConstants.PASSSWORD_TITLE);

        txtPassword = new Text(contentComposite, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(gdText);
        txtPassword.setText("12345678");
        
    	Label lblMachineKey = new Label(contentComposite, SWT.NONE);
        lblMachineKey.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblMachineKey.setText(MessageConstants.ActivationOfflineDialogV2_LBL_MACHINE_KEY);

        lblMachineKeyDetail = new Label(contentComposite, SWT.NONE);
        lblMachineKeyDetail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        lblMachineKeyDetail.setForeground(ColorUtil.getTextLinkColor());
        lblMachineKeyDetail.setText(machineKey);

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

        btnActivate = new Button(logInRightComposite, SWT.NONE);
        btnActivate.setLayoutData(gdBtn);
        btnActivate.setText(StringConstants.BTN_LOG_IN_TITLE);
        getShell().setDefaultButton(btnActivate);
        
        createComponentOrganization(contentComposite);
        
        layoutOrganization();
        
        return container;
    }
    
    private void createComponentOrganization(Composite contentComposite) {
    	compositeOrganization = new Composite(contentComposite, SWT.NONE);
    	
        GridData gdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdText.heightHint = 30;
    	
        GridLayout glCompositeOrganization = new GridLayout(2, true);
        glCompositeOrganization.verticalSpacing = 10;
        compositeOrganization.setLayout(glCompositeOrganization);
        compositeOrganization.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        


        Label lblOrganization = new Label(compositeOrganization, SWT.NONE);
        GridData gdOrganization = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        lblOrganization.setLayoutData(gdOrganization);
        lblOrganization.setText(MessageConstants.ActivationDialogV2_LBL_SELECT_ORGANIZATION);

        cbbOrganization = new Combo(compositeOrganization, SWT.READ_ONLY);
        cbbOrganization.setLayoutData(gdText);
        cbbOrganization.setEnabled(false);

        Composite activateComposite = new Composite(compositeOrganization, SWT.NONE);
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

        GridData gdBtn = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gdBtn.widthHint = 100;
        
        btnSave = new Button(activateRightComposite, SWT.NONE);
        btnSave.setLayoutData(gdBtn);
        btnSave.setText(StringConstants.BTN_ACTIVATE_TITLE);
        btnSave.setEnabled(false);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite buttonBar = new Composite(parent, SWT.NONE);
        GridLayout glButtonBar = new GridLayout(1, false);
        glButtonBar.marginWidth = 0;
        glButtonBar.verticalSpacing = 10;
        buttonBar.setLayout(glButtonBar);
        buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite bottomTerm = new Composite(buttonBar, SWT.NONE);
        bottomTerm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdBottomBarTerm = new GridLayout(2, false);
        gdBottomBarTerm.marginWidth = 10;
        gdBottomBarTerm.marginHeight = 0;
        bottomTerm.setLayout(gdBottomBarTerm);
        
        lnkAgreeTerm = new Link(bottomTerm, SWT.WRAP);
        lnkAgreeTerm.setText(MessageConstants.ActivationDialogV2_LBL_AGREE_TERM);
        
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
    
    private void layoutExecutionCompositeListener() {
    	isExpanded = !isExpanded;
        layoutOrganization();
    };
    
    private void layoutOrganization() {
    	Display.getDefault().timerExec(10, new Runnable() {
            @Override
            public void run() {
            	compositeOrganization.setVisible(isExpanded);
                if (!isExpanded) {
                    ((GridData) compositeOrganization.getLayoutData()).exclude = true;
//                    compositeOrganization.setSize(compositeOrganization.getSize().x,
//                    		compositeOrganization.getSize().y - compositeOrganization.getSize().y);
                    Point currentSize = getShell().getSize();
                    int detailsHeight = compositeOrganization.getSize().y;
                    int newY = currentSize.y - detailsHeight;
                    getShell().setSize(currentSize.x, newY);
                } else {
                    ((GridData) compositeOrganization.getLayoutData()).exclude = false;
                    Point currentSize = getShell().getSize();
                    int detailsHeight = compositeOrganization.getSize().y;
                    int newY = currentSize.y + detailsHeight;
                    getShell().setSize(currentSize.x, newY);
                }
                compositeOrganization.layout(true, true);
                compositeOrganization.getParent().layout();
            }
        });
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