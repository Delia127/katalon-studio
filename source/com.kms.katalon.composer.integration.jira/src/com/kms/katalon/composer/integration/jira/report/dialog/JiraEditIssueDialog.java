package com.kms.katalon.composer.integration.jira.report.dialog;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.issue.DefaultIssueHTMLLinkProvider;

public class JiraEditIssueDialog extends JiraIssueBrowserDialog {

	private final DefaultIssueHTMLLinkProvider htmlLinkProvider;

	public JiraEditIssueDialog(Shell parentShell, TestCaseLogRecord logRecord, DefaultIssueHTMLLinkProvider htmlLinkProvider)
			throws URISyntaxException, IOException {
		super(parentShell, logRecord, htmlLinkProvider);
		this.htmlLinkProvider = htmlLinkProvider;
	}

	@Override
    protected void trigger() {
        StringBuilder updateFieldsJS = new StringBuilder();
        try {
            if (getSettingStore().isUseTestCaseNameAsSummaryEnabled()) {
                updateFieldsJS.append(updateField(JiraIssue.FIELD_SUMMARY,
                        htmlLinkProvider.getIssueMetaData().getSummary()));
            }

            updateFieldsJS.append(updateField(JiraIssue.FIELD_DESCRIPTION,
                    htmlLinkProvider.getIssueMetaData().getDescription()));

            updateFieldsJS.append(updateField(JiraIssue.FIELD_ENVIRONMENT,
                    htmlLinkProvider.getIssueMetaData().getEnvironment()));
            browser.execute(waitAndExec(JiraIssue.FIELD_DESCRIPTION, updateFieldsJS.toString()));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
	}
}
