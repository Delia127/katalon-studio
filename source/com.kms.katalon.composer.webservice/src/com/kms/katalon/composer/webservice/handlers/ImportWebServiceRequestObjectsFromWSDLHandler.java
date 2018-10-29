package com.kms.katalon.composer.webservice.handlers;

import java.nio.file.Paths;
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
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.ImportWebServiceObjectsFromWSDLDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.tracking.service.Trackings;

public class ImportWebServiceRequestObjectsFromWSDLHandler {

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
        eventBroker.subscribe(EventConstants.IMPORT_WEB_SERVICE_OBJECTS_FROM_WSDL, new EventHandler() {

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
        try {
            ITreeEntity parentTreeEntity = findParentTreeEntity(selectedObjects);
            if (parentTreeEntity == null) {
                if (objectRepositoryTreeRoot == null) {
                    return;
                }
                parentTreeEntity = objectRepositoryTreeRoot;
            }

            FolderEntity parentFolderEntity = (FolderEntity) parentTreeEntity.getObject();
            ObjectRepositoryController toController = ObjectRepositoryController.getInstance();

            ImportWebServiceObjectsFromWSDLDialog dialog = new ImportWebServiceObjectsFromWSDLDialog(parentShell);

            String[] requestMethods = new String[] { WebServiceRequestEntity.SOAP, WebServiceRequestEntity.SOAP12 };
            if (dialog.open() == Dialog.OK) {
                for (int i = 0; i < requestMethods.length; i++) {
                    String requestMethod = requestMethods[i];

                    List<WebServiceRequestEntity> soapRequestEntities = dialog
                            .getWebServiceRequestEntities(requestMethod);
                    if (soapRequestEntities != null && soapRequestEntities.size() > 0) {
                        FolderEntity folder = FolderController.getInstance().addNewFolder(parentFolderEntity,
                                requestMethod);
                        FolderTreeEntity newFolderTree = new FolderTreeEntity(folder, parentTreeEntity);
                        for (WebServiceRequestEntity entity : soapRequestEntities) {
                            entity.setElementGuidId(Util.generateGuid());
                            entity.setParentFolder(folder);
                            entity.setProject(folder.getProject());
                            toController.saveNewTestObject(entity);
                        }
                    }
                }

                trackImportWSDL(dialog.getWSDLSpecLocation());

                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, parentTreeEntity);
            }

        } catch (FilePathTooLongException e) {
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void trackImportWSDL(String wsdlSpecLocation) {
        try {
            Paths.get(wsdlSpecLocation);
            Trackings.trackImportWSDL("file");
        } catch (Throwable t) {
            Trackings.trackImportWSDL("url");
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
