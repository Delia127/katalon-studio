package com.kms.katalon.composer.integration.jira.report;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.integration.jira.constant.ImageConstants;
import com.kms.katalon.composer.integration.jira.report.provider.JiraTestLogIssueLabelProvider;
import com.kms.katalon.composer.report.parts.integration.TestLogIntegrationColumn;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public class JiraReportTestLogColumn extends TestLogIntegrationColumn {

    public JiraReportTestLogColumn(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord) {
        super(reportEntity, testSuiteLogRecord);
    }

    @Override
    public Image getProductImage() {
        return ImageConstants.IMG_16_JIRA;
    }

    @Override
    public ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex) {
        TreeViewerColumn tableViewerColumnIntegration = new TreeViewerColumn((TreeViewer) tableViewer, SWT.NONE);
        TreeColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();
        tableViewerColumnIntegration.setLabelProvider(new JiraTestLogIssueLabelProvider(columnIndex, this));
        tblclmnTCIntegration.setImage(getProductImage());
        return tableViewerColumnIntegration;
    }

}
