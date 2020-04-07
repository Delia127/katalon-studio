package com.kms.katalon.activation.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganizationRole;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.license.models.License;
import com.kms.katalon.license.models.LicenseResource;
import com.kms.katalon.logging.LogUtil;

public class ActivationDialogV2 extends AbstractDialog {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    public static final int REQUEST_SIGNUP_CODE = 1001;

    public static final int REQUEST_OFFLINE_CODE = 1002;

    private Text txtServerUrl;

    private Text txtEmail;

    private Text txtPassword;

    private Label lblProgressMessage;

    private Link lnkSwitchToSignupDialog;

    private Button btnActivate;

    private Button btnSave;

    private Combo cbbOrganization;

    private Label lblMachineKeyDetail;

    private Link lnkConfigProxy;
    
    private Link lnkOfflineActivation2;

    private Link lnkForgotPassword;

    private Link lblHelpOrganization;

    private List<AnalyticsOrganization> organizations = new ArrayList<>();

    private String machineId;

    private LicenseResource licenseResource;

    private License license;

    private Link lnkAgreeTerm;
    
    private Link lnkLearnAboutKSE;

    private Composite organizationComposite;
    
    private boolean isExpanded;
    
    public ActivationDialogV2(Shell parentShell) {
        super(parentShell, false);
        machineId = MachineUtil.getMachineId();
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
        
        lnkLearnAboutKSE.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
        
        lnkOfflineActivation2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(REQUEST_OFFLINE_CODE);
                close();
            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String serverUrl = txtServerUrl.getText().trim();
                String username = txtEmail.getText();
                String password = txtPassword.getText();

                if (!validateServer()) {
                    setProgressMessage(MessageConstants.ERR_MSG_SERVER_INVALID, true);
                    return;
                }

                Executors.newFixedThreadPool(1).submit(() -> {
                    UISynchronizeService.syncExec(() -> {
                        enableObject(false);
                        setProgressMessage(MessageConstants.ActivationDialogV2_MSG_LOGIN, false);
                    });
                    UISynchronizeService.syncExec(() -> {
                        try {
                            StringBuilder errorMessage = new StringBuilder();
                            boolean testConnection = KatalonApplicationActivator.getFeatureActivator().testConnection(serverUrl);
                            if (!testConnection) {
                                setProgressMessage(MessageConstants.ActivationDialogV2_MSG_CANNOT_CONNECT_TESTOPS, true);
                                enableObject(true);
                                return;
                            }

                            LogUtil.logInfo("Activating credentials...");
                            licenseResource = ActivationInfoCollector.activate(serverUrl, username, password, machineId, errorMessage);
                            if (licenseResource != null) {
                                license = licenseResource.getLicense();
                                if (license != null) {
                                    if (license.getOrganizationId() != null) {
                                        LogUtil.logInfo("Retrieving organization information of license...");
                                        String org = ActivationInfoCollector.getOrganization(username, password,
                                                license.getOrganizationId());

                                        LogUtil.logInfo("Saving organization information and closing activation dialog...");
                                        save(org);
                                    } else {
                                        LogUtil.logInfo("Fetching organizations...");
                                        getOrganizations();
                                        setProgressMessage(StringUtils.EMPTY, false);
                                    }
                                } else {
                                    LogUtil.logError("Could not get license from TestOps server. Error message: "
                                            + errorMessage.toString());
                                    enableObject(true);
                                    setProgressMessage(errorMessage.toString(), true);
                                    ActivationInfoCollector.sendTrackingForActivate(username, machineId, false, errorMessage);
                                }
                            } else {
                                LogUtil.logError(
                                        "Failed to active user credentials. Error message: " + errorMessage.toString());
                                enableObject(true);
                                setProgressMessage(errorMessage.toString(), true);
                                ActivationInfoCollector.sendTrackingForActivate(username, machineId, false, errorMessage);
                            }
                        } catch (Exception ex) {
                            LogUtil.logError(ex);
                            setProgressMessage(MessageConstants.ActivationDialogV2_LBL_ERROR_ORGANIZATION, true);
                            enableObject(true);
                            ActivationInfoCollector.sendTrackingForActivate(username, machineId, false, new StringBuilder().append(ex));
                        } catch (Throwable t) {
                            LogUtil.logError(t);
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

    private void enableObject(boolean isEnable) {
        btnActivate.setEnabled(isEnable);
        txtServerUrl.setEnabled(isEnable);
        txtEmail.setEnabled(isEnable);
        txtPassword.setEnabled(isEnable);
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
                    ActivationInfoCollector.markActivated(email, password, JsonUtil.toJson(organization), license);
                    close();

                    String message = licenseResource.getMessage();
                    StringBuilder errorMessage = new StringBuilder();
                    if (!StringUtils.isEmpty(message)) {
                        errorMessage.append(message);
                        WarningLicenseDialog warningLicenseDialog = new WarningLicenseDialog(Display.getCurrent().getActiveShell(), message);
                        warningLicenseDialog.open();
                    }
                    ActivationInfoCollector.sendTrackingForActivate(email, machineId, true, errorMessage);
                } catch (Exception e) {
                    enableObject(true);
                    btnSave.setEnabled(false);
                    LogUtil.logError(e, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
                    ActivationInfoCollector.sendTrackingForActivate(email, machineId, false, new StringBuilder().append(e));
                }
            });
        });
    }
    
    private void save(String org) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        Executors.newFixedThreadPool(1).submit(() -> {
            UISynchronizeService
                    .syncExec(() -> setProgressMessage(MessageConstants.ActivationDialogV2_MSG_GETTING_FEATURE, false));
            UISynchronizeService.syncExec(() -> {
                try {
                    ActivationInfoCollector.markActivated(email, password, org, license);
                    close();
                    String message = licenseResource.getMessage();
                    StringBuilder errorMessage = new StringBuilder();
                    if (!StringUtils.isEmpty(message)) {
                        errorMessage.append(message);
                        WarningLicenseDialog warningLicenseDialog = new WarningLicenseDialog(Display.getCurrent().getActiveShell(), message);
                        warningLicenseDialog.open();
                    }
                    ActivationInfoCollector.sendTrackingForActivate(email, machineId, true, errorMessage);
                } catch (Exception e) {
                    enableObject(true);
                    btnSave.setEnabled(false);
                    LogUtil.logError(e, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
                    ActivationInfoCollector.sendTrackingForActivate(email, machineId, false, new StringBuilder().append(e));
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
                try {
                    String serverUrl = ApplicationInfo.getTestOpsServer();
                    String email = txtEmail.getText();
                    String password = txtPassword.getText();

                    LogUtil.logInfo("Retrievinng token using credentials...");
                    String token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, email, password);

                    LogUtil.logInfo("Fetching organizations using token...");
                    organizations = AnalyticsApiProvider.getOrganizations(serverUrl, token);

                    LogUtil.logInfo(organizations.size() + " organization(s) fetched");
                    switch (organizations.size()) {
                        case 0:
                            AnalyticsOrganization organizationDefault = AnalyticsApiProvider.createDefaultOrganization(serverUrl, token);
                            organizations.add(organizationDefault);
                            save(0);
                            break;
                        case 1:
                            save(0);
                            break;
                        default:
                            layoutExecutionCompositeListener();
                            cbbOrganization.setItems(getOrganizationNames(organizations).toArray(new String[organizations.size()]));
                            cbbOrganization.select(getDefaultOrganizationIndex()); 
                            setProgressMessage("", false);
                            cbbOrganization.setEnabled(true);
                            btnSave.setEnabled(true);
                            break;
                    }
                } catch (Exception e) {
                    ActivationInfoCollector.sendTrackingForActivate(txtEmail.getText(), machineId, false, new StringBuilder().append(e));
                    LogUtil.logError(e);
                    setProgressMessage("", false);
                    MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(),
                            MessageConstants.ActivationDialogV2_LBL_ERROR, null,
                            MessageConstants.ActivationDialogV2_LBL_ERROR_ORGANIZATION, MessageDialog.ERROR,
                            new String[] { "OK" }, 0);
                    if (dialog.open() == Dialog.OK) {
                        enableObject(true);
                        btnSave.setEnabled(false);
                    }
                }
            });
        });
    }
    
    private int getDefaultOrganizationIndex() {
        int selectionIndex = 0;
        for (int i = 0; i < organizations.size(); i++) {
            AnalyticsOrganization organization = organizations.get(i);
            if ("USER".equals(organization.getRole())) {
                selectionIndex = i;
                return selectionIndex;
            }
        }
    	return selectionIndex;
    }

    private void setProgressMessage(String message, boolean isError) {
        if (isError) {
            lblProgressMessage.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lblProgressMessage.setForeground(ColorUtil.getTextRunningColor());
        }
        lblProgressMessage.setText(message);
        lblProgressMessage.getParent().layout();
    }

    @Override
    protected void setInput() {
        txtServerUrl.setText(ApplicationInfo.getDefaultTestOpsServer());
        btnActivate.setEnabled(validateInput());
    }

    private boolean validateEmail() {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail.getText()).find();
    }

