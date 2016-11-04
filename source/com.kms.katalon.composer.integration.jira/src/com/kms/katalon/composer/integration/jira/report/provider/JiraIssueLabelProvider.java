package com.kms.katalon.composer.integration.jira.report.provider;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.integration.jira.report.JiraLinkedIssuesDialog;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class JiraIssueLabelProvider extends TypeCheckedStyleCellLabelProvider<JiraIssue> {

    public JiraIssueLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    protected Class<JiraIssue> getElementType() {
        return JiraIssue.class;
    }

    @Override
    protected Image getImage(JiraIssue element) {
        return null;
    }

    @Override
    protected String getText(JiraIssue issue) {
        switch (columnIndex) {
            case JiraLinkedIssuesDialog.CLMN_SUMMARY_IDEX: {
                return StringUtils.defaultString(issue.getFields().getSummary());
            }
            case JiraLinkedIssuesDialog.CLMN_STATUS_IDEX: {
                return issue.getFields().getStatus().getName();
            }
        }
        return StringUtils.EMPTY;
    }
}
