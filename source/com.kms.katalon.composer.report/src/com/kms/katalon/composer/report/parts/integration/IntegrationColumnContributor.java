package com.kms.katalon.composer.report.parts.integration;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.entity.report.ReportEntity;

public interface IntegrationColumnContributor {
    ReportEntity getReportEntity();

    Image getProductImage();
}
