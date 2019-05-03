package com.kms.katalon.composer.integration.analytics.preferences;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.integration.analytics.dialog.NewProjectDialog;
import com.kms.katalon.composer.integration.analytics.handlers.AnalyticsAuthorizationHandler;
import com.kms.katalon.constants.ActivationPreferenceConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.util.CryptoUtil;

public class AnalyticsPreferencesPage extends FieldEditorPreferencePageWithHelp {

    private Composite container;

    private Composite enablerComposite;

    private Composite mainComposite;

    private Button btnConnect;

    private Button enableAnalyticsIntegration;

    private Button cbxAutoSubmit, cbxAttachScreenshot, cbxAttachCaptureVideo;

    private Text txtServerUrl, txtEmail, txtPassword;

    private Label lblStatus;

    private Combo cbbProjects;

    private Combo cbbTeams;

    private List<AnalyticsProject> projects = new ArrayList<>();

    private List<AnalyticsTeam> teams = new ArrayList<>();

    private Button btnCreate;

    private AnalyticsSettingStore analyticsSettingStore;

    private Button chckEncrypt;

    public AnalyticsPreferencesPage() {
        analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        enablerComposite = new Composite(container, SWT.NONE);
        enablerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        enablerComposite.setLayout(new GridLayout());

        enableAnalyticsIntegration = new Button(enablerComposite, SWT.CHECK);
        enableAnalyticsIntegration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        enableAnalyticsIntegration
                .setText(ComposerIntegrationAnalyticsMessageConstants.LBL_ENABLE_ANALYTICS_INTEGRATION);

        mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glMainComposite = new GridLayout(1, false);
        glMainComposite.marginWidth = 0;
        glMainComposite.marginHeight = 0;
        mainComposite.setLayout(glMainComposite);

        createAuthenticationGroup();
        createSelectGroup();
        createTestResultGroup();

        addListeners();
        initialize();

        return container;
    }

    private void createAuthenticationGroup() {
        Group grpAuthentication = new Group(mainComposite, SWT.NONE);
        grpAuthentication.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glGrpAuthentication = new GridLayout(2, false);
        glGrpAuthentication.horizontalSpacing = 15;
        grpAuthentication.setLayout(glGrpAuthentication);
        grpAuthentication.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_AUTHENTICATE_GROUP);

