package com.kms.katalon.composer.report.handlers;

import javax.annotation.PostConstruct;

import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.components.impl.handler.OpenFileEntityHandler;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.parts.ReportCollectionPart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public class OpenReportCollectionHandler extends OpenFileEntityHandler<ReportCollectionEntity> {
    private static final String REPORT_COLLECTION_PART_URI = "bundleclass://com.kms.katalon.composer.report/"
            + ReportCollectionPart.class.getName();
    
    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    @Override
    protected Class<? extends ReportCollectionEntity> getEntityType() {
        return ReportCollectionEntity.class;
    }
    
    @PostConstruct
    protected void initialize() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventServiceAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void handleEvent(Event event) {
                Object object = getObject(event);
                if (object instanceof ReportCollectionEntity) {
                    if (featureService.canUse(KSEFeature.REPORT_HISTORY)) {
                        execute((ReportCollectionEntity) object);
                    } else {
                        KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.REPORT_HISTORY);
                    }
                }
            }
        });
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
