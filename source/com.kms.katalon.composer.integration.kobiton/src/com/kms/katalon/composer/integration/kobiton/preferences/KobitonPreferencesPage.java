package com.kms.katalon.composer.integration.kobiton.preferences;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.preferences.ComboFieldEditor;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerIntegrationKobitonMessageConstants;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerKobitonStringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.integration.kobiton.constants.KobitonPreferenceConstants;
import com.kms.katalon.integration.kobiton.entity.KobitonApiKey;
import com.kms.katalon.integration.kobiton.entity.KobitonLoginInfo;
import com.kms.katalon.integration.kobiton.exceptions.KobitonApiException;
import com.kms.katalon.integration.kobiton.providers.KobitonApiProvider;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class KobitonPreferencesPage extends FieldEditorPreferencePageWithHelp {
    private static final char PASSWORD_ECHO_CHAR = '*';

    private ComboFieldEditor apiKeyComboEditor;

    private Button connectButton;

    private StringFieldEditor passwordFieldEditor;

    private StringFieldEditor userNameEditor;

    private BooleanFieldEditor enableKobitonIntegration;

    private Composite composite;

    private Group authenticateGroup;

    private StringFieldEditor serverEditor;

    private Link statusLabel;

    private String token;

    private Composite serverAndApiKeyComposite;
    
    public KobitonPreferencesPage() {
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(KobitonPreferenceConstants.KOBITON_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        composite = createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);
        enableKobitonIntegration = new BooleanFieldEditor(KobitonPreferenceConstants.KOBITON_INTEGRATION_ENABLE,
                ComposerIntegrationKobitonMessageConstants.LBL_ENABLE_KOBITON_INTEGRATION, composite);
        addField(enableKobitonIntegration);
        authenticateGroup = createGroup(composite, ComposerIntegrationKobitonMessageConstants.LBL_AUTHENTICATE_GROUP,
                2, 2, GridData.FILL_HORIZONTAL);
        userNameEditor = new StringFieldEditor(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_USERNAME,
                ComposerIntegrationKobitonMessageConstants.LBL_USERNAME, authenticateGroup);
        addField(userNameEditor);
        passwordFieldEditor = new StringFieldEditor(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_PASSWORD,
                ComposerIntegrationKobitonMessageConstants.LBL_PASSWORD, authenticateGroup);
        passwordFieldEditor.getTextControl(authenticateGroup).setEchoChar(PASSWORD_ECHO_CHAR);
        addField(passwordFieldEditor);

        Composite connectComposite = createComposite(composite, 2, 2, GridData.FILL_HORIZONTAL);

        connectButton = new Button(connectComposite, SWT.NONE);
        connectButton.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
        connectButton.setText(ComposerIntegrationKobitonMessageConstants.BTN_CONNECT);

        statusLabel = new Link(connectComposite, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        statusLabel.setText(""); //$NON-NLS-1$
        
        serverAndApiKeyComposite = createComposite(composite, 2, 1, GridData.FILL_HORIZONTAL);
        serverEditor = new StringFieldEditor(KobitonPreferenceConstants.KOBITON_SERVER_ENDPOINT,
                ComposerIntegrationKobitonMessageConstants.LBL_SERVER_URL, serverAndApiKeyComposite);
        serverEditor.getTextControl(serverAndApiKeyComposite).setEditable(false);
        addField(serverEditor);
        
        apiKeyComboEditor = new ComboFieldEditor(KobitonPreferenceConstants.KOBITON_API_KEY,
                ComposerIntegrationKobitonMessageConstants.LBL_API_KEYS, new String[][] {}, serverAndApiKeyComposite);
        addField(apiKeyComboEditor);
        addListeners();
        initialize();
        checkState();
        return composite;
    }

    @Override
    protected void initialize() {
        super.initialize();
        changeEnabledState();
        initKeyComboState();
    }

    private void initKeyComboState() {
        if (apiKeyComboEditor.isNamesAndValuesEmpty()) {
            String key = getPreferenceStore().getString(KobitonPreferenceConstants.KOBITON_API_KEY);
            apiKeyComboEditor.changeNamesAndValues(new String[][] { { key, key } });
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (FieldEditor.VALUE.equals(event.getProperty())) {
            handleFieldEditorValueChanged(event);
        }
        super.propertyChange(event);
    }

    private void handleFieldEditorValueChanged(PropertyChangeEvent event) {
        if (event.getSource() == enableKobitonIntegration) {
            changeEnabledState();
        }
    }

    @Override
    protected void performDefaults() {
        apiKeyComboEditor.changeNamesAndValues(new String[][] { { "", "" } }); //$NON-NLS-1$ //$NON-NLS-2$
        super.performDefaults();
        getPreferenceStore().setToDefault(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_TOKEN);
        changeEnabledState();
    }

    private void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean performOk() {
        final boolean performOk = super.performOk();
        if (performOk && token != null) {
            getPreferenceStore().setValue(KobitonPreferenceConstants.KOBITON_AUTHENTICATION_TOKEN, token);
        }
        return performOk;
    }

    private void addListeners() {
        connectButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<KobitonApiKey> apiKeys = getApiKeys(userNameEditor.getStringValue(),
                        passwordFieldEditor.getStringValue());
                if (apiKeys == null) {
                    return;
                }
                apiKeyComboEditor.changeNamesAndValues(getKeyValueArrayFromApiKeyList(apiKeys));
                if (apiKeys.isEmpty()) {
                    statusLabel.setText(ComposerKobitonStringConstants.WARN
                            + ": " + ComposerIntegrationKobitonMessageConstants.KobitonPreferencesPage_WARN_MSG_NO_API_KEY); //$NON-NLS-1$
                    statusLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
                }
                checkState();
            }

            private List<KobitonApiKey> getApiKeys(final String userName, final String password) {
                final List<KobitonApiKey> apiKeys = new ArrayList<>();
                try {
                    new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
                        @Override
                        public void run(IProgressMonitor monitor) throws InvocationTargetException,
                                InterruptedException {
                            try {
                                monitor.beginTask(
                                        ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_RETRIEVING_KEYS, 2);
                                monitor.subTask(ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER);
                                final KobitonLoginInfo loginInfo = KobitonApiProvider.login(userName, password);
                                UISynchronizeService.syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        userNameEditor.setStringValue(loginInfo.getUser().getUsername());
                                    }
                                });
                                monitor.worked(1);
                                monitor.subTask(ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_GETTING_KEYS);
                                apiKeys.addAll(KobitonApiProvider.getApiKeyList(loginInfo.getToken()));
                                monitor.worked(1);
                                setToken(loginInfo.getToken());
                            } catch (URISyntaxException | IOException | KobitonApiException e) {
                                throw new InvocationTargetException(e);
                            } finally {
                                monitor.done();
                            }
                        }
                    });
                    statusLabel.setText(ComposerIntegrationKobitonMessageConstants.MSG_DLG_PRG_SUCCESSFULLY);
                    statusLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
                    return apiKeys;
                } catch (InvocationTargetException exception) {
                    final Throwable cause = exception.getCause();
                    if (cause instanceof KobitonApiException) {
                        statusLabel.setText(ComposerKobitonStringConstants.ERROR + ": " + cause.getMessage()); //$NON-NLS-1$
                        statusLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                    } else {
                        LoggerSingleton.logError(cause);
                    }
                } catch (InterruptedException e) {
                    // Ignore this
                }
                return null;
            }

            private String[][] getKeyValueArrayFromApiKeyList(List<KobitonApiKey> apiKeys) {
                String[][] apiKeysAndValues = new String[apiKeys.size()][2];
                for (int index = 0; index < apiKeys.size(); index++) {
                    final KobitonApiKey apiKey = apiKeys.get(index);
                    String key = apiKey.getKey();
                    String[] apiKeyAndValue = apiKeysAndValues[index];
                    apiKeyAndValue[0] = key;
                    apiKeyAndValue[1] = key;
                }
                return apiKeysAndValues;
            }
        });

        statusLabel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Program.launch(e.text);
                } catch (IllegalArgumentException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
    }

    private void changeEnabledState() {
        boolean isKobitonIntegrated = enableKobitonIntegration.getBooleanValue();
        connectButton.setEnabled(isKobitonIntegrated);
        passwordFieldEditor.setEnabled(isKobitonIntegrated, authenticateGroup);
        userNameEditor.setEnabled(isKobitonIntegrated, authenticateGroup);
        serverEditor.setEnabled(isKobitonIntegrated, serverAndApiKeyComposite);
        apiKeyComboEditor.setEnabled(isKobitonIntegrated, serverAndApiKeyComposite);
    }

    @Override
    protected void createFieldEditors() {
        // Ignore this as we override createContents();
    }

    public static Group createGroup(Composite parent, String text, int columns, int hspan, int fill) {
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(columns, false));
        group.setText(text);
        group.setFont(parent.getFont());
        GridData gd = new GridData(fill);
        gd.horizontalSpan = hspan;
        group.setLayoutData(gd);
        return group;
    }

    public static Composite createComposite(Composite parent, int columns, int hspan, int fill) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(columns, false));
        composite.setFont(parent.getFont());
        GridData gd = new GridData(fill);
        gd.horizontalSpan = hspan;
        composite.setLayoutData(gd);
        return composite;
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.PREFERENCE_KOBITON;
    }
}
