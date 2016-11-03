package com.kms.katalon.composer.report.parts.integration;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.entity.report.ReportEntity;

public abstract class TestCaseLogColumnIntegrationView {

    protected ReportEntity reportEntity;

    public TestCaseLogColumnIntegrationView(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }

    public abstract TableViewerColumn createTableIntegrationColumn(TableViewer tableViewer, int columnIndex);

    public abstract Image getProductImage();
}
