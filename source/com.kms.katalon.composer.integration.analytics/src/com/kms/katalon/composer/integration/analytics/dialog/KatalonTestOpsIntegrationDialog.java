package com.kms.katalon.composer.integration.analytics.dialog;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsOrganization;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.handler.AnalyticsAuthorizationHandler;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class KatalonTestOpsIntegrationDialog extends AbstractDialog {

    public static final int REFRESH_ID = 3;

    public static final int OK_ID = 2;

    private Composite container;

    private Button btnOk;

    private Button btnRefresh;

    private Button btnNewProject;

    private Text txtOrganization;

    private Combo cbbProjects;

    private Combo cbbTeams;

    private Link lnkStatus;

    private List<AnalyticsProject> projects = new ArrayList<>();

    private List<AnalyticsTeam> teams = new ArrayList<>();

    private AnalyticsSettingStore analyticsSettingStore;

    private AnalyticsOrganization organization;

    private String password;

    private String serverUrl;

    private String email;

    private AnalyticsTokenInfo tokenInfo;

    private boolean isClose = false;

    public KatalonTestOpsIntegrationDialog(Shell parentShell) {
        super(parentShell);
        analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        try {
            password = analyticsSettingStore.getPassword();
            serverUrl = ApplicationInfo.getTestOpsServer();
            email = analyticsSettingStore.getEmail();
            organization = analyticsSettingStore.getOrganization();

            tokenInfo = AnalyticsAuthorizationHandler.getTokenNew(serverUrl, email, password, analyticsSettingStore);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(ComposerIntegrationAnalyticsMessageConstants.TITLE_DLG_QUICK_ANALYTICS_INTEGRATION);
    }

    public boolean checkConnection() {
        if (tokenInfo == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean close() {
        isClose = updateDataStore();
//        if (!isClose) {
//            return false;
//        }
        return super.close();
    }


    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout();
        glContainer.verticalSpacing = 5;
        container.setLayout(glContainer);

        Label lblNote = new Label(container, SWT.NONE);
        lblNote.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_QUICK_TITLE_ANALYTICS_INTEGRATION);

        Composite recommendComposite = new Composite(container, SWT.NONE);
        recommendComposite.setLayout(new GridLayout(2, false));
        recommendComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Group grpSelect = new Group(container, SWT.NONE);
        grpSelect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glGrpSelect = new GridLayout(4, false);
        grpSelect.setLayout(glGrpSelect);
        grpSelect.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_SELECT_GROUP);

        Label lblOrganization = new Label(grpSelect, SWT.NONE);
        lblOrganization.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_ORGANIZATION);

        txtOrganization = new Text(grpSelect, SWT.READ_ONLY);
        txtOrganization.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Label lblTeam = new Label(grpSelect, SWT.NONE);
        lblTeam.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEAM);

        cbbTeams = new Combo(grpSelect, SWT.READ_ONLY);
        cbbTeams.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Label lblProject = new Label(grpSelect, SWT.NONE);
        lblProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PROJECT);

        cbbProjects = new Combo(grpSelect, SWT.READ_ONLY);
        cbbProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        btnNewProject = new Button(grpSelect, SWT.NONE);
        btnNewProject.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        btnNewProject.setText(ComposerIntegrationAnalyticsMessageConstants.BTN_NEW_PROJECT);

        lnkStatus = new Link(container, SWT.WRAP);
        GridData gdStatus = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdStatus.heightHint = 30;
        lnkStatus.setLayoutData(gdStatus);

        Composite titleComposite = new Composite(container, SWT.NONE);
        titleComposite.setLayout(new GridLayout(1, false));
        titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblRecommend = new Label(titleComposite, SWT.NONE);
        lblRecommend.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_QUICK_ANALYTICS_INTEGRATION_RECOMMEND);

        Composite suggestComposite = new Composite(container, SWT.NONE);
        suggestComposite.setLayout(new GridLayout(2, false));
        suggestComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        Label lblSuggest = new Label(suggestComposite, SWT.NONE);
        lblSuggest.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_QUICK_ANALYTICS_INTEGRATION_UPLOAD);

        Label lblDir = new Label(suggestComposite, SWT.NONE);
        lblDir.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_QUICK_ANALYTICS_INTEGRATION_TO_CONFIG);
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblDir.getFont()).setStyle(SWT.BOLD | SWT.ITALIC);
        Font boldFont = boldDescriptor.createFont(lblDir.getDisplay());
        lblDir.setFont(boldFont);

        initialize();

        return container;
    }

    private void initialize() {
        if (tokenInfo != null) {
            fillData();
        }
    }

    private void fillData() {
        teams.clear();
        projects.clear();

        txtOrganization.setText(organization.getName());
        txtOrganization.setEnabled(false);

        enableObject(false);

        Executors.newFixedThreadPool(1).submit(() -> {
            UISynchronizeService.syncExec(() -> {
                setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CONNECTING_TO_SERVER,
                        false);
            });
            UISynchronizeService.syncExec(() -> {
                tokenInfo = AnalyticsAuthorizationHandler.getTokenNew(serverUrl, email, password, analyticsSettingStore);
                Long orgId = organization.getId();
                getTeam(serverUrl, orgId, tokenInfo);
                if (teams != null && teams.size() > 0) {
                    getProject(serverUrl,
                            teams.get(AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore, teams)),
                            tokenInfo);
                    cbbTeams.setItems(
                            AnalyticsAuthorizationHandler.getTeamNames(teams).toArray(new String[teams.size()]));
                    int indexSelectTeam = AnalyticsAuthorizationHandler.getDefaultTeamIndex(analyticsSettingStore,
                            teams);
                    cbbTeams.select(indexSelectTeam);
                    cbbTeams.setEnabled(true);
                    enableObject(true);
                    setProjectsBasedOnTeam(teams.get(indexSelectTeam), projects);
                } else {
                    enableObject(false);
                    String message = MessageFormat.format(ComposerIntegrationAnalyticsMessageConstants.LNK_REPORT_WARNING_MSG_NO_TEAM,
                            ApplicationInfo.getTestOpsServer(), Long.toString(orgId));
                    setProgressMessage(message, true);
                }
            });
        });
    }

    private void enableObject(boolean isEnable) {
        cbbTeams.setEnabled(isEnable);
        cbbProjects.setEnabled(isEnable);
        btnNewProject.setEnabled(isEnable);
        if (btnOk != null) {
            btnOk.setEnabled(isEnable);
        }
    }

    private void getTeam(String serverUrl, Long orgId, AnalyticsTokenInfo tokenInfo) {
        try {
            teams = AnalyticsAuthorizationHandler.getTeams(analyticsSettingStore.getServerEndpoint(), orgId, tokenInfo);
            setProgressMessage(StringUtils.EMPTY, false);
        } catch (Exception e) {
            setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_REQUEST_TOKEN_ERROR, true);
        }
    }

    private void getProject(final String serverUrl, final AnalyticsTeam team, AnalyticsTokenInfo tokenInfo) {
        try {
            projects = AnalyticsAuthorizationHandler.getProjects(serverUrl, team, tokenInfo);
            setProgressMessage(StringUtils.EMPTY, false);
        } catch (Exception e) {
            setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_REQUEST_TOKEN_ERROR, true);
        }
    }

    private void setProgressMessage(String message, boolean isError) {
        lnkStatus.setText(message);
        if (isError) {
            lnkStatus.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lnkStatus.setForeground(ColorUtil.getTextRunningColor());
        }
    }

    private void setProjectsBasedOnTeam(AnalyticsTeam team, List<AnalyticsProject> projects) {
        if (projects != null && !projects.isEmpty()) {
            cbbProjects.setEnabled(true);
            cbbProjects.setItems(
                    AnalyticsAuthorizationHandler.getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(AnalyticsAuthorizationHandler.getDefaultProjectIndex(analyticsSettingStore, projects));
            setProgressMessage(StringUtils.EMPTY, false);
        } else {
            cbbProjects.clearSelection();
            cbbProjects.removeAll();
            setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.LNK_REPORT_WARNING_MSG_NO_PROJECT, true);
        }
        String role = team.getRole();
        if (role.equals("USER")) {
            btnNewProject.setEnabled(false);
        } else {
            btnNewProject.setEnabled(true);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        btnRefresh = createButton(parent, REFRESH_ID, "Refresh", true);
        btnOk = createButton(parent, OK_ID, "OK", true);
    }

    @Override
    protected void registerControlModifyListeners() {
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isClose = updateDataStore();
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_RELOAD_DATA, true);
                okPressed();
            }
        });

        btnRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fillData();
            }
        });

        cbbTeams.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Executors.newFixedThreadPool(1).submit(() -> {
                    UISynchronizeService.syncExec(() -> {
                        setProgressMessage(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_RETRIEVING_PROJECTS,
                                false);
                    });
                    UISynchronizeService.syncExec(() -> {
                        AnalyticsTeam getSelectTeam = teams.get(cbbTeams.getSelectionIndex());
                        AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email,
                                password, analyticsSettingStore);
                        getProject(serverUrl, getSelectTeam, tokenInfo);
                        setProjectsBasedOnTeam(getSelectTeam, projects);
                    });
                });
            }
        });

        btnNewProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AnalyticsTeam team = null;
                if (teams != null && teams.size() > 0) {
                    team = teams.get(cbbTeams.getSelectionIndex());
                }
                NewProjectDialog dialog = new NewProjectDialog(btnNewProject.getDisplay().getActiveShell(), serverUrl,
                        email, password, team);
                if (dialog.open() == Dialog.OK) {
                    AnalyticsProject createdProject = dialog.getAnalyticsProject();
                    if (createdProject != null) {
                        AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.getToken(serverUrl, email,
                                password, analyticsSettingStore);
                        getProject(serverUrl, team, tokenInfo);
                        if (projects == null) {
                            return;
                        }
                        cbbProjects.setItems(AnalyticsAuthorizationHandler.getProjectNames(projects)
                                .toArray(new String[projects.size()]));
                        cbbProjects.select(AnalyticsAuthorizationHandler.getProjectIndex(createdProject, projects));
                    }
                }
            }
        });

        lnkStatus.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }

    private boolean updateDataStore() {
        try {
            if (cbbTeams.getSelectionIndex() == -1) {
//                MessageDialog.openError(Display.getCurrent().getActiveShell(), ComposerAnalyticsStringConstants.ERROR,
//                        ComposerIntegrationAnalyticsMessageConstants.REPORT_WARNING_MSG_NO_TEAM);
                return false;
            }

            if (cbbProjects.getSelectionIndex() == -1) {
//                MessageDialog.openError(Display.getCurrent().getActiveShell(), ComposerAnalyticsStringConstants.ERROR,
//                        ComposerIntegrationAnalyticsMessageConstants.REPORT_MSG_MUST_SET_PROJECT);
                return false;
            }
            analyticsSettingStore.enableIntegration(true);
            if (!teams.isEmpty()) {
                analyticsSettingStore.setTeam(teams.get(cbbTeams.getSelectionIndex()));
            }
            analyticsSettingStore.setProject(
                    cbbProjects.getSelectionIndex() != -1 ? projects.get(cbbProjects.getSelectionIndex()) : null);
            analyticsSettingStore.setAutoSubmit(true);

            return true;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
        return false;
    }

    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), initialSize.y + 20);
    }

    @Override
    protected void setInput() {

    }

}
