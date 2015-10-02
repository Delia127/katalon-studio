package com.kms.katalon.composer.report.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.report.ReportEntity;

public class DeleteReportHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @PostConstruct
    private void registerEventHandler() {

        eventBroker.subscribe(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof ReportTreeEntity) {
                    excute((ReportTreeEntity) object);
                }
            }
        });
    }

    private void excute(ReportTreeEntity reportTreeEntity) {
        try {
            ReportEntity report = (ReportEntity) reportTreeEntity.getObject();
            if (report == null) {
                return;
            }
            
            EntityPartUtil.closePart(report);
            
            ReportController.getInstance().deleteReport(report);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_REPORT,
                    e.getMessage());
        }
    }
}
