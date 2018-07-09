package com.kms.katalon.composer.integration.jira.report.dialog.progress;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.integration.jira.JiraIntegrationException;

public class NewIssueProgressDialog extends JiraIssueProgressDialog {
    public NewIssueProgressDialog(Shell parent, String issueKey, TestCaseLogRecord logRecord) {
        super(parent, issueKey, logRecord);
    }

    public JiraIssueProgressResult run() {
        final JiraIssueProgressResult result = new JiraIssueProgressResult();
        try {
            run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(MessageFormat.format(
                                ComposerJiraIntegrationMessageConstant.JOB_TASK_UPDATE_JIRA_ISSUE, issueKey), 2);
                        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();
                        retrieveJiraIssue(handler, result);
                        checkCanceled(monitor);
                        monitor.worked(1);

                        uploadTestCaseLog(logRecord, result.getJiraIssue());
                        checkCanceled(monitor);
                        monitor.worked(1);
                        
                        linkWithTestCaseJiraIssue(logRecord, result.getJiraIssue());
                        checkCanceled(monitor);
                        monitor.worked(1);
                        result.setComplete(true);
                    } catch (JiraIntegrationException e) {
                        result.setError(e);
                    } catch (IOException e) {
                        result.setError(new JiraIntegrationException(e));
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException ignored) {}
        return result;
    }
}
