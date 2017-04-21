package com.kms.katalon.composer.checkpoint.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class RefreshCheckpointHandler {
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
                if (!(object instanceof CheckpointTreeEntity)) {
                    return;
                }

                try {
                    excute((CheckpointTreeEntity) object);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
    }

    private void excute(CheckpointTreeEntity checkpointTreeEntity) throws Exception {
        CheckpointEntity checkpoint = checkpointTreeEntity.getObject();
        if (checkpoint != null) {
            FolderController.getInstance().refreshFolder(checkpoint.getParentFolder());
            return;
        }

        ITreeEntity parentEntity = checkpointTreeEntity.getParent();
        if (parentEntity instanceof FolderTreeEntity) {
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, parentEntity);
            return;
        }

        FolderEntity checkpointRootFolder = FolderController.getInstance()
                .getCheckpointRoot(ProjectController.getInstance().getCurrentProject());
        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM,
                new FolderTreeEntity(checkpointRootFolder, null));
    }
}
