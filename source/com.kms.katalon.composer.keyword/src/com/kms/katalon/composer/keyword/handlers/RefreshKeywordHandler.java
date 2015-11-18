package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

public class RefreshKeywordHandler {

    @Inject
    IEventBroker eventBroker;
    

    @Inject
    EModelService modelService;
    
    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                try {
                    Object selectedObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                    if (selectedObject != null && selectedObject instanceof KeywordTreeEntity) {
                        KeywordTreeEntity keywordTreeEntity = (KeywordTreeEntity) selectedObject;
                        IFile keywordFile = (IFile) ((ICompilationUnit) keywordTreeEntity.getObject()).getResource();
                        keywordFile.refreshLocal(IResource.DEPTH_ZERO, null);
                        if (keywordFile != null && keywordFile.exists()) {
                            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordTreeEntity);
                            KeywordController.getInstance().parseCustomKeywordFile(keywordFile,
                                    ProjectController.getInstance().getCurrentProject());
                        } else {
                            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordTreeEntity.getParent());
                        }
                   
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    MultiStatusErrorDialog.showErrorDialog(e, "Unable to refresh this keyword", e.getMessage());
                }
            }
        });
    }
}
