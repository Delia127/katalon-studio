package com.kms.katalon.composer.integration.jira.report;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.integration.jira.constant.ImageConstants;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogColumnIntegrationView;
import com.kms.katalon.entity.report.ReportEntity;

public class JiraReportTestCaseColumnView extends TestCaseLogColumnIntegrationView {

    public JiraReportTestCaseColumnView(ReportEntity reportEntity) {
        super(reportEntity);
    }

    @Override
    public TableViewerColumn createTableIntegrationColumn(TableViewer tableViewer, int columnIndex) {
        TableViewerColumn tableViewerColumnIntegration = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();
        tableViewerColumnIntegration.setLabelProvider(new JiraTestCaseIssueLabelProvider(columnIndex, this));
        tblclmnTCIntegration.setImage(getProductImage());
        return tableViewerColumnIntegration;
    }


    @Override
    public Image getProductImage() {
        return ImageConstants.IMG_JIRA;
    }

    public ReportEntity getReportEntity() {
        return reportEntity;
    }
}
