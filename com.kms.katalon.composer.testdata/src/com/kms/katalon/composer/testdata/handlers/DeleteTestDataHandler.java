package com.kms.katalon.composer.testdata.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class DeleteTestDataHandler {

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
				if (object != null && object instanceof TestDataTreeEntity) {
					execute((TestDataTreeEntity) object);
				}
			}
		});
	}

	private void execute(TestDataTreeEntity testDataTreeEntity) {
		try {
			DataFileEntity testData = (DataFileEntity) testDataTreeEntity.getObject();
			TestDataController.getInstance().deleteDataFile(testData);

			// remove TestCase part from its partStack if it exists
			String partId = EntityPartUtil.getTestDataPartId(testData.getId());
			MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
					application);
			MPart mPart = (MPart) modelService.find(partId, application);
			if (mPart != null) {
				mStackPart.getChildren().remove(mPart);
			}

			eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, TestDataController.getInstance()
					.getIdForDisplay(testData));
			eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, testDataTreeEntity.getParent());
		} catch (EntityIsReferencedException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, e.getMessage());
		} catch (Exception e) {
			LoggerSingleton.logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
					StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA);
		}
	}

}
