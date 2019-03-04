package com.kms.katalon.composer.integration.analytics.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.composer.integration.analytics.constants.ComposerIntegrationAnalyticsMessageConstants;
import com.kms.katalon.composer.integration.analytics.handlers.AnalyticsAuthorizationHandler;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;

public class NewProjectDialog extends AbstractDialog {
    
    private Text txtProject;
    
    private String serverUrl;
    
    private String email;
    
    private String password;
    
    private Job creatingJob;
    
    private AnalyticsProject analyticsProject;
    
    private AnalyticsTeam team;
    
    public NewProjectDialog(Shell parentShell, String serverUrl, String email, String password, AnalyticsTeam team) {
        super(parentShell);
        this.serverUrl = serverUrl;
        this.email = email;
        this.password = password;
        this.team = team;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ComposerIntegrationAnalyticsMessageConstants.BTN_CREATE, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        createNewProject();
    }

    @Override
    protected void registerControlModifyListeners() {
        // do nothing
    }

    @Override
    protected void setInput() {
        txtProject.setText(StringUtils.EMPTY);
    }
    
    @Override
    protected void cancelPressed() {
        if (creatingJob != null && creatingJob.getState() == Job.RUNNING) {
            creatingJob.cancel();
        }
        super.cancelPressed();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
    	Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(3, false);
        glContainer.horizontalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        container.setLayout(glContainer);
        
        Label lblServerUrl = new Label(container, SWT.NONE);
        lblServerUrl.setText(ComposerIntegrationAnalyticsMessageConstants.LBL_NEW_PROJECT);

        txtProject = new Text(container, SWT.BORDER | SWT.FLAT);
        GridData txtProjectGridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        txtProjectGridData.widthHint = 300;
        txtProject.setLayoutData(txtProjectGridData);

        return container;
    }
    
    private boolean createNewProject() {
        getButton(OK).setEnabled(false);
        final String newProjectName = txtProject.getText();
        creatingJob = new Job(ComposerIntegrationAnalyticsMessageConstants.MSG_DLG_PRG_CREATING_PROJECTS) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    final AnalyticsTokenInfo tokenInfo = AnalyticsAuthorizationHandler.requestToken(serverUrl, email, password);
                    analyticsProject = AnalyticsAuthorizationHandler.createProject(serverUrl, newProjectName, team, tokenInfo.getAccess_token());
                    return Status.OK_STATUS;
                } catch (final Exception e) {
                    if (!monitor.isCanceled() && !isDisposed()) {
                        UISynchronizeService.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MultiStatusErrorDialog.showErrorDialog(e, ComposerAnalyticsStringConstants.ERROR, e.getLocalizedMessage());
                            }
                        });
                    }
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };

        creatingJob.setUser(false);
        creatingJob.schedule();
        creatingJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (isDisposed()) {
                    return;
                }
                
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        getButton(OK).setEnabled(true);
                    }                    
                });
                
                if (!event.getResult().isOK()) {
                    return;
                }                

                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        setReturnCode(OK);
                        close();
                    }
                });
            }
        });
        return true;
    }

    public String getDialogTitle() {
        return ComposerIntegrationAnalyticsMessageConstants.DIA_TITLE_CREATE_NEW_PROJECT;
    }
    
    public AnalyticsProject getAnalyticsProject() {
        return analyticsProject;
    }

    private boolean isDisposed() {
        Shell shell = getShell();
        return shell == null || shell.isDisposed();
    }
    
}
