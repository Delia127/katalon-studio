package com.kms.katalon.composer.integration.jira.toolbar.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class JiraImportedIssueLabelProvider extends TypeCheckedStyleCellLabelProvider<JiraIssue> {

    public JiraImportedIssueLabelProvider(int columnIndex) {
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
            case IssueSelectionDialog.CLMN_SUMMARY_IDX: {
                return StringUtils.defaultString(issue.getFields().getSummary());
            }
            case IssueSelectionDialog.CLMN_STATUS_IDX: {
                return issue.getFields().getStatus().getName();
            }
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected String getElementToolTipText(JiraIssue element) {
        return columnIndex == IssueSelectionDialog.CLMN_SUMMARY_IDX ? getText(element) : null;
    }
}
