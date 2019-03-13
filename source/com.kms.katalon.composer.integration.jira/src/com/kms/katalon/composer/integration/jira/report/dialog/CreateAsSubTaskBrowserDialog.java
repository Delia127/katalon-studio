package com.kms.katalon.composer.integration.jira.report.dialog;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.issue.IssueMetaDataProvider;
import com.kms.katalon.integration.jira.issue.NewSubtaskHTMLLinkProvider;

public class CreateAsSubTaskBrowserDialog extends JiraIssueBrowserDialog {

    private IssueMetaDataProvider issueMetaData;
    public CreateAsSubTaskBrowserDialog(Shell parentShell, TestCaseLogRecord logRecord,
            NewSubtaskHTMLLinkProvider htmlLinkProvider) throws URISyntaxException, IOException {
        super(parentShell, logRecord, htmlLinkProvider);
        this.issueMetaData = htmlLinkProvider.getIssueMetaData();
    }

    @Override
    protected void trigger() {
        try {
            StringBuilder updateFieldsJS = new StringBuilder();
            if (getSettingStore().isUseTestCaseNameAsSummaryEnabled()) {
                updateFieldsJS.append(updateField(JiraIssue.FIELD_SUMMARY, issueMetaData.getSummary()));
            }
            updateFieldsJS.append(updateField(JiraIssue.FIELD_DESCRIPTION, issueMetaData.getDescription()));
            browser.execute(waitAndExec(JiraIssue.FIELD_DESCRIPTION, updateFieldsJS.toString()));
        } catch (IOException e) {
           LoggerSingleton.logError(e);
        }
    }

    protected String updateField(String id, String value) {
        return "document.getElementById(\"" + id + "\").value = \"" + StringEscapeUtils.escapeEcmaScript(value) + "\";\n";
    }
}
