package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jdt.core.IPackageFragment;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

public class RefreshPackageHandler {

    @Inject
    IEventBroker eventBroker;
    

    @Inject
    EModelService modelService;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new EventHandler() {
            @SuppressWarnings("restriction")
            @Override
            public void handleEvent(Event event) {
                try {
                    Object selectedObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                    if (selectedObject != null && selectedObject instanceof PackageTreeEntity) {
                        PackageTreeEntity packageTreeEntity = (PackageTreeEntity) selectedObject;
                        IPackageFragment packageFragment = (IPackageFragment) packageTreeEntity.getObject();
                        if (packageFragment != null && packageFragment.exists()) {
                            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, packageTreeEntity);
                            KeywordController.getInstance().parseCustomKeywordInPackage(packageFragment, 
                                    ProjectController.getInstance().getCurrentProject());
                        } else {
                            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, packageTreeEntity.getParent());
                        }
                    }
                } catch (Exception e) {
                    LoggerSingleton.getInstance().getLogger().error(e);
                }
            }
        });
    }
}
