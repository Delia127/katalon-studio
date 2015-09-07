package com.kms.katalon.composer.report.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.report.ReportEntity;

public class DeleteReportHandler {

	@Inject
	IEventBroker eventBroker;

	@Inject
	MApplication application;

	@Inject
	EModelService modelService;

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
			ReportController.getInstance().deleteReport(report);

			String partId = EntityPartUtil.getReportPartId(report.getId());
			MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
					application);
			MPart mPart = (MPart) modelService.find(partId, application);
			if (mPart != null) {
				mStackPart.getChildren().remove(mPart);
			}
			eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, reportTreeEntity.getParent());
		} catch (Exception e) {
			LoggerSingleton.logError(e);
			MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_REPORT,
					e.getMessage());
		}
	}
}
