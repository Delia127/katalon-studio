package com.kms.katalon.composer.webservice.handlers;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.ImportWebServiceObjectsFromPostmanDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.tracking.service.Trackings;

public class ImportWebServiceRequestObjectFromPostmanHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    @Inject
    EPartService partService;

    @Inject
    IEclipseContext context;

    private FolderTreeEntity objectRepositoryTreeRoot;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.IMPORT_WEB_SERVICE_OBJECTS_FROM_POSTMAN, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (!canExecute()) {
                    return;
                }
                execute(null, Display.getCurrent().getActiveShell());
            }
        });
    }

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object[] selectedObjects,
            @Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        int k = 0;
        try {
            ITreeEntity parentTreeEntity = objectRepositoryTreeRoot;

            FolderEntity parentFolderEntity = (FolderEntity) parentTreeEntity.getObject();
            ObjectRepositoryController toController = ObjectRepositoryController.getInstance();

            ImportWebServiceObjectsFromPostmanDialog dialog = new ImportWebServiceObjectsFromPostmanDialog(parentShell,
                    parentFolderEntity);

            if (dialog.open() == Dialog.OK) {
                List<WebServiceRequestEntity> requestEntities = dialog.getWebServiceRequestEntities();
               
                for (WebServiceRequestEntity entity : requestEntities) {
                    try {
                        EntityNameController.validateName(entity.toString());
                    } catch (Exception e) {
                        entity.setName("New Postman Request"+"("+k+")");
                        k++;
                      }
                        toController.saveNewTestObject(entity);
                    
                }

                trackImportPostman(dialog.getPostmanSpecLocation());

                eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
                
                List<WebElementTreeEntity> requestTreeEntities = new ArrayList<>();
                for (WebServiceRequestEntity request: requestEntities) {
                    requestTreeEntities.add(TreeEntityUtil.getWebElementTreeEntity(request, ProjectController.getInstance().getCurrentProject()));
                }
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEMS,
                        requestTreeEntities.toArray());
            }
        } catch (FilePathTooLongException e) {
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_REQ_OBJ);
        }
    }

    private void trackImportPostman(String postmanSpecLocation) {
        try {
            Paths.get(postmanSpecLocation);
            Trackings.trackImportPostman("file");
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public static ITreeEntity findParentTreeEntity(Object[] selectedObjects) throws Exception {
        if (selectedObjects != null) {
            for (Object entity : selectedObjects) {
                if (entity instanceof ITreeEntity) {
                    Object entityObject = ((ITreeEntity) entity).getObject();
                    if (entityObject instanceof FolderEntity) {
                        FolderEntity folder = (FolderEntity) entityObject;
                        if (folder.getFolderType() == FolderType.WEBELEMENT) {
                            return (ITreeEntity) entity;
                        }
                    } else if (entityObject instanceof WebElementEntity) {
                        return ((ITreeEntity) entity).getParent();
                    }
                }
            }
        }
        return null;
    }

    @Inject
    @Optional
    private void catchTestDataFolderTreeEntitiesRoot(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {
        try {
            for (Object o : treeEntities) {
                Object entityObject = ((ITreeEntity) o).getObject();
                if (entityObject instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) entityObject;
                    if (folder.getFolderType() == FolderType.WEBELEMENT) {
                        objectRepositoryTreeRoot = (FolderTreeEntity) o;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
