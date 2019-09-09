package com.kms.katalon.composer.integration.analytics.uploadProject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.handler.AnalyticsAuthorizationHandler;
import com.kms.katalon.integration.analytics.handler.AnalyticsGridHandler;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class StoreProjectCodeToCloudDialog extends Dialog {

    private Combo cbbProjects;

    private Combo cbbTeams;

    private Text txtCodeRepoName;

    private Label lblStatus;

    private AnalyticsSettingStore analyticsSettingStore;

    private List<AnalyticsProject> projects = new ArrayList<>();

    private List<AnalyticsTeam> teams = new ArrayList<>();

    private String serverUrl, email, password;

    private ProjectEntity currentProject;

    private ProjectController pController = ProjectController.getInstance();

    public StoreProjectCodeToCloudDialog(Shell parentShell) {
        super(parentShell);
        currentProject = pController.getCurrentProject();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ComposerIntegrationAnalyticsMessageConstants.BTN_UPLOAD, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_TITLE_UPLOAD_CODE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 15;

        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblGetting = new Label(composite, SWT.NONE);
        lblGetting.setText(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_UPLOAD_CODE);
        GridData lblGettingGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 2);
        lblGetting.setLayoutData(lblGettingGridData);

        Group grpUploadProject = new Group(composite, SWT.NONE);
        grpUploadProject.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glGrpAuthentication = new GridLayout(2, false);
        glGrpAuthentication.horizontalSpacing = 15;
        grpUploadProject.setLayout(glGrpAuthentication);

        Label lblTeam = new Label(grpUploadProject, SWT.NONE);
        lblTeam.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEAM);

        cbbTeams = new Combo(grpUploadProject, SWT.READ_ONLY);
        GridData cbbTeamsGritData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        cbbTeams.setLayoutData(cbbTeamsGritData);
        cbbTeams.setEnabled(false);

        Label lblProject = new Label(grpUploadProject, SWT.NONE);
        lblProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PROJECT);

        cbbProjects = new Combo(grpUploadProject, SWT.READ_ONLY);
        GridData cbbProjectsGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        cbbProjects.setLayoutData(cbbProjectsGridData);
        cbbProjects.setEnabled(false);

        Label lblCodeRepoName = new Label(grpUploadProject, SWT.NONE);
        lblCodeRepoName.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_CODE_REPO_NAME);

        txtCodeRepoName = new Text(grpUploadProject, SWT.BORDER);
        GridData txtCodeRepoNameGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        txtCodeRepoName.setLayoutData(txtCodeRepoNameGridData);
        String nameSuggest = currentProject.getName();
        txtCodeRepoName.setText(nameSuggest);

        lblStatus = new Label(grpUploadProject, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 2));

        addListener();
        fillData();
        return composite;
    }

    private void fillData() {
        try {
            analyticsSettingStore = new AnalyticsSettingStore(
                    ProjectController.getInstance().getCurrentProject().getFolderLocation());
            boolean encryptionEnabled = analyticsSettingStore.isEncryptionEnabled();
            password = analyticsSettingStore.getPassword(encryptionEnabled);
            serverUrl = analyticsSettingStore.getServerEndpoint(encryptionEnabled);
            email = analyticsSettingStore.getEmail(encryptionEnabled);

            Executors.newFixedThreadPool(1).submit(() -> {
                UISynchronizeService.syncExec(() -> {
                            setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER, false);
                        });
                UISynchronizeService.syncExec(() -> {
                    AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email, password,
                            analyticsSettingStore);
                    if (tokenInfo == null) {
                        setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_REQUEST_TOKEN_ERROR, true);
                        return;
                    }
                    setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_TEAMS, false);
                    Long orgId = analyticsSettingStore.getOrganization().getId();
                    teams = AnalyticsAuthorizationHandler.getTeams(serverUrl, email, password, orgId, tokenInfo);
                    if (teams != null && !teams.isEmpty()) {
                        int defaultTeamIndex = AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams);
                        cbbTeams.setItems(
                                AnalyticsAuthorizationHandler.getTeamNames(teams).toArray(new String[teams.size()]));
                        cbbTeams.select(defaultTeamIndex);

                        setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_PROJECTS, false);
                        projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password, teams.get(defaultTeamIndex), tokenInfo);
                        setProjectsBasedOnTeam(projects);
                    }
                    cbbTeams.setEnabled(true);
                    cbbProjects.setEnabled(true);
                    setProgressMessage("", false);
                });
            });
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
    }

    private void setProjectsBasedOnTeam(List<AnalyticsProject> projects) {
        if (projects != null && !projects.isEmpty()) {
            cbbProjects.setItems(
                    AnalyticsAuthorizationHandler.getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
        } else {
            cbbProjects.clearSelection();
            cbbProjects.removeAll();
        }
    }

    private void addListener() {
        cbbTeams.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cbbTeams.setEnabled(false);
                cbbProjects.setEnabled(false);
                Executors.newFixedThreadPool(1).submit(() -> {
                    UISynchronizeService.syncExec(() -> {
                        setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_GETTING_PROJECTS, false);
                    });
                    UISynchronizeService.syncExec(() -> {
                        AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email,
                                password, analyticsSettingStore);
                        projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, email, password,
                                teams.get(cbbTeams.getSelectionIndex()), tokenInfo);
                        setProjectsBasedOnTeam(projects);
                        setProgressMessage("", false);
                        cbbTeams.setEnabled(true);
                        cbbProjects.setEnabled(true);
                    });
                 });
            }
        });
    }

    @Override
    protected void okPressed() {
        String nameFileZip = txtCodeRepoName.getText();

        if (nameFileZip.isEmpty()) {
            MultiStatusErrorDialog.showErrorDialog(
                    ComposerIntegrationAnalyticsMessageConstants.STORE_CODE_ERROR_NO_FILE_NAME,
                    ComposerAnalyticsStringConstants.ERROR,
                    ComposerIntegrationAnalyticsMessageConstants.STORE_CODE_ERROR_NO_NAME);
            return;
        }

        int currentIndexProject = cbbProjects.getSelectionIndex();
        AnalyticsProject sellectProject = projects.get(currentIndexProject);

        String folderCurrentProject = currentProject.getFolderLocation();

        AnalyticsGridHandler.uploadProject(serverUrl, email, password, nameFileZip, sellectProject,
                folderCurrentProject, new ProgressMonitorDialog(getShell()));

        try {
            Long teamId = sellectProject.getTeamId();
            Long projectId = sellectProject.getId();
            URIBuilder builder = new URIBuilder(serverUrl);
            builder.setPath(String.format(AnalyticsStringConstants.ANALYTICS_URL_TEST_PROJECT, teamId, projectId));
            Program.launch(builder.toString());
        } catch (URISyntaxException e) {
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
        super.okPressed();
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
    protected void cancelPressed() {
        super.cancelPressed();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(550, super.getInitialSize().y);
    }
}
