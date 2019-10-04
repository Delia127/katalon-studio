package com.kms.katalon.composer.integration.analytics.dialog;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.handler.AnalyticsAuthorizationHandler;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class UploadSelectionDialog extends Dialog {

    public static final int UPLOAD_ID = 2;

    public static final int CANCEL_ID = 3;

    private Button btnUpload;

    private Button btnCancel;

    private Button btnCreate;

    private Combo cbbProjects;

    private Combo cbbTeams;

    private Label lblStatus;

    private List<AnalyticsTeam> teams;

    private List<AnalyticsProject> projects;

    private AnalyticsSettingStore analyticsSettingStore;

    private String email, password, serverUrl;

    private AnalyticsOrganization organization;

    private AnalyticsTokenInfo tokenInfo;

    public UploadSelectionDialog(Shell parentShell) {
        super(parentShell);
        analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        try {
            boolean encryptionEnabled = true;
            email = analyticsSettingStore.getEmail(encryptionEnabled);
            password = analyticsSettingStore.getPassword(encryptionEnabled);
            serverUrl = analyticsSettingStore.getServerEndpoint(encryptionEnabled);
            organization = analyticsSettingStore.getOrganization();
        } catch (IOException | GeneralSecurityException e) {
            // ignore
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData bodyGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        bodyGridData.widthHint = 450;
        body.setLayoutData(bodyGridData);
        GridLayout bodyGridLayout = new GridLayout(1, false);
        bodyGridLayout.marginWidth = 10;
        bodyGridLayout.marginHeight = 10;
        body.setLayout(bodyGridLayout);

        CLabel lblInformation = new CLabel(body, SWT.WRAP);
        lblInformation.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));
        lblInformation.setBottomMargin(2);
        lblInformation.setText(StringConstants.LBL_SELECTION_INFORMATION);

        GridData gdLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdLabel.widthHint = 50;

        Composite teamComposite = new Composite(body, SWT.NONE);
        teamComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout teamGridLayout = new GridLayout(2, false);
        teamGridLayout.marginWidth = 0;
        teamGridLayout.marginHeight = 0;
        teamComposite.setLayout(teamGridLayout);

        Label lblTeam = new Label(teamComposite, SWT.NONE);
        lblTeam.setLayoutData(gdLabel);
        lblTeam.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEAM);

        cbbTeams = new Combo(teamComposite, SWT.READ_ONLY);
        cbbTeams.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbbTeams.setEnabled(false);

        Composite projectComposite = new Composite(body, SWT.NONE);
        projectComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout projectGridLayout = new GridLayout(3, false);
        projectGridLayout.marginWidth = 0;
        projectGridLayout.marginHeight = 0;
        projectComposite.setLayout(projectGridLayout);

        Label lblProject = new Label(projectComposite, SWT.NONE);
        lblProject.setLayoutData(gdLabel);
        lblProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PROJECT);

        cbbProjects = new Combo(projectComposite, SWT.READ_ONLY);
        cbbProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbbProjects.setEnabled(false);

        btnCreate = new Button(projectComposite, SWT.NONE);
        btnCreate.setText(ComposerIntegrationAnalyticsMessageConstants.BTN_NEW_PROJECT);
        btnCreate.setEnabled(false);

        lblStatus = new Label(body, SWT.NONE);

        connect();

        return body;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        btnUpload = createButton(parent, UPLOAD_ID, StringConstants.BTN_UPLOAD, true);
        btnUpload.setEnabled(false);

        btnCancel = createButton(parent, CANCEL_ID, StringConstants.BTN_CANCEL, false);
        addControlListeners();
    }

    private void addControlListeners() {

        btnUpload.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleUpload();
            }
        });

        btnCreate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
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
                            analyticsSettingStore.setTeam(team);
                            if (projects == null) {
                                return;
                            }
                            cbbProjects.setItems(AnalyticsAuthorizationHandler.getProjectNames(projects)
                                    .toArray(new String[projects.size()]));
                            cbbProjects.select(AnalyticsAuthorizationHandler
                                    .getDefaultProjectIndex(analyticsSettingStore, projects));
                        } catch (IOException ex) {
                            LoggerSingleton.logError(ex);
                            MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR,
                                    ex.getMessage());
                        }
                    }
                }
                connect();
            }
        });

        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }
        });

        cbbTeams.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                enableObject(false);
                cbbProjects.setItems();
                Executors.newFixedThreadPool(1).submit(() -> {
                    UISynchronizeService.syncExec(() -> setProgressMessage(
                            ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER, false));
                    UISynchronizeService.syncExec(() -> {
                        try {
                            analyticsSettingStore.setTeam(teams.get(cbbTeams.getSelectionIndex()));
                            AnalyticsTeam team = teams.get(
                                    AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
                            AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email,
                                    password, analyticsSettingStore);
                            projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, team, tokenInfo);
                            setProjectsBasedOnTeam(teams, projects);
                            enableObject(true);
                            if (teams.get(cbbTeams.getSelectionIndex()).getRole().equals("USER")) {
                                btnCreate.setEnabled(false);
                            } else {
                                btnCreate.setEnabled(true);
                            }
                            setProgressMessage(StringUtils.EMPTY, false);
                        } catch (IOException ex) {
                            LoggerSingleton.logError(ex);
                            MultiStatusErrorDialog.showErrorDialog(ex, ComposerAnalyticsStringConstants.ERROR,
                                    ex.getMessage());
                        }
                    });
                });
            }
        });
    }

    private void setProjectsBasedOnTeam(List<AnalyticsTeam> teams, List<AnalyticsProject> projects) {

        if (projects != null && !projects.isEmpty()) {
            cbbProjects.setItems(
                    AnalyticsAuthorizationHandler.getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
        }
    }

    private void connect() {
        Executors.newFixedThreadPool(1).submit(() -> {
            UISynchronizeService.syncExec(() -> setProgressMessage(
                    ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER, false));
            UISynchronizeService.syncExec(() -> {
                tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email, password, analyticsSettingStore);
                teams = AnalyticsAuthorizationHandler.getTeams(serverUrl, organization.getId(), tokenInfo);
                projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, teams.get(0), tokenInfo);

                fillData();
                enableObject(true);

                if (teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams))
                        .getRole()
                        .equals("USER")) {
                    btnCreate.setEnabled(false);
                }
                setProgressMessage(StringUtils.EMPTY, false);
            });
        });
    }

    private void enableObject(boolean isEnable) {
        cbbTeams.setEnabled(isEnable);
        cbbProjects.setEnabled(isEnable);
        btnCreate.setEnabled(isEnable);
        btnUpload.setEnabled(isEnable);
    }
    
    private void fillData() {
        try {
            cbbTeams.setItems(AnalyticsAuthorizationHandler.getTeamNames(teams).toArray(new String[teams.size()]));
            cbbTeams.select(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
            setProjectsBasedOnTeam(teams, projects);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
    }

    private void setProgressMessage(String message, boolean isError) {
        lblStatus.setText(message);
        if (isError) {
            lblStatus.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lblStatus.setForeground(ColorUtil.getTextRunningColor());
        }
        lblStatus.getParent().layout();
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.SELECTION_DIALOG_TITLE);
    }

    private void closeDialog() {
        this.close();
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void handleUpload() {
        try {
            AnalyticsTeam team = null;
            if (teams != null && teams.size() > 0) {
                team = teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams));
            }
            analyticsSettingStore.setTeam(team);
            analyticsSettingStore.setProject(
                    cbbProjects.getSelectionIndex() != -1 ? projects.get(cbbProjects.getSelectionIndex()) : null);
        } catch (IOException e1) {
            LoggerSingleton.logError(e1);
        }
        setReturnCode(UPLOAD_ID);
        closeDialog();
    }
}
