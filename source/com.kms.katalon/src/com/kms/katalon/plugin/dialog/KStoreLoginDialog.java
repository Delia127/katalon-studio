package com.kms.katalon.plugin.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.Organization;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.plugin.models.KStoreBasicCredentials;
import com.kms.katalon.plugin.service.KStoreRestClient;
import com.kms.katalon.plugin.service.KStoreRestClient.AuthenticationResult;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganizationRole;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;

public class KStoreLoginDialog extends Dialog {

    private Text txtUsername;

    private Text txtPassword;

    private String username;

    private String password;

    private String token;

    private Label lblProgressMessage;
    
    private Combo cbbOrganization;
    
    private Composite body, organizationComposite;
    
    private Button btnConnect;
    
    private Button btnSkip;

    private List<AnalyticsOrganization> organizations = new ArrayList<>();

    private boolean isExpanded;

    public KStoreLoginDialog(Shell parentShell) {
        super(parentShell);
        isExpanded = false;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        body = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        body.setLayout(layout);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.minimumWidth = 400;
        body.setLayoutData(gdBody);
        
        Link lblInstruction = new Link(body, SWT.WRAP);
        lblInstruction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        lblInstruction.setText(StringConstants.KStoreLoginDialog_LBL_INSTRUCTION);
        lblInstruction.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Program.launch(StringConstants.LINK_KS_PLUGINS_DOCS_LINK);
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });

        Composite inputComposite = new Composite(body, SWT.NONE);
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout glInput = new GridLayout(2, false);
        glInput.verticalSpacing = 10;
        glInput.marginBottom = 10;
        inputComposite.setLayout(glInput);
        
        GridData gdLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdLabel.widthHint = 75;

        GridData gdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdText.heightHint = 22;
        
        GridData gdBtn = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gdBtn.widthHint = 100;
        
        Label lblUsername = new Label(inputComposite, SWT.NONE);
        lblUsername.setText(StringConstants.KStoreLoginDialog_LBL_USERNAME);
        lblUsername.setLayoutData(gdLabel);

        txtUsername = new Text(inputComposite, SWT.BORDER);
        txtUsername.setLayoutData(gdText);
        
        username = ApplicationInfo.getAppProperty("email");
        if (!StringUtils.isBlank(username)) {
            txtUsername.setText(username);
            txtUsername.setEditable(false);
        }

        Label lblPassword = new Label(inputComposite, SWT.NONE);
        lblPassword.setText(StringConstants.KStoreLoginDialog_LBL_PASSWORD);
        lblPassword.setLayoutData(gdLabel);

        txtPassword = new Text(inputComposite, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(gdText);

        lblProgressMessage = new Label(body, SWT.NONE);
        lblProgressMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        
        organizationComposite = new Composite(body, SWT.NONE);
        organizationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        organizationComposite.setLayout(new GridLayout(2, false));
        
        Label lblOrganization = new Label(organizationComposite, SWT.NONE);
        GridData gdOrganization = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdOrganization.widthHint = 150;
        lblOrganization.setLayoutData(gdOrganization);
        lblOrganization.setText(MessageConstants.ActivationDialogV2_LBL_SELECT_ORGANIZATION);
        
        cbbOrganization = new Combo(organizationComposite, SWT.READ_ONLY);
        cbbOrganization.setLayoutData(gdText);
        cbbOrganization.setEnabled(false);

        Composite buttonComposite = new Composite(body, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        buttonComposite.setLayout(new GridLayout(2, false));

        btnSkip = new Button(buttonComposite, SWT.FLAT);
        btnSkip.setText(StringConstants.KStoreLoginDialog_BTN_SKIP);
        btnSkip.setLayoutData(gdBtn);
        
        btnConnect = new Button(buttonComposite, SWT.FLAT);
        btnConnect.setText(StringConstants.KStoreLoginDialog_BTN_CONNECT);
        btnConnect.setEnabled(false);
        btnConnect.setLayoutData(gdBtn);
        getShell().setDefaultButton(btnConnect);

        layoutOrganization();

        registerControlListeners();

        return body;
    }

    private void registerControlListeners() {
        txtUsername.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                username = txtUsername.getText();
                validate();
            }
        });

        txtPassword.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                password = txtPassword.getText();
                validate();
            }
        });
        
        btnConnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!btnConnect.getText().equals(StringConstants.KStoreLoginDialog_BTN_OK)) {
                    enableObject(false);
                    Executors.newFixedThreadPool(1).submit(() -> {
                        UISynchronizeService.syncExec(() -> setProgressMessage(MessageConstants.ActivationDialogV2_MSG_ACTIVATING, false));
                        UISynchronizeService.syncExec(() -> authenticate());
                    });
                } else {
                    save(cbbOrganization.getSelectionIndex());
                }
            }
        });
        
        btnSkip.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(Dialog.CANCEL);
                close();
            }
        });
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
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    private void validate() {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
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
        KStoreBasicCredentials credentials = new KStoreBasicCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        try {
            btnConnect.setEnabled(false);
            KStoreRestClient restClient = new KStoreRestClient(credentials);
            AuthenticationResult authenticateResult = restClient.authenticate();
            if (authenticateResult.isAuthenticated()) {
                getOrganizations();
                token = authenticateResult.getToken();
            } else {
                enableObject(true);
                setProgressMessage(StringConstants.KStoreLoginDialog_INVALID_ACCOUNT_ERROR, true);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            setProgressMessage(StringConstants.KStoreLoginDialog_FAILED_TO_AUTHENTICATE_MSG, true);
            enableObject(true);
        }
    }

    private void getOrganizations() {
        Executors.newFixedThreadPool(1).submit(() -> {
            UISynchronizeService.syncExec(() -> setProgressMessage(MessageConstants.ActivationDialogV2_MSG_GETTING_ORGANIZATION, false));
            UISynchronizeService.syncExec(() -> {
                try {
                    String serverUrl = ApplicationInfo.getTestOpsServer();
                    String email = txtUsername.getText();
                    String password = txtPassword.getText();

                    String token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, email, password);
                    organizations = AnalyticsApiProvider.getOrganizations(serverUrl, token);
                    
                    Organization organization = ApplicationInfo.getOrganization();
                    if (isValidOrganization(organization)) {
                        save();
                    } else {
                        setProgressMessage(String.format(MessageConstants.MSG_ERROR_NOT_BELONG_ORG, organization.getId()), true);
                        enableObject(true);
                    }
                } catch (Exception e) {
                    setProgressMessage(StringConstants.KStoreLoginDialog_FAILED_TO_AUTHENTICATE_MSG, true);
                    enableObject(true);
                }
            });
        });
    }
    
    private boolean isValidOrganization(Organization organization) {
        
        for (int i = 0; i < organizations.size(); i++) {
            AnalyticsOrganization org = organizations.get(i);
            if (org.getId().equals(organization.getId())) {
                ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ORGANIZATION, JsonUtil.toJson(org) , true);
                return true;
            }
        }
        return false;
    }

    private void enableObject(boolean isEnable) {
        txtUsername.setEnabled(isEnable);
        txtPassword.setEnabled(isEnable);
        btnConnect.setEnabled(isEnable);
        btnSkip.setEnabled(isEnable);
    }

    private void save(int index) {
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ORGANIZATION, JsonUtil.toJson(organizations.get(index)) , true);
        super.okPressed();
    }
    
    private void save() {
        super.okPressed();
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

    private static List<String> getOrganizationNames(List<AnalyticsOrganization> organizations) {
        List<String> names = organizations.stream().map(organization -> organization.getName()).collect(Collectors.toList());
        return names;
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

    private void setProgressMessage(String message, boolean isError) {
        lblProgressMessage.setText(message);
        if (isError) {
            lblProgressMessage.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lblProgressMessage.setForeground(ColorUtil.getTextRunningColor());
        }
        lblProgressMessage.getParent().layout();
    }

}
