package com.kms.katalon.composer.webservice.handlers;

import java.util.List;

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
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.NewRequestDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class NewWebServiceRequestObjectHandler {

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

	@CanExecute
	private boolean canExecute() throws Exception {
		if (ProjectController.getInstance().getCurrentProject() != null) {
			return true;
		} else {
			return false;
		}
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object[] selectedObjects,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
		try {
			
			if (selectedObjects != null) {
                ITreeEntity parentTreeEntity = findParentTreeEntity(selectedObjects);
                if (parentTreeEntity == null) {
                    parentTreeEntity = objectRepositoryTreeRoot;
                }
                if (parentTreeEntity != null) {
                    FolderEntity parentFolderEntity = (FolderEntity) parentTreeEntity.getObject();
                    String suggestedName = ObjectRepositoryController.getInstance().getAvailableWebElementName(parentFolderEntity, 
                    		StringConstants.HAND_NEW_REQUEST);                    		
                    NewRequestDialog dialog = new NewRequestDialog(parentShell, parentFolderEntity);
                    dialog.setName(suggestedName);
                    dialog.open();

                    if (dialog.getReturnCode() == Dialog.OK) {
                    	WebServiceRequestEntity request = new WebServiceRequestEntity();
                    	request.setName(dialog.getName());
                    	request.setServiceType(dialog.getWebServiveType());
                    	WebServiceRequestEntity requestEntity = ObjectRepositoryController.getInstance().addNewRequest(parentFolderEntity, request);
                        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
                        eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, new WebElementTreeEntity(requestEntity, parentTreeEntity));
                        eventBroker.post(EventConstants.WEBSERVICE_REQUEST_OBJECT_OPEN, requestEntity);
                    }

                }
            }
		} catch (Exception e) {
			LoggerSingleton.logError(e);
			MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, 
					StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_REQ_OBJ);
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
            for(Object o : treeEntities) {
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