        Label lblServerUrl = new Label(grpAuthentication, SWT.NONE);
        lblServerUrl.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_SERVER_URL);

        txtServerUrl = new Text(grpAuthentication, SWT.BORDER);
        txtServerUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblEmail = new Label(grpAuthentication, SWT.NONE);
        lblEmail.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_EMAIL);

        txtEmail = new Text(grpAuthentication, SWT.BORDER);
        txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(grpAuthentication, SWT.NONE);
        lblPassword.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PASSWORD);

        Composite passwordComposite = new Composite(grpAuthentication, SWT.NONE);
        passwordComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glPassword = new GridLayout(2, false);
        glPassword.marginWidth = 0;
        glPassword.marginHeight = 0;
        passwordComposite.setLayout(glPassword);

        txtPassword = new Text(passwordComposite, SWT.BORDER);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        chckEncrypt = new Button(grpAuthentication, SWT.CHECK);
        chckEncrypt.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 2, 1));
        chckEncrypt.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_ENABLE_ANTHENTICATION_ENCRYPTION);

        Composite compConnect = new Composite(grpAuthentication, SWT.NONE);
        compConnect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        GridLayout glConnect = new GridLayout(2, false);
        glConnect.marginHeight = 0;
        glConnect.marginWidth = 0;
        compConnect.setLayout(glConnect);

        btnConnect = new Button(compConnect, SWT.NONE);
        btnConnect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnConnect.setText(ComposerIntegrationAnalyticsMessageConstants.BTN_CONNECT);

        lblStatus = new Label(compConnect, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    }

    private void createSelectGroup() {
        Group grpSelect = new Group(mainComposite, SWT.NONE);
        grpSelect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glGrpSelect = new GridLayout(4, false);
        grpSelect.setLayout(glGrpSelect);
        grpSelect.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_SELECT_GROUP);

        Label lblTeam = new Label(grpSelect, SWT.NONE);
        lblTeam.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEAM);

        cbbTeams = new Combo(grpSelect, SWT.READ_ONLY);
        cbbTeams.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Label lblProject = new Label(grpSelect, SWT.NONE);
        lblProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PROJECT);

        cbbProjects = new Combo(grpSelect, SWT.READ_ONLY);
        cbbProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        btnCreate = new Button(grpSelect, SWT.NONE);
        btnCreate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        btnCreate.setText(ComposerIntegrationAnalyticsMessageConstants.BTN_NEW_PROJECT);
    }

    private void createTestResultGroup() {
        Group grpTestResult = new Group(mainComposite, SWT.NONE);
        grpTestResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glGrpTestResult = new GridLayout(1, false);
        glGrpTestResult.horizontalSpacing = 15;
        grpTestResult.setLayout(glGrpTestResult);
        grpTestResult.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEST_RESULT_GROUP);

        cbxAutoSubmit = new Button(grpTestResult, SWT.CHECK);
        cbxAutoSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbxAutoSubmit.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEST_RESULT_AUTO_SUBMIT);

        Composite attachComposite = new Composite(grpTestResult, SWT.NONE);
        GridLayout glGrpAttach = new GridLayout(1, false);
        glGrpAttach.marginLeft = 15;
        attachComposite.setLayout(glGrpAttach);
        attachComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        cbxAttachScreenshot = new Button(attachComposite, SWT.CHECK);
        cbxAttachScreenshot.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbxAttachScreenshot.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEST_RESULT_ATTACH_SCREENSHOT);

        cbxAttachCaptureVideo = new Button(attachComposite, SWT.CHECK);
        cbxAttachCaptureVideo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbxAttachCaptureVideo
                .setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEST_RESULT_ATTACH_CAPTURED_VIDEO);
        cbxAttachCaptureVideo.setVisible(false);
    }

    @Override
    protected void initialize() {
        super.initialize();
        fillData();
        changeEnabled();
    }

    @Override
    protected void createFieldEditors() {
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        if (!isInitialized()) {
            return;
        }
        changeEnabled();
        updateDataStore();
    }

    @Override
    public boolean performOk() {

        if (!isInitialized()) {
            return true;
        }
        
        boolean integrationEnabled = enableAnalyticsIntegration.getSelection();
        
        if (!integrationEnabled) {
            updateDataStore();
            return true;
        }

        if (cbbTeams.getSelectionIndex() == -1) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), ComposerAnalyticsStringConstants.ERROR,
                    ComposerIntegrationAnalyticsMessageConstants.REPORT_MSG_MUST_CONNECT_SUCCESSFULLY);
            return false;
        }

        if (StringUtils.isEmpty(txtEmail.getText()) || StringUtils.isEmpty(txtPassword.getText())
                || StringUtils.isEmpty(txtServerUrl.getText())) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), ComposerAnalyticsStringConstants.ERROR,
                    ComposerIntegrationAnalyticsMessageConstants.REPORT_MSG_MUST_ENTER_REQUIRED_INFORMATION);
            return false;
        }

        changeEnabled();
        updateDataStore();
        return super.performOk();
    }

    @Override
    public boolean performCancel() {
        try {
            analyticsSettingStore.enableIntegration(isIntegratedSuccessfully());
            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
            eventBroker.post(EventConstants.IS_INTEGRATED, isIntegratedSuccessfully());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return super.performCancel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (FieldEditor.VALUE.equals(event.getProperty())) {
            handleFieldEditorValueChanged(event);
        }
        super.propertyChange(event);
    }

    private void handleFieldEditorValueChanged(PropertyChangeEvent event) {
        if (event.getSource() == enableAnalyticsIntegration) {
            changeEnabled();
        }
    }

    private void fillData() {
        try {
            boolean encryptionEnabled = analyticsSettingStore.isEncryptionEnabled();
            enableAnalyticsIntegration.setSelection(analyticsSettingStore.isIntegrationEnabled());

            cbbTeams.setItems();
            cbbProjects.setItems();

            String password = analyticsSettingStore.getPassword(analyticsSettingStore.isEncryptionEnabled());
            String serverUrl = analyticsSettingStore.getServerEndpoint(analyticsSettingStore.isEncryptionEnabled());
            String email = analyticsSettingStore.getEmail(analyticsSettingStore.isEncryptionEnabled());

            if (enableAnalyticsIntegration.getSelection()) {
                AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(
                        analyticsSettingStore.getServerEndpoint(encryptionEnabled),
                        analyticsSettingStore.getEmail(encryptionEnabled), 
                        password, 
                        analyticsSettingStore);
                if (tokenInfo == null){
                    txtEmail.setText(analyticsSettingStore.getEmail(encryptionEnabled));
                    txtServerUrl.setText(analyticsSettingStore.getServerEndpoint(encryptionEnabled));
                    maskPasswordField();
                    return;
                }
                teams = AnalyticsAuthorizationHandler.getTeams(analyticsSettingStore.getServerEndpoint(encryptionEnabled),
                        analyticsSettingStore.getEmail(encryptionEnabled), password, tokenInfo,
                        new ProgressMonitorDialog(getShell()));
                projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password,
                        teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams)), tokenInfo,
                        new ProgressMonitorDialog(getShell()));

                if (teams != null && !teams.isEmpty()) {
                    cbbTeams.setItems(AnalyticsAuthorizationHandler.getTeamNames(teams).toArray(new String[teams.size()]));
                    cbbTeams.select(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
                }

                if (teams != null && teams.size() > 0) {
                    setProjectsBasedOnTeam(teams, projects, analyticsSettingStore.getServerEndpoint(encryptionEnabled),
                            analyticsSettingStore.getEmail(encryptionEnabled), password);
                }
            }

            txtEmail.setText(analyticsSettingStore.getEmail(encryptionEnabled));
            txtPassword.setText(password);
            chckEncrypt.setSelection(analyticsSettingStore.isEncryptionEnabled());
            maskPasswordField();
            txtServerUrl.setText(analyticsSettingStore.getServerEndpoint(encryptionEnabled));
            cbxAutoSubmit.setSelection(analyticsSettingStore.isAutoSubmit());
            cbxAttachScreenshot.setSelection(analyticsSettingStore.isAttachScreenshot());
            cbxAttachCaptureVideo.setSelection(analyticsSettingStore.isAttachCapturedVideos());

            ScopedPreferenceStore preferenceStore = PreferenceStoreManager
                    .getPreferenceStore(ActivationPreferenceConstants.ACTIVATION_INFO_STORAGE);
            String preferenceEmail = preferenceStore.getString(ActivationPreferenceConstants.ACTIVATION_INFO_EMAIL);
            String preferencePassword = preferenceStore
                    .getString(ActivationPreferenceConstants.ACTIVATION_INFO_PASSWORD);

            if (!StringUtils.isEmpty(preferenceEmail) && !StringUtils.isEmpty(preferencePassword)) {
                txtEmail.setText(CryptoUtil.decode(CryptoUtil.getDefault(preferenceEmail)));
                txtPassword.setText(CryptoUtil.decode(CryptoUtil.getDefault(preferencePassword)));
                // empty preference store password
                preferenceStore.setValue(ActivationPreferenceConstants.ACTIVATION_INFO_PASSWORD, StringUtils.EMPTY);
            }

        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
    }

    private void maskPasswordField() {
        txtPassword.setEchoChar(GlobalStringConstants.CR_ECO_PASSWORD.charAt(0));
    }

    private void changeEnabled() {
        boolean isAnalyticsIntegrated = enableAnalyticsIntegration.getSelection();
        btnConnect.setEnabled(isAnalyticsIntegrated);
        txtPassword.setEnabled(isAnalyticsIntegrated);
        txtEmail.setEnabled(isAnalyticsIntegrated);
        txtServerUrl.setEnabled(isAnalyticsIntegrated);
        cbbProjects.setEnabled(isAnalyticsIntegrated);
        btnCreate.setEnabled(isAnalyticsIntegrated);
        cbxAutoSubmit.setEnabled(isAnalyticsIntegrated);
        cbxAutoSubmit.setSelection(isAnalyticsIntegrated);
        cbxAttachScreenshot.setEnabled(isAnalyticsIntegrated);
        cbxAttachScreenshot.setSelection(isAnalyticsIntegrated);
        cbxAttachCaptureVideo.setEnabled(isAnalyticsIntegrated);
        chckEncrypt.setEnabled(isAnalyticsIntegrated);
        chckEncrypt.setSelection(isAnalyticsIntegrated);
    }

    private boolean isIntegratedSuccessfully() {
        if (!isInitialized()) {
            return false;
        }

        boolean isAnalyticsIntegrated = enableAnalyticsIntegration.getSelection();
        return isAnalyticsIntegrated && !teams.isEmpty();
    }

    private void updateDataStore() {
        try {
            boolean encryptionEnabled = chckEncrypt.getSelection();
            analyticsSettingStore.enableIntegration(isIntegratedSuccessfully());
            analyticsSettingStore.setServerEndPoint(txtServerUrl.getText(), encryptionEnabled);
            analyticsSettingStore.setEmail(txtEmail.getText(), encryptionEnabled);
            analyticsSettingStore.setPassword(txtPassword.getText(), encryptionEnabled);
            analyticsSettingStore.enableEncryption(encryptionEnabled);
            if (!teams.isEmpty()) {
                analyticsSettingStore.setTeam(teams.get(cbbTeams.getSelectionIndex()));
            }
            analyticsSettingStore.setProject(
                    cbbProjects.getSelectionIndex() != -1 ? projects.get(cbbProjects.getSelectionIndex()) : null);
            analyticsSettingStore.setAutoSubmit(cbxAutoSubmit.getSelection());
            analyticsSettingStore.setAttachScreenshot(cbxAttachScreenshot.getSelection());
            analyticsSettingStore.setAttachLog(enableAnalyticsIntegration.getSelection());
            analyticsSettingStore.setAttachCapturedVideos(cbxAttachCaptureVideo.getSelection());

            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
            eventBroker.post(EventConstants.IS_INTEGRATED, isIntegratedSuccessfully());
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
    }

    private void addListeners() {
        enableAnalyticsIntegration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeEnabled();
            }
        });

        btnConnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String serverUrl = txtServerUrl.getText();
                String email = txtEmail.getText();
                String password = txtPassword.getText();
                if (StringUtils.isEmpty(serverUrl) || StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(),
                            ComposerAnalyticsStringConstants.ERROR,
                            ComposerIntegrationAnalyticsMessageConstants.REPORT_MSG_MUST_ENTER_REQUIRED_INFORMATION);
                    return;
                }
                cbbTeams.setItems();
                cbbProjects.setItems();
                AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email, password, analyticsSettingStore);
                if (tokenInfo == null) {
                    return;
                }
                teams = AnalyticsAuthorizationHandler.getTeams(serverUrl, email, password, tokenInfo, new ProgressMonitorDialog(getShell()));
                projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password,
                        teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams)),
                        tokenInfo,
                        new ProgressMonitorDialog(getShell()));

                if (teams != null && !teams.isEmpty()) {
                    cbbTeams.setItems(AnalyticsAuthorizationHandler.getTeamNames(teams).toArray(new String[teams.size()]));
                    cbbTeams.select(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
                }

                setProjectsBasedOnTeam(teams, projects, serverUrl, email, password);
            }
        });

        cbbTeams.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    String serverUrl = txtServerUrl.getText();
                    String email = txtEmail.getText();
                    String password = txtPassword.getText();
                    AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email, password, analyticsSettingStore);
                    projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password,
                            teams.get(cbbTeams.getSelectionIndex()), tokenInfo, new ProgressMonitorDialog(getShell()));
                    analyticsSettingStore.setTeam(teams.get(cbbTeams.getSelectionIndex()));
                    setProjectsBasedOnTeam(teams, projects, serverUrl, email, password);

                } catch (IOException ex) {
                    LoggerSingleton.logError(ex);
                    MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR, ex.getMessage());
                }
            }
        });

        btnCreate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String serverUrl = txtServerUrl.getText();
                String email = txtEmail.getText();
                String password = txtPassword.getText();

                AnalyticsTeam team = null;
                if (teams != null && teams.size() > 0) {
                    team = teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
                }

                NewProjectDialog dialog = new NewProjectDialog(btnCreate.getDisplay().getActiveShell(), serverUrl,
                        email, password, team);
                if (dialog.open() == Dialog.OK) {
                    AnalyticsProject createdProject = dialog.getAnalyticsProject();
                    if (createdProject != null) {
                        try {
                            analyticsSettingStore.setProject(createdProject);
                            AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email, password, analyticsSettingStore);
                            projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password, team, tokenInfo,
                                    new ProgressMonitorDialog(getShell()));
                            if (projects == null) {
                                return;
                            }
                            cbbProjects.setItems(AnalyticsAuthorizationHandler.getProjectNames(projects)
                                    .toArray(new String[projects.size()]));
                            cbbProjects.select(
                                    AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
                        } catch (IOException ex) {
                            LoggerSingleton.logError(ex);
                            MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR,
                                    ex.getMessage());
                        }
                    }
                }
            }
        });

        cbxAutoSubmit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cbxAttachScreenshot.setSelection(cbxAutoSubmit.getSelection());
            }
        });

    }

    private void setProjectsBasedOnTeam(List<AnalyticsTeam> teams, List<AnalyticsProject> projects, String serverUrl,
            String email, String password) {
        AnalyticsTeam team = teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));

        if (projects != null && !projects.isEmpty()) {
            cbbProjects.setItems(AnalyticsAuthorizationHandler.getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
        } else {
        	cbbProjects.setItems(AnalyticsAuthorizationHandler.getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
        }
        String role = team.getRole();
        if (role.equals("USER")) {
            btnCreate.setEnabled(false);
        } else {
            btnCreate.setEnabled(true);
        }
    }

    protected boolean isInitialized() {
        return enableAnalyticsIntegration != null;
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_KATALON_ANALYTICS;
    }
}
