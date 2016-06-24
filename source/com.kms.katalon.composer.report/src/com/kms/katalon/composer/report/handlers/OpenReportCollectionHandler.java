package com.kms.katalon.composer.report.handlers;

import com.kms.katalon.composer.components.impl.handler.OpenFileEntityHandler;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.parts.ReportCollectionPart;
import com.kms.katalon.entity.report.ReportCollectionEntity;

public class OpenReportCollectionHandler extends OpenFileEntityHandler<ReportCollectionEntity> {
    private static final String REPORT_COLLECTION_PART_URI = "bundleclass://com.kms.katalon.composer.report/"
            + ReportCollectionPart.class.getName();

    @Override
    protected Class<? extends ReportCollectionEntity> getEntityType() {
        return ReportCollectionEntity.class;
    }

    @Override
    public String getContributionURI() {
        return REPORT_COLLECTION_PART_URI;
    }

    @Override
    public String getIconURI() {
        return ImageConstants.URL_16_REPORT_COLLECTION;
    }

    @Override
    public String getPartId(ReportCollectionEntity reportCollection) {
        return EntityPartUtil.getReportCollectionPartId(reportCollection.getId());
    }
}
