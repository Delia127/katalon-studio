package com.kms.katalon.composer.execution.preferences;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.utils.ApplicationProxyUtil;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.network.ProxyServerType;
import com.kms.katalon.execution.preferences.ProxyPreferenceDefaultValueInitializer;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class ProxyConfigurationPreferencesPage extends PreferencePageWithHelp {
    private Text txtAddress;

    private Text txtPort;

    private Text txtUsername;

    private Text txtPass;

    private Combo cboProxyOption;

    private Combo cboProxyServerType;
    
    private Combo cboUseMobBrowserProxy;

    private Button chkRequireAuthentication;

    private static final int MAX_PORT_VALUE = 65535;

    public ProxyConfigurationPreferencesPage() {
        super();
        noDefaultButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite area = parent;

        Composite innerComposite = new Composite(area, SWT.NONE);
        innerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout glContainer = new GridLayout(2, false);
        glContainer.verticalSpacing = 10;
        glContainer.marginTop = 20;
        glContainer.marginLeft = 10;
        glContainer.marginRight = 10;
        glContainer.marginBottom = 30;
        innerComposite.setLayout(glContainer);

        Label lblProxyOption = new Label(innerComposite, SWT.NONE);
        lblProxyOption.setText(MessageConstants.LBL_PROXY_OPTION);

        cboProxyOption = new Combo(innerComposite, SWT.READ_ONLY);
        GridData gdComboProxyOption = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdComboProxyOption.widthHint = 320;
        gdComboProxyOption.heightHint = 18;
        cboProxyOption.setLayoutData(gdComboProxyOption);
        cboProxyOption.setItems(ProxyOption.displayStringValues());
        cboProxyOption.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.getSource() != cboProxyOption) {
                    return;
                }
                String selectText = cboProxyOption.getText();
                ProxyOption proxyOption = ProxyOption.valueOfDisplayName(selectText);
                switch (proxyOption) {
                    case MANUAL_CONFIG:
                        selectManualConfigProxyOption();
                        return;
                    case NO_PROXY:
                        selectNoProxyOption();
                        return;
                    case USE_SYSTEM:
                        selectSystemProxyOption();
                        return;
                    default:
                        break;

                }
            }
        });

        Label lblNewLabel = new Label(innerComposite, SWT.NONE);
        lblNewLabel.setText(MessageConstants.LBL_PROXY_SERVER_TYPE);

        cboProxyServerType = new Combo(innerComposite, SWT.READ_ONLY);
        GridData gdComboProxyServerType = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdComboProxyServerType.widthHint = 319;
        gdComboProxyServerType.heightHint = 18;
        cboProxyServerType.setLayoutData(gdComboProxyServerType);
        cboProxyServerType.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        cboProxyServerType.setItems(ProxyServerType.stringValues());
        
        Label lblUseMobBrowserProxy = new Label(innerComposite, SWT.NONE);
        lblUseMobBrowserProxy.setText(MessageConstants.LBL_USE_MOB_BROWSER_PROXY);
        cboUseMobBrowserProxy = new Combo(innerComposite, SWT.READ_ONLY);
        cboUseMobBrowserProxy.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        cboUseMobBrowserProxy.setItems("true", "false");

        Label lblAddress = new Label(innerComposite, SWT.NONE);
        lblAddress.setText(MessageConstants.LBL_ADDRESS);

        Composite composite = new Composite(innerComposite, SWT.NONE);
        GridLayout glComposite = new GridLayout(3, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        glComposite.horizontalSpacing = 0;
        glComposite.verticalSpacing = 0;
        composite.setLayout(glComposite);
        GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        gdComposite.widthHint = 314;
        gdComposite.heightHint = 22;
        composite.setLayoutData(gdComposite);

        txtAddress = new Text(composite, SWT.BORDER);
        GridData gdAddress = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        gdAddress.widthHint = 206;
        txtAddress.setLayoutData(gdAddress);

        Label lblPort = new Label(composite, SWT.NONE);
        GridData gdLblPort = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblPort.widthHint = 32;
        lblPort.setLayoutData(gdLblPort);
        lblPort.setText(MessageConstants.LBL_PORT);

        txtPort = new Text(composite, SWT.BORDER);
        GridData gd_txtPort = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd_txtPort.widthHint = 68;
        txtPort.setLayoutData(gd_txtPort);
        txtPort.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                String text = txtPort.getText();
                String newText = text.substring(0, e.start) + e.text + text.substring(e.end);
                if (StringUtils.isEmpty(newText)) {
                    e.doit = true;
                    return;
                }
                try {
                    int val = Integer.parseInt(newText);
                    e.doit = val >= 0 && val <= MAX_PORT_VALUE;
                } catch (NumberFormatException ex) {
                    e.doit = false;
                }
            }
        });
        chkRequireAuthentication = new Button(innerComposite, SWT.CHECK);
        chkRequireAuthentication.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        chkRequireAuthentication.setText(MessageConstants.CHK_TEXT_PROXY_SERVER_TYPE_REQUIRE_AUTHENTICATION);
        chkRequireAuthentication.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = chkRequireAuthentication.getSelection();
                txtUsername.setEnabled(selection);
                txtUsername.setText(selection ? txtUsername.getText() : "");
                txtPass.setEnabled(selection);
                txtPass.setText(selection ? txtPass.getText() : "");
            }
        });

        Group authenticateGroup = new Group(innerComposite, SWT.NONE);
        authenticateGroup.setText("Authentication");
        authenticateGroup.setLayout(new GridLayout(2, false));
        authenticateGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true, 2, 1));

        Label lblUsername = new Label(authenticateGroup, SWT.NONE);
        lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblUsername.setText(MessageConstants.LBL_USERNAME);

        txtUsername = new Text(authenticateGroup, SWT.BORDER);
        GridData gdUsername = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        txtUsername.setLayoutData(gdUsername);

        Label lblPassword = new Label(authenticateGroup, SWT.NONE);
        lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblPassword.setText(MessageConstants.LBL_PASSWORD);

        txtPass = new Text(authenticateGroup, SWT.BORDER | SWT.PASSWORD);
        GridData gdPass = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        txtPass.setLayoutData(gdPass);

        initialize();

        return area;
    }

    private void selectNoProxyOption() {
        cboProxyServerType.deselectAll();
        cboProxyServerType.setEnabled(false);
        cboUseMobBrowserProxy.select(1);
        cboUseMobBrowserProxy.setEnabled(false);
        txtPort.setText("");
        txtPort.setEnabled(false);
        txtAddress.setText("");
        txtAddress.setEnabled(false);
        chkRequireAuthentication.setEnabled(false);
        chkRequireAuthentication.setSelection(false);
        txtUsername.setEnabled(false);
        txtUsername.setText("");
        txtPass.setEnabled(false);
        txtPass.setText("");
    }

    private void selectSystemProxyOption() {
        selectNoProxyOption();
    }

    private void selectManualConfigProxyOption() {
        cboProxyServerType.setEnabled(true);
        cboUseMobBrowserProxy.setEnabled(true);
        txtPort.setEnabled(true);
        txtAddress.setEnabled(true);
        chkRequireAuthentication.setEnabled(true);
        cboProxyServerType.setText(ProxyServerType.HTTP.toString());
        if (!chkRequireAuthentication.getSelection()) {
            txtUsername.setEnabled(false);
            txtUsername.setText("");
            txtPass.setEnabled(false);
            txtPass.setText("");
        }
    }

    private void initialize() {
        ProxyInformation proxyInfo = null;
        if (ProxyPreferences.isProxyPreferencesSet()) {
            proxyInfo = ProxyPreferences.getProxyInformation();
            cboProxyOption.setText(ProxyOption.valueOf(proxyInfo.getProxyOption()).getDisplayName());
        } else {
            proxyInfo = ApplicationProxyUtil.getProxyInformation();
            cboProxyOption.setText(proxyInfo.getProxyOption());
        }
        cboProxyServerType.setText(proxyInfo.getProxyServerType());
        cboUseMobBrowserProxy.setText(String.valueOf(proxyInfo.getUseMobBroserProxy()));
        txtAddress.setText(proxyInfo.getProxyServerAddress());
        txtPort.setText(proxyInfo.getProxyServerPort() > 0 ? proxyInfo.getProxyServerPort() + "" : "");
        txtUsername.setText(proxyInfo.getUsername());
        txtPass.setText(proxyInfo.getPassword());

        String proxyOption = cboProxyOption.getText();
        if (ApplicationMessageConstants.NO_PROXY.equals(proxyOption)) {
            selectNoProxyOption();
        } else if (ApplicationMessageConstants.USE_SYSTEM_PROXY.equals(proxyOption)) {
            selectSystemProxyOption();
        } else {
            chkRequireAuthentication.setEnabled(true);
            if (StringUtils.isNotEmpty(txtUsername.getText()) && StringUtils.isNotEmpty(txtPass.getText())) {
                chkRequireAuthentication.setSelection(true);
            }
            boolean requiredAuthentication = chkRequireAuthentication.getSelection();
            txtUsername.setEnabled(requiredAuthentication);
            txtPass.setEnabled(requiredAuthentication);
        }
    }

    @Override
    public boolean performOk() {
        if (cboProxyOption == null) {
            return true;
        }
        ProxyInformation proxyInfo = new ProxyInformation();
        proxyInfo.setProxyOption(ProxyOption.valueOfDisplayName(cboProxyOption.getText()).name());
        proxyInfo.setProxyServerType(cboProxyServerType.getText());
        proxyInfo.setProxyServerAddress(txtAddress.getText());
        final String portValue = txtPort.getText();
        proxyInfo.setProxyServerPort(StringUtils.isEmpty(portValue)
                ? String.valueOf(ProxyPreferenceDefaultValueInitializer.PROXY_SERVER_PORT_DEFAULT_VALUE) : portValue);
        proxyInfo.setUsername(txtUsername.getText());
        proxyInfo.setPassword(txtPass.getText());
        proxyInfo.setUseMobBrowserProxy(Boolean.valueOf(cboUseMobBrowserProxy.getText()));
        try {
            ProxyPreferences.saveProxyInformation(proxyInfo);
            return true;
        } catch (IOException e) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    ComposerExecutionMessageConstants.PREF_MSG_UNABLE_TO_SAVE_PROXY_CONFIG);
            return false;
        }
    }
    
    @Override
    public boolean hasDocumentation() {
        return true;
    }
    
    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.PREFERENCE_PROXY;
    }
}