    private boolean validatePassword() {
        return txtPassword.getText().length() >= 8;
    }

    private boolean validateServer() {
//        String[] schemes = {"http","https"};
//        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
//        return urlValidator.isValid(txtServerUrl.getText());
        return true;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        Composite contentComposite = new Composite(container, SWT.NONE);
        GridLayout glContent = new GridLayout(2, false);
        glContent.verticalSpacing = 10;
        contentComposite.setLayout(glContent);

        GridData gdLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdLabel.widthHint = 100;

        GridData gdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdText.heightHint = 22;

        GridData gdBtn = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gdBtn.widthHint = 100;

        Label lblServerUrl = new Label(contentComposite, SWT.NONE);
        lblServerUrl.setLayoutData(gdLabel);
        lblServerUrl.setText(StringConstants.SERVER_URL);

        txtServerUrl = new Text(contentComposite, SWT.BORDER);
        txtServerUrl.setLayoutData(gdText);

        Label lblEmail = new Label(contentComposite, SWT.NONE);
        lblEmail.setLayoutData(gdLabel);
        lblEmail.setText(StringConstants.EMAIL);

        txtEmail = new Text(contentComposite, SWT.BORDER);
        txtEmail.setLayoutData(gdText);
        txtEmail.setFocus();

        Label lblPassword = new Label(contentComposite, SWT.NONE);
        lblPassword.setLayoutData(gdLabel);
        lblPassword.setText(StringConstants.PASSSWORD_TITLE);

        txtPassword = new Text(contentComposite, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(gdText);

        Label lblMachineKey = new Label(contentComposite, SWT.NONE);
        lblMachineKey.setLayoutData(gdLabel);
        lblMachineKey.setText(MessageConstants.ActivationOfflineDialogV2_LBL_MACHINE_KEY);

        lblMachineKeyDetail = new Label(contentComposite, SWT.NONE);
        lblMachineKeyDetail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        lblMachineKeyDetail.setForeground(ColorUtil.getTextLinkColor());
        lblMachineKeyDetail.setText(machineId);

        Composite activateComposite = new Composite(contentComposite, SWT.NONE);
        activateComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        GridLayout gdLogInComposite = new GridLayout(2, false);
        gdLogInComposite.marginHeight = 0;
        gdLogInComposite.marginWidth = 0;
        activateComposite.setLayout(gdLogInComposite);

        lblProgressMessage = new Label(activateComposite, SWT.WRAP);
        GridData gdStatus = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdStatus.heightHint = 44;
        lblProgressMessage.setLayoutData(gdStatus);

        Composite activateRightComposite = new Composite(activateComposite, SWT.NONE);
        activateRightComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
        GridLayout gdActivateRight = new GridLayout(1, false);
        gdActivateRight.marginHeight = 0;
        gdActivateRight.marginWidth = 0;
        activateRightComposite.setLayout(gdActivateRight);

        btnActivate = new Button(activateRightComposite, SWT.NONE);
        btnActivate.setLayoutData(gdBtn);
        btnActivate.setText(StringConstants.BTN_ACTIVATE_TITLE);
        getShell().setDefaultButton(btnActivate);

        organizationComposite = new Composite(contentComposite, SWT.NONE);

        GridLayout glCompositeOrganization = new GridLayout(2, false);
        glCompositeOrganization.verticalSpacing = 10;
        glCompositeOrganization.horizontalSpacing = 0;
        glCompositeOrganization.marginHeight = 0;
        glCompositeOrganization.marginWidth = 0;
        organizationComposite.setLayout(glCompositeOrganization);
        organizationComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));

