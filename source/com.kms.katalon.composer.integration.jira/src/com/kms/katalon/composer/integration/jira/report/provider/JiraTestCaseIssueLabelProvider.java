package com.kms.katalon.composer.integration.jira.report.provider;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.DefaultCellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.HoveredImageColumnLabelProvider;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.constant.ImageConstants;
import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.integration.jira.report.JiraLinkedIssuesDialog;
import com.kms.katalon.composer.integration.jira.report.JiraReportTestCaseColumn;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.tracking.service.Trackings;

public class JiraTestCaseIssueLabelProvider extends HoveredImageColumnLabelProvider<TestCaseLogRecord>
        implements JiraUIComponent {

    private JiraReportTestCaseColumn view;

    public JiraTestCaseIssueLabelProvider(int columnIndex, JiraReportTestCaseColumn view) {
        super(columnIndex);
        this.view = view;
    }

    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new DefaultCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return 5;
            }
        };
    }

    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        TestCaseLogRecord logRecord = (TestCaseLogRecord) cell.getElement();
        Shell activeShell = e.display.getActiveShell();

        ReportEntity reportEntity = getReportEntity();

        int index = getTestCaseLogRecordIndex(logRecord, reportEntity);
        JiraLinkedIssuesDialog dialog = new JiraLinkedIssuesDialog(activeShell,
                getJiraIssueCollection(index, logRecord, reportEntity), logRecord);
        Trackings.trackOpenLinkedJiraIssuesDialog();
        if (dialog.open() != JiraLinkedIssuesDialog.OK || !dialog.isChanged()) {
            return;
        }

        try {
            updateJiraReport(logRecord, dialog.getJiraIssueCollection(), reportEntity);
        } catch (JiraIntegrationException ex) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
        }
    }

    private ReportEntity getReportEntity() {
        return view.getReportEntity();
    }

    @Override
    protected String getElementToolTipText(TestCaseLogRecord element) {
        return ComposerJiraIntegrationMessageConstant.TOOLTIP_CLICK_TO_MANAGE_JIRA_ISSUES;
    }

    @Override
    protected Class<TestCaseLogRecord> getElementType() {
        return TestCaseLogRecord.class;
    }

    @Override
    protected Image getImage(TestCaseLogRecord logRecord) {
        return getJiraIssueCollection(logRecord, view.getReportEntity()).getIssues().isEmpty()
                ? ImageConstants.IMG_ISSUE_HOVER_OUT : getHoveredImage(logRecord);
    }

    @Override
    protected String getText(TestCaseLogRecord element) {
        return StringUtils.EMPTY;
    }

    @Override
    protected Image getHoveredImage(TestCaseLogRecord element) {
        return ImageConstants.IMG_ISSUE_HOVER_IN;
    }
}
