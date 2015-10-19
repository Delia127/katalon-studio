package com.kms.katalon.composer.objectrepository.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class RefreshTestObjectHandler {
    @Inject
    IEventBroker eventBroker;
    
    @PostConstruct
    public void registerEventHandler(IEventBroker eventBroker) {
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof WebElementTreeEntity) {
                    try {
                        excute((WebElementTreeEntity) object);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                }
            }
        });
    }

    private void excute(WebElementTreeEntity testObjectTreeEntity) throws Exception {
        if (testObjectTreeEntity.getObject() == null) {
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            ITreeEntity parentEntity = testObjectTreeEntity.getParent();
            if (parentEntity != null && parentEntity instanceof FolderTreeEntity) {
                eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, parentEntity);
            } else {
                FolderEntity folder = FolderController.getInstance().getObjectRepositoryRoot(project);
                eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new FolderTreeEntity(folder, null));
            }
        } else {
        	WebElementEntity webElement = (WebElementEntity) testObjectTreeEntity.getObject();
        	FolderController.getInstance().refreshFolder(webElement.getParentFolder());
        }
    }
}
