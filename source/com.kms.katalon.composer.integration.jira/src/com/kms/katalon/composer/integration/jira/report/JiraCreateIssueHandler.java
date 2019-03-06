package com.kms.katalon.composer.integration.jira.report;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.report.dialog.CreateAsSubTaskBrowserDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.JiraEditIssueDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.JiraIssueBrowserDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.JiraNewIssueDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.LinkJiraIssueDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.JiraIssueProgressResult;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.LinkJiraIssueProgressDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.NewIssueProgressDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.UpdateJiraIssueProgressDialog;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.issue.EditIssueHTMLLinkProvider;
import com.kms.katalon.integration.jira.issue.NewIssueHTMLLinkProvider;
import com.kms.katalon.integration.jira.issue.NewSubtaskHTMLLinkProvider;

public class JiraCreateIssueHandler implements JiraUIComponent {
    private Shell shell;

    private TestCaseLogRecord logRecord;

    public JiraCreateIssueHandler(Shell shell, TestCaseLogRecord logRecord) {
        this.shell = shell;
        this.logRecord = logRecord;
    }

    public JiraIssueProgressResult openLinkIssueDialog() {
        LinkJiraIssueDialog linkDialog = new LinkJiraIssueDialog(shell,
                ComposerJiraIntegrationMessageConstant.DIA_TITLE_LINK_TO_EXISTING_ISSUE,
                ComposerJiraIntegrationMessageConstant.DIA_MESSAGE_LINK_TO_EXISTING_ISSUE,
                ComposerJiraIntegrationMessageConstant.DIA_LBL_LINK_TO_EXISTING_ISSUE);
        if (linkDialog.open() != Dialog.OK) {
            return null;
        }
        return new LinkJiraIssueProgressDialog(shell, linkDialog.getIssueKey(), logRecord).run();
    }

    public JiraIssueProgressResult openNewIssueDialog(int numSteps) {
        try {
            return openNewIssueBrowserDialog(new JiraNewIssueDialog(shell, logRecord,
                    new NewIssueHTMLLinkProvider(logRecord, numSteps, getSettingStore())));
        } catch (URISyntaxException | IOException ex) {
            LoggerSingleton.logError(ex);
            return null;
        }
    }

    private JiraIssueProgressResult openNewIssueBrowserDialog(JiraIssueBrowserDialog browserDialog) {
        if (browserDialog.open() != Dialog.OK) {
            return null;
        }
        String issueKey = browserDialog.getIssueKey();
        return new NewIssueProgressDialog(shell, issueKey, logRecord).run();
    }

    public JiraIssueProgressResult openCreateAsSubTaskDialog(int numSteps) {
        LinkJiraIssueDialog linkDialog = new LinkJiraIssueDialog(shell,
                ComposerJiraIntegrationMessageConstant.DIA_TITLE_CREATE_NEW_AS_SUB_TASK,
                ComposerJiraIntegrationMessageConstant.DIA_MESSAGE_CREATE_NEW_AS_SUB_TASK,
                ComposerJiraIntegrationMessageConstant.DIA_LBL_CREATE_NEW_AS_SUB_TASK);
        if (linkDialog.open() != Dialog.OK) {
            return null;
        }
        JiraIssueProgressResult result = new UpdateJiraIssueProgressDialog(shell, linkDialog.getIssueKey(), logRecord)
                .run();
        if (!checkResult(result)) {
            return null;
        }

        try {
            JiraIssue parentIssue = result.getJiraIssue();
            CreateAsSubTaskBrowserDialog browserDialog = new CreateAsSubTaskBrowserDialog(shell, logRecord,
                    new NewSubtaskHTMLLinkProvider(logRecord, getSettingStore(), parentIssue));
            if (browserDialog.open() != Dialog.OK) {
                return null;
            }
            String issueKey = browserDialog.getIssueKey();
            if (parentIssue.getKey().equals(issueKey)) {
                return null;
            }
            return new NewIssueProgressDialog(shell, issueKey, logRecord).run();

        } catch (URISyntaxException | IOException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    public boolean checkResult(JiraIssueProgressResult result) {
        if (result == null) {
            return false;
        }
        if (result.hasError()) {
            JiraIntegrationException error = result.getError();
            MultiStatusErrorDialog.showErrorDialog(error, error.getMessage(), error.getMessage());
            LoggerSingleton.logError(error);
            return false;
        }
        return result.isComplete();
    }

    public JiraIssueProgressResult openEditIssueDialog(JiraIssue jiraIssue) {
        try {
            JiraEditIssueDialog browserDialog = new JiraEditIssueDialog(shell, logRecord,
                    new EditIssueHTMLLinkProvider(logRecord, getSettingStore(), jiraIssue));

            if (browserDialog.open() != Dialog.OK) {
                return null;
            }
            return new UpdateJiraIssueProgressDialog(shell, browserDialog.getIssueKey(), logRecord).run();
        } catch (ClassCastException | URISyntaxException | IOException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

}
