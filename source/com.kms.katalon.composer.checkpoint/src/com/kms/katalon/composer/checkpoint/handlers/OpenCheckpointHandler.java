package com.kms.katalon.composer.checkpoint.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.checkpoint.parts.CheckpointCsvPart;
import com.kms.katalon.composer.checkpoint.parts.CheckpointDatabasePart;
import com.kms.katalon.composer.checkpoint.parts.CheckpointExcelPart;
import com.kms.katalon.composer.checkpoint.parts.CheckpointTestDataPart;
import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;

public class OpenCheckpointHandler {

    private static final String CHECKPOINT_BUNDLE_URI = "bundleclass://com.kms.katalon.composer.checkpoint/";

    private static final String CHECKPOINT_TEST_DATA_PART_URI = CHECKPOINT_BUNDLE_URI
            + CheckpointTestDataPart.class.getName();

    private static final String CHECKPOINT_EXCEL_PART_URI = CHECKPOINT_BUNDLE_URI + CheckpointExcelPart.class.getName();

    private static final String CHECKPOINT_CSV_PART_URI = CHECKPOINT_BUNDLE_URI + CheckpointCsvPart.class.getName();

    private static final String CHECKPOINT_DATABASE_PART_URI = CHECKPOINT_BUNDLE_URI
            + CheckpointDatabasePart.class.getName();

    @Inject
    MApplication application;

    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    @PostConstruct
    public void openCheckpointTreeEntity(IEventBroker eventBroker) {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (!(object instanceof CheckpointEntity)) {
                    return;
                }
                openCheckpointEntity((CheckpointEntity) object);
            }
        });
    }

    @Inject
    @Optional
    public void openCheckpointEntity(@UIEventTopic(EventConstants.CHECKPOINT_OPEN) CheckpointEntity checkpoint) {
        try {
            if (checkpoint == null) {
                return;
            }

            String partId = EntityPartUtil.getCheckpointPartId(checkpoint.getId());
            MPartStack partStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                    application);
            MPart part = (MPart) modelService.find(partId, application);

            if (part == null) {
                part = modelService.createModelElement(MPart.class);
                part.setElementId(partId);
                part.setLabel(checkpoint.getName());
                part.setIconURI(ImageConstants.URL_16_CHECKPOINT);
                setContributionURI(part, checkpoint.getSourceInfo());
                part.setCloseable(true);
                part.setTooltip(checkpoint.getIdForDisplay());
                partStack.getChildren().add(part);
            }

            if (part.getObject() == null) {
                part.setObject(checkpoint);
            }

            partStack.setSelectedElement(part);
            partService.showPart(part, PartState.ACTIVATE);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void setContributionURI(MPart part, CheckpointSourceInfo sourceInfo) {
        if (sourceInfo == null) {
            return;
        }

        if (sourceInfo.isFromTestData()) {
            part.setContributionURI(CHECKPOINT_TEST_DATA_PART_URI);
            return;
        }

        switch (sourceInfo.getSourceType()) {
            case ExcelFile:
                part.setContributionURI(CHECKPOINT_EXCEL_PART_URI);
                break;
            case CSV:
                part.setContributionURI(CHECKPOINT_CSV_PART_URI);
                break;
            case DBData:
                part.setContributionURI(CHECKPOINT_DATABASE_PART_URI);
                break;
            default:
                break;
        }
    }

}
