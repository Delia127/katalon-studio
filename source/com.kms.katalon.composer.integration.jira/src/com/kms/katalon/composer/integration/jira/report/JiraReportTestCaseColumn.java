package com.kms.katalon.composer.integration.jira.report;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.integration.jira.constant.ImageConstants;
import com.kms.katalon.composer.integration.jira.report.provider.JiraTestCaseIssueLabelProvider;
import com.kms.katalon.composer.report.parts.integration.TestCaseIntegrationColumn;
import com.kms.katalon.entity.report.ReportEntity;

public class JiraReportTestCaseColumn extends TestCaseIntegrationColumn {

    public JiraReportTestCaseColumn(ReportEntity reportEntity) {
        super(reportEntity);
    }

    @Override
    public ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex) {
        TableViewerColumn tableViewerColumnIntegration = new TableViewerColumn((TableViewer) tableViewer, SWT.NONE);
        TableColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();
        tableViewerColumnIntegration.setLabelProvider(new JiraTestCaseIssueLabelProvider(columnIndex, this));
        tblclmnTCIntegration.setImage(getProductImage());
        return tableViewerColumnIntegration;
    }

    @Override
    public Image getProductImage() {
        return ImageConstants.IMG_16_JIRA;
    }
}
