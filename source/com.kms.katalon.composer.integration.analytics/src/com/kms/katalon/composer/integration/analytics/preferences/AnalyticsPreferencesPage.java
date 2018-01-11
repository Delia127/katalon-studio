package com.kms.katalon.composer.integration.analytics.preferences;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.integration.analytics.dialog.NewProjectDialog;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class AnalyticsPreferencesPage extends FieldEditorPreferencePageWithHelp {

    private Composite container;

    private Composite enablerComposite;

    private Composite mainComposite;

    private Button btnConnect;

    private Button enableAnalyticsIntegration;

    private Button cbxAutoSubmit, cbxAttachScreenshot, cbxAttachLog, cbxAttachCaptureVideo;

    private Text txtServerUrl, txtEmail, txtPassword;

    private Label lblStatus;

    private Combo cbbProjects;

    private Combo cbbTeams;

    private List<AnalyticsProject> projects = new ArrayList<>();

    private List<AnalyticsTeam> teams = new ArrayList<>();

    private Button btnCreate;

    private AnalyticsSettingStore analyticsSettingStore;

    private Button chckShowPassword, chckEncryptPassword;

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
        GridLayout glPassword = new GridLayout(3, false);
        glPassword.marginWidth = 0;
        glPassword.marginHeight = 0;
        passwordComposite.setLayout(glPassword);

        txtPassword = new Text(passwordComposite, SWT.BORDER);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        chckShowPassword = new Button(passwordComposite, SWT.CHECK);
        chckShowPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        chckShowPassword.setText("Show Password");

        chckEncryptPassword = new Button(passwordComposite, SWT.CHECK);
        chckEncryptPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
        chckEncryptPassword.setText("Encrypt Password");

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

        cbxAttachLog = new Button(attachComposite, SWT.CHECK);
        cbxAttachLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbxAttachLog.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEST_RESULT_ATTACH_LOG);

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
        changeEnabled();
        updateDataStore();
        return super.performOk();
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
            enableAnalyticsIntegration.setSelection(analyticsSettingStore.isIntegrationEnabled());

            cbbTeams.setItems();
            cbbProjects.setItems();

            String password = analyticsSettingStore.getPassword(analyticsSettingStore.isPasswordEncryptionEnabled());
            if (enableAnalyticsIntegration.getSelection()) {
                teams = getTeams(analyticsSettingStore.getServerEndpoint(), analyticsSettingStore.getEmail(),
                        password, false);
                if (teams != null && !teams.isEmpty()) {
                    cbbTeams.setItems(getTeamNames(teams).toArray(new String[teams.size()]));
                    cbbTeams.select(getDefaultTeamIndex());
                }

                if (teams != null && teams.size() > 0) {
                    AnalyticsTeam team = teams.get(getDefaultTeamIndex());
                    projects = getProjects(analyticsSettingStore.getServerEndpoint(), analyticsSettingStore.getEmail(),
                            password, team, false);
                    if (projects != null && !projects.isEmpty()) {
                        cbbProjects.setItems(getProjectNames(projects).toArray(new String[projects.size()]));
                        cbbProjects.select(getDefaultProjectIndex());
                    }
                }
            }

            txtEmail.setText(analyticsSettingStore.getEmail());
            txtPassword.setText(password);
            chckEncryptPassword.setSelection(analyticsSettingStore.isPasswordEncryptionEnabled());
            maskPasswordField();
            txtServerUrl.setText(analyticsSettingStore.getServerEndpoint());
            cbxAutoSubmit.setSelection(analyticsSettingStore.isAutoSubmit());
            cbxAttachScreenshot.setSelection(analyticsSettingStore.isAttachScreenshot());
            cbxAttachLog.setSelection(analyticsSettingStore.isAttachLog());
            cbxAttachCaptureVideo.setSelection(analyticsSettingStore.isAttachCapturedVideos());
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
    }

    private void maskPasswordField() {
        if (chckShowPassword.getSelection()) {
            // show password
            txtPassword.setEchoChar('\0');
        } else {
            txtPassword.setEchoChar(GlobalStringConstants.CR_ECO_PASSWORD.charAt(0));
        }
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
        cbxAttachScreenshot.setEnabled(isAnalyticsIntegrated);
        cbxAttachLog.setEnabled(isAnalyticsIntegrated);
        cbxAttachCaptureVideo.setEnabled(isAnalyticsIntegrated);
        chckEncryptPassword.setEnabled(isAnalyticsIntegrated);
        chckShowPassword.setEnabled(isAnalyticsIntegrated);
    }

    private void updateDataStore() {
        try {
            analyticsSettingStore.enableIntegration(enableAnalyticsIntegration.getSelection());
            analyticsSettingStore.setServerEndPoint(txtServerUrl.getText());
            analyticsSettingStore.setEmail(txtEmail.getText());
            analyticsSettingStore.setPassword(txtPassword.getText(), chckEncryptPassword.getSelection());
            analyticsSettingStore.enablePasswordEncryption(chckEncryptPassword.getSelection());
            analyticsSettingStore.setProject(
                    cbbProjects.getSelectionIndex() != -1 ? projects.get(cbbProjects.getSelectionIndex()) : null);
            analyticsSettingStore.setAutoSubmit(cbxAutoSubmit.getSelection());
            analyticsSettingStore.setAttachScreenshot(cbxAttachScreenshot.getSelection());
            analyticsSettingStore.setAttachLog(cbxAttachLog.getSelection());
            analyticsSettingStore.setAttachCapturedVideos(cbxAttachCaptureVideo.getSelection());
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

                teams = getTeams(serverUrl, email, password, false);
                if (teams != null && !teams.isEmpty()) {
                    cbbTeams.setItems(getTeamNames(teams).toArray(new String[teams.size()]));
                    cbbTeams.select(getDefaultTeamIndex());
                }

                AnalyticsTeam team = teams.get(getDefaultTeamIndex());

                projects = getProjects(serverUrl, email, password, team, false);
                if (projects != null && !projects.isEmpty()) {
                    cbbProjects.setItems(getProjectNames(projects).toArray(new String[projects.size()]));
                    cbbProjects.select(getDefaultProjectIndex());
                }

            }
        });

        cbbTeams.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    String serverUrl = txtServerUrl.getText();
                    String email = txtEmail.getText();
                    String password = txtPassword.getText();

                    analyticsSettingStore.setTeam(teams.get(cbbTeams.getSelectionIndex()));
                    AnalyticsTeam team = teams.get(getDefaultTeamIndex());
                    projects = getProjects(serverUrl, email, password, team, false);
                    if (projects != null && !projects.isEmpty()) {
                        cbbProjects.setItems(getProjectNames(projects).toArray(new String[projects.size()]));
                        cbbProjects.select(getDefaultProjectIndex());
                    }
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
                    team = teams.get(getDefaultTeamIndex());
                }

                NewProjectDialog dialog = new NewProjectDialog(btnCreate.getDisplay().getActiveShell(), serverUrl,
                        email, password, team);
                if (dialog.open() == Dialog.OK) {
                    AnalyticsProject createdProject = dialog.getAnalyticsProject();
                    if (createdProject != null) {
                        try {
                            analyticsSettingStore.setProject(createdProject);
                            projects = getProjects(serverUrl, email, password, team, false);
                            if (projects == null) {
                                return;
                            }
                            cbbProjects.setItems(getProjectNames(projects).toArray(new String[projects.size()]));
                            cbbProjects.select(getDefaultProjectIndex());
                        } catch (IOException ex) {
                            LoggerSingleton.logError(ex);
                            MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR,
                                    ex.getMessage());
                        }
                    }
                }
            }
        });
        
        chckShowPassword.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                maskPasswordField();
            }
        });

    }

    private List<AnalyticsProject> getProjects(final String serverUrl, final String email, final String password,
            final AnalyticsTeam team, final boolean isUpdateStatus) {
        final List<AnalyticsProject> projects = new ArrayList<>();
        try {
            new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_RETRIEVING_PROJECTS,
                                2);
                        monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER);
                        final AnalyticsTokenInfo tokenInfo = AnalyticsApiProvider.requestToken(serverUrl, email,
                                password);
                        monitor.worked(1);
                        monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_PROJECTS);
                        final List<AnalyticsProject> loaded = AnalyticsApiProvider.getProjects(serverUrl, team,
                                tokenInfo.getAccess_token());
                        if (loaded != null && !loaded.isEmpty()) {
                            projects.addAll(loaded);
                        }
                        monitor.worked(1);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            return projects;
        } catch (InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof AnalyticsApiExeception) {
                MessageDialog.openError(getShell(), ComposerAnalyticsStringConstants.ERROR, cause.getMessage());
            } else {
                LoggerSingleton.logError(cause);
            }
        } catch (InterruptedException e) {
            // Ignore this
        }
        return null;
    }

    private List<AnalyticsTeam> getTeams(final String serverUrl, final String email, final String password,
            final boolean isUpdateStatus) {
        final List<AnalyticsTeam> teams = new ArrayList<>();
        try {
            new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_RETRIEVING_TEAMS, 2);
                        monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER);
                        final AnalyticsTokenInfo tokenInfo = AnalyticsApiProvider.requestToken(serverUrl, email,
                                password);
                        monitor.worked(1);
                        monitor.subTask(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_TEAMS);
                        final List<AnalyticsTeam> loaded = AnalyticsApiProvider.getTeams(serverUrl,
                                tokenInfo.getAccess_token());
                        if (loaded != null && !loaded.isEmpty()) {
                            teams.addAll(loaded);
                        }
                        monitor.worked(1);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            return teams;
        } catch (InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof AnalyticsApiExeception) {
                MessageDialog.openError(getShell(), ComposerAnalyticsStringConstants.ERROR, cause.getMessage());
            } else {
                LoggerSingleton.logError(cause);
            }
        } catch (InterruptedException e) {
            // Ignore this
        }
        return null;
    }

    private List<String> getProjectNames(List<AnalyticsProject> projects) {
        List<String> names = new ArrayList<>();
        projects.forEach(p -> names.add(p.getName()));
        return names;
    }

    private List<String> getTeamNames(List<AnalyticsTeam> projects) {
        List<String> names = new ArrayList<>();
        projects.forEach(p -> names.add(p.getName()));
        return names;
    }

    private int getDefaultProjectIndex() {
        int selectionIndex = 0;

        try {
            AnalyticsProject storedProject = analyticsSettingStore.getProject();
            if (storedProject != null && storedProject.getId() != null) {
                for (int i = 0; i < projects.size(); i++) {
                    AnalyticsProject p = projects.get(i);
                    if (p.getId() == storedProject.getId()) {
                        selectionIndex = i;
                    }
                }
            }
        } catch (IOException e) {
            // do nothing
        }
        return selectionIndex;
    }

    private int getDefaultTeamIndex() {
        int selectionIndex = 0;

        try {
            AnalyticsTeam storedProject = analyticsSettingStore.getTeam();
            if (storedProject != null && storedProject.getId() != null && teams != null) {
                for (int i = 0; i < teams.size(); i++) {
                    AnalyticsTeam p = teams.get(i);
                    if (p.getId() == storedProject.getId()) {
                        selectionIndex = i;
                    }
                }
            }
        } catch (IOException e) {
            // do nothing
        }
        return selectionIndex;
    }

    protected boolean isInitialized() {
        return enableAnalyticsIntegration != null;
    }

    @Override
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_KATALON_ANALYTICS;
    }
}
