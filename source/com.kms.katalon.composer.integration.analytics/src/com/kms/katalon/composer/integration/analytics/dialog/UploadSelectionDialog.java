package com.kms.katalon.composer.integration.analytics.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
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
import com.kms.katalon.composer.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class UploadSelectionDialog extends Dialog {
    public static final int UPLOAD_ID = 2;
    public static final int CANCEL_ID = 3;

    private Button btnUpload;
    private Button btnCancel;
    private Combo cbbProjects;
    private Combo cbbTeams;

    private List<AnalyticsTeam> teams;
    private List<AnalyticsProject> projects;
    private AnalyticsSettingStore analyticsSettingStore;

    public UploadSelectionDialog(Shell parentShell, List<AnalyticsTeam> teams, List<AnalyticsProject> projects) {
        super(parentShell);
        analyticsSettingStore = new AnalyticsSettingStore(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        this.teams = teams;
        this.projects = projects;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData bodyGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        bodyGridData.widthHint = 400;
        body.setLayoutData(bodyGridData);
        GridLayout bodyGridLayout = new GridLayout(1, false);
        bodyGridLayout.marginWidth = 10;
        bodyGridLayout.marginHeight = 10;
        body.setLayout(bodyGridLayout);

        CLabel lblInformation = new CLabel(body, SWT.WRAP);
        lblInformation.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
        lblInformation.setBottomMargin(2);
        lblInformation.setText(StringConstants.LBL_SELECTION_INFORMATION);

        Label lblTeam = new Label(body, SWT.NONE);
        lblTeam.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_TEAM);

        cbbTeams = new Combo(body, SWT.READ_ONLY);
        cbbTeams.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Label lblProject = new Label(body, SWT.NONE);
        lblProject.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_PROJECT);

        cbbProjects = new Combo(body, SWT.READ_ONLY);
        cbbProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        fillData();

        return body;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        btnUpload = createButton(parent, UPLOAD_ID, StringConstants.BTN_UPLOAD, true);
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

        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }
        });
    }

    private void fillData() {
        try {
            cbbTeams.setItems(getTeamNames(teams).toArray(new String[teams.size()]));
            cbbTeams.select(getDefaultTeamIndex());
            cbbProjects.setItems(getProjectNames(projects).toArray(new String[projects.size()]));
            cbbProjects.select(getDefaultProjectIndex());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getMessage());
        }
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
            analyticsSettingStore.setProject(
                    cbbProjects.getSelectionIndex() != -1 ? projects.get(cbbProjects.getSelectionIndex()) : null);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        setReturnCode(UPLOAD_ID);
        closeDialog();
    }

}
