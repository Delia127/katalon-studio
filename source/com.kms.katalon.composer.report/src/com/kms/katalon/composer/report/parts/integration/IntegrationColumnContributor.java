package com.kms.katalon.composer.report.parts.integration;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.entity.report.ReportEntity;

public interface IntegrationColumnContributor {
    ReportEntity getReportEntity();

    Image getProductImage();

    ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex);
}