        Label lblOrganization = new Label(organizationComposite, SWT.NONE);
        GridData gdOrganization = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdOrganization.widthHint = 150;
        lblOrganization.setLayoutData(gdOrganization);
        lblOrganization.setText(MessageConstants.ActivationDialogV2_LBL_SELECT_ORGANIZATION);

        cbbOrganization = new Combo(organizationComposite, SWT.READ_ONLY);
        cbbOrganization.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        cbbOrganization.setEnabled(false);

        Composite saveComposite = new Composite(organizationComposite, SWT.NONE);
        saveComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
        GridLayout gdTemp = new GridLayout(2, false);
        gdTemp.marginHeight = 0;
        gdTemp.marginWidth = 0;
        saveComposite.setLayout(gdTemp);

        Composite saveLeftComposite = new Composite(saveComposite, SWT.NONE);
        GridLayout glSaveLeftComposite = new GridLayout(1, false);
        glSaveLeftComposite.marginHeight = 0;
        glSaveLeftComposite.marginWidth = 0;
        saveLeftComposite.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false));
        saveLeftComposite.setLayout(glSaveLeftComposite);

        lblHelpOrganization = new Link(saveLeftComposite, SWT.NONE);
        lblHelpOrganization.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        lblHelpOrganization.setText(String.format(MessageConstants.ActivationDialogV2_LNK_SEE_MORE_ORGANIZATION, ApplicationInfo.getTestOpsServer()));

        Composite saveRightComposite = new Composite(saveComposite, SWT.NONE);
        saveRightComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, true, false));
        GridLayout gdActivateComposite = new GridLayout(1, false);
        gdActivateComposite.marginHeight = 0;
        gdActivateComposite.marginWidth = 0;
        saveRightComposite.setLayout(gdActivateComposite);

        btnSave = new Button(saveRightComposite, SWT.NONE);
        btnSave.setLayoutData(gdBtn);
        btnSave.setText(StringConstants.BTN_SAVE);
        btnSave.setEnabled(false);

        layoutOrganization();

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

        Composite bottomTerm = new Composite(buttonBar, SWT.NONE);
        bottomTerm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdBottomBarTerm = new GridLayout(1, false);
        gdBottomBarTerm.marginWidth = 10;
        gdBottomBarTerm.marginHeight = 0;
        bottomTerm.setLayout(gdBottomBarTerm);
        
        lnkLearnAboutKSE = new Link(bottomTerm, SWT.WRAP);
        lnkLearnAboutKSE.setText(MessageConstants.ActivationDialogV2_LBL_LEARN_ABOUT_KSE);
        
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
        linkBar.setLayout(new GridLayout(7, false));
        
        lnkForgotPassword = new Link(linkBar, SWT.NONE);
        lnkForgotPassword.setText(String.format("<a>%s</a>", MessageConstants.ActivationDialogV2_LNK_RESET_PASSWORD));
        lnkForgotPassword.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        
        Label label3 = new Label(linkBar, SWT.SEPARATOR);
        GridData gdSeparatorofOfline = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gdSeparatorofOfline.heightHint = 22;
        label3.setLayoutData(gdSeparatorofOfline);

        lnkOfflineActivation2 = new Link(linkBar, SWT.NONE);
        lnkOfflineActivation2
                .setText(String.format("<a>%s</a>", MessageConstants.ActivationDialogV2_LNK_OFFLINE_ACTIVATION));
        lnkOfflineActivation2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        Label label4 = new Label(linkBar, SWT.SEPARATOR);
        label4.setLayoutData(gdSeparatorofOfline);

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
                organizationComposite.setVisible(isExpanded);
                Point currentSize = getShell().getSize();
                int detailsHeight = organizationComposite.getSize().y;
                int newY;
                if (!isExpanded) {
                    ((GridData) organizationComposite.getLayoutData()).exclude = true;
                    newY = currentSize.y - detailsHeight;
                } else {
                    ((GridData) organizationComposite.getLayoutData()).exclude = false;
                    newY = currentSize.y + detailsHeight;
                }
                getShell().setSize(currentSize.x, newY);
                organizationComposite.layout(true, true);
                organizationComposite.getParent().layout();
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
