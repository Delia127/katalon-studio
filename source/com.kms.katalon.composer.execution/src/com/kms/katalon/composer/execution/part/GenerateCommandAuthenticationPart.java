package com.kms.katalon.composer.execution.part;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.controls.HelpComposite;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.execution.console.entity.OsgiConsoleOptionContributor;
import com.kms.katalon.integration.analytics.entity.AnalyticsApiKey;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class GenerateCommandAuthenticationPart extends Composite {

    private Text txtAPIKey;

    private static final String KATALON_STUDIO_ONLINE_LICENSE_DOCUMENT = StringConstants.KATALON_STUDIO_ONLINE_LICENSE_DOCUMENT;

    private static final String DEFAULT_ORGANIZATION = StringConstants.DEFAULT_ORGANIZATION;

    private ComboViewer cbOrganizations;

    private Combo ccbOrganizations;

    private static final String ARG_API_KEY = OsgiConsoleOptionContributor.API_KEY_OPTION;

    private static final String ARG_ORG_ID_KEY = OsgiConsoleOptionContributor.ORG_ID_KEY_OPTION;

    private AnalyticsOrganization selectedOrganization = null;

    public GenerateCommandAuthenticationPart(Composite parent) {
        super(parent, SWT.NONE);
        this.setLayout(new GridLayout());
        this.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        createAuthenticationPart();
        fetchDataForComboBoxOrganizations();
        getApiKey();
    }

    private void createAuthenticationPart() {
        Group grpAuthenContainer = new Group(this, SWT.NONE);
        grpAuthenContainer.setLayout(new GridLayout(3, false));
        grpAuthenContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpAuthenContainer.setText(StringConstants.DIA_GRP_AUTHENTICATION);

        Label lblAPIKey = new Label(grpAuthenContainer, SWT.NONE);
        lblAPIKey.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblAPIKey.setText(StringConstants.DIA_LBL_APIKEY);

        txtAPIKey = new Text(grpAuthenContainer, SWT.BORDER);
        GridData gdTxtAPIKey = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
        gdTxtAPIKey.widthHint = 600;
        txtAPIKey.setLayoutData(gdTxtAPIKey);

        createNoticesComposite(grpAuthenContainer);

        Label lblOrgs = new Label(grpAuthenContainer, SWT.NONE);
        lblOrgs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblOrgs.setText(StringConstants.DIA_LBL_ORGANIZATION);

        cbOrganizations = new ComboViewer(grpAuthenContainer, SWT.READ_ONLY);
        ccbOrganizations = cbOrganizations.getCombo();
        ccbOrganizations.setLayout(new GridLayout());
        ccbOrganizations.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ccbOrganizations.setEnabled(false);
        cbOrganizations.setContentProvider(ArrayContentProvider.getInstance());
        cbOrganizations.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof AnalyticsOrganization) {
                    AnalyticsOrganization current = (AnalyticsOrganization) element;
                    return current.getName();
                }
                return super.getText(element);
            }
        });
        cbOrganizations.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                AnalyticsOrganization organization = (AnalyticsOrganization) selection.getFirstElement();
                selectedOrganization = organization;
                cbOrganizations.refresh();
            }
        });

        createHelpComposite(grpAuthenContainer);
    }

    private void fetchDataForComboBoxOrganizations() {
        AnalyticsOrganization[] organizations = getKREOrganizations();
        if (organizations != null) {
            ccbOrganizations.setEnabled(true);
            cbOrganizations.setInput(organizations);
        }
    }

    private void createHelpComposite(Composite parent) {
        HelpComposite btnHelp = new HelpComposite(parent, KATALON_STUDIO_ONLINE_LICENSE_DOCUMENT);
        btnHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    }

    public void createNoticesComposite(Composite parent) {
        Composite noticesComposite = new Composite(parent, SWT.NONE);
        noticesComposite.setLayout(new GridLayout(1, false));
        noticesComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));

        Link lblApiKeyUsage = new Link(noticesComposite, SWT.WRAP);
        lblApiKeyUsage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        ControlUtils.setFontStyle(lblApiKeyUsage, SWT.BOLD | SWT.ITALIC, -1);
        lblApiKeyUsage.setText(StringConstants.DIA_LBL_API_KEY_USAGE);
        lblApiKeyUsage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Program.launch(DocumentationMessageConstants.KSTORE_API_KEYS_USAGE);
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });

        Link lblConsoleModeRequirement = new Link(noticesComposite, SWT.WRAP);
        lblConsoleModeRequirement.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        ControlUtils.setFontStyle(lblConsoleModeRequirement, SWT.BOLD | SWT.ITALIC, -1);
        lblConsoleModeRequirement.setText(StringConstants.MSG_CONSOLE_MODE_REQUIREMENT);
        lblConsoleModeRequirement.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Program.launch(DocumentationMessageConstants.RUNTIME_ENGINE_INTRODUCTION);
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });
    }

    private void getApiKey() {
        Thread getApiKey = new Thread(() -> {
            AnalyticsApiKey apiKey = null;
            try {
                String serverUrl = ApplicationInfo.getTestOpsServer();
                String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
                if (!Strings.isNullOrEmpty(email) && !Strings.isNullOrEmpty(encryptedPassword)) {
                    String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                    AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                    List<AnalyticsApiKey> apiKeys = AnalyticsApiProvider.getApiKeys(serverUrl, token.getAccess_token());
                    if (!apiKeys.isEmpty()) {
                        apiKey = apiKeys.get(0);
                    }
                }
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            } finally {
                if (apiKey != null) {
                    String key = apiKey.getKey();
                    UISynchronizeService.asyncExec(() -> {
                        if (!txtAPIKey.isDisposed()) {
                            txtAPIKey.setText(key);
                        }
                    });
                }
            }
        });
        getApiKey.start();
    }

    public Map<String, String> getConsoleArgsMap() {
        Map<String, String> args = new LinkedHashMap<String, String>();

        if (!StringUtils.isEmpty(txtAPIKey.getText())) {
            args.put(ARG_API_KEY, wrapArgumentValue(txtAPIKey.getText()));
        }
        if (selectedOrganization != null && selectedOrganization.getId() != null) {
            args.put(ARG_ORG_ID_KEY, selectedOrganization.getId().toString());
        }
        return args;
    }

    private AnalyticsOrganization[] getKREOrganizations() {
        try {
            String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
            String encryptedPw = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);

            String pw = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPw));
            String serverUrl = ApplicationInfo.getTestOpsServer();

            LogUtil.logInfo("Retrievinng token using credentials...");
            String token = KatalonApplicationActivator.getFeatureActivator().connect(serverUrl, email, pw);

            LogUtil.logInfo("Fetching organizations using token...");

            List<AnalyticsOrganization> organizations = new ArrayList<>();
            organizations.addAll(AnalyticsApiProvider.getKREOrganizations(serverUrl, token));

            LogUtil.logInfo(organizations.size() + " KRE organization(s) fetched");
            if (organizations.size() >= 1) {
                AnalyticsOrganization defaultOrganization = new AnalyticsOrganization();
                defaultOrganization.setName(DEFAULT_ORGANIZATION);
                organizations.add(0, defaultOrganization);
                return organizations.toArray(new AnalyticsOrganization[organizations.size()]);
            }
        } catch (Exception e) {
            LogUtil.logError(e, ApplicationMessageConstants.ACTIVATION_COLLECT_FAIL_MESSAGE);
        }
        return null;
    }

    private String wrapArgumentValue(String value) {
        return "\"" + value + "\"";
    }
}
