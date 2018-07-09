package com.kms.katalon.composer.integration.jira.toolbar.dialog;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.preference.JiraPreferenceInitializer;
import com.kms.katalon.integration.jira.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.entity.JiraFilter;

public class ImportJiraJQLDialog extends AbstractDialog implements JiraUIComponent {
    private Text text;

    private JiraFilter filter;

    public ImportJiraJQLDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
        text.setText(JiraPreferenceInitializer.getLastEditedJQL(getCurrentProject()));
        text.setFocus();
        text.selectAll();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout containerLayout = new GridLayout(2, false);
        containerLayout.horizontalSpacing = 15;
        container.setLayout(containerLayout);

        Label lblJiraJql = new Label(container, SWT.NONE);
        lblJiraJql.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        lblJiraJql.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_JIRA_JQL);

        text = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        return container;
    }

    @Override
    public String getDialogTitle() {
        return ComposerJiraIntegrationMessageConstant.DIA_TITLE_IMPORT_FROM_JQL;
    }

    @Override
    protected void okPressed() {
        Job job = new Job(ComposerJiraIntegrationMessageConstant.DIA_JOB_FETCH_ISSUES) {
            private String jql = StringUtils.EMPTY;

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                UISynchronizeService.syncExec(() -> {
                    jql = text.getText();
                    ImportJiraJQLDialog.this.getButton(OK).setEnabled(false);
                });
                try {
                    filter = new JiraIntegrationAuthenticationHandler().getJiraFilterByJql(getCredential(), jql);
                    UISynchronizeService.syncExec(() -> {
                        updateJQLForNextUsage(jql);
                        ImportJiraJQLDialog.super.okPressed();
                    });

                    return Status.OK_STATUS;
                } catch (JiraIntegrationException | IOException e) {
                    UISynchronizeService.syncExec(() -> {
                        MessageDialog.openWarning(getShell(), StringConstants.WARN, e.getMessage());
                        ImportJiraJQLDialog.this.getButton(OK).setEnabled(true);
                    });
                    LoggerSingleton.logError(e);
                    return Status.CANCEL_STATUS;
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private void updateJQLForNextUsage(String jql) {
        try {
            JiraPreferenceInitializer.saveLastEditedJQL(jql, getCurrentProject());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    public JiraFilter getFilter() {
        return filter;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 250);
    }
    
    @Override
    protected boolean hasDocumentation() {
        return true;
    }
    
    @Override
    protected String getDocumentationUrl() {
        return ComposerJiraIntegrationMessageConstant.DIA_DOCUMENT_URL_IMPORT_TEST_CASE_FROM_JIRA;
    }
}
