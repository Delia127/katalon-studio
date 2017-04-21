package com.kms.katalon.composer.testdata.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class RefreshTestDataHandler {
	@Inject
	IEventBroker eventBroker;

	@PostConstruct
	public void registerEventHandler(IEventBroker eventBroker) {
		eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new EventHandler() {

			@Override
			public void handleEvent(Event event) {
                if (ProjectController.getInstance().getCurrentProject() == null) {
                    return;
                }
				Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				if (object != null && object instanceof TestDataTreeEntity) {
					try {
						excute((TestDataTreeEntity) object);
					} catch (Exception e) {
						LoggerSingleton.logError(e);
					}
				}
			}
		});
	}

	private void excute(TestDataTreeEntity testDataTreeEntity) throws Exception {
		if (testDataTreeEntity.getObject() == null) {
			ITreeEntity parentEntity = testDataTreeEntity.getParent();
			if (parentEntity != null && parentEntity instanceof FolderTreeEntity) {
				eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, parentEntity);
			} else {
                FolderEntity folder = FolderController.getInstance()
                        .getTestDataRoot(ProjectController.getInstance().getCurrentProject());
				eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new FolderTreeEntity(folder, null));
			}
		} else {
			DataFileEntity dataFile = testDataTreeEntity.getObject();
			FolderController.getInstance().refreshFolder(dataFile.getParentFolder());
		}
	}
}
