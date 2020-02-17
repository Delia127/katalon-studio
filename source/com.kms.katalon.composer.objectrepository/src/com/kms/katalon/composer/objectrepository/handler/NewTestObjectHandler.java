package com.kms.katalon.composer.objectrepository.handler;

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
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.dialog.NewTestObjectDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewTestObjectHandler {
	@Inject
	IEventBroker eventBroker;

	@Inject
	EModelService modelService;

	@Inject
	MApplication application;

	@Inject
	EPartService partService;
	
    @Inject
    private ESelectionService selectionService;

	@Inject
	IEclipseContext context;

	private FolderTreeEntity objectRepositoryTreeRoot;
    
    private String newDefaultName = StringConstants.HAND_NEW_TEST_OBJ;

    @Inject
    @Optional
    private void executeByEvent(@UIEventTopic(EventConstants.TEST_OBJECT_NEW) Object unuse) {
        if (!canExecute()) {
            return;
        }
        execute(Display.getCurrent().getActiveShell());
    }

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (selectedObjects == null) {
                return;
            }

            ITreeEntity parentTreeEntity = getParentTreeEntity(selectedObjects);
            if (parentTreeEntity == null) {
                if (objectRepositoryTreeRoot == null) {
                    return;
                }
                parentTreeEntity = objectRepositoryTreeRoot;
            }

            FolderEntity parentFolderEntity = (FolderEntity) parentTreeEntity.getObject();
            ObjectRepositoryController toController = ObjectRepositoryController.getInstance();
            String suggestedName = toController.getAvailableWebElementName(parentFolderEntity, newDefaultName);

            NewTestObjectDialog dialog = new NewTestObjectDialog(parentShell, parentFolderEntity, suggestedName);
            if (dialog.open() != Dialog.OK) {
                return;
            }

            // save test object
            WebElementEntity webElement = toController.saveNewTestObject(dialog.getEntity());

            if (webElement == null) {
                MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                        StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_OBJ);
                return;
            }
            
            Trackings.trackCreatingObject("testObject");

            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, new WebElementTreeEntity(webElement,
                    parentTreeEntity));
            eventBroker.post(EventConstants.TEST_OBJECT_OPEN, webElement);
        } catch (FilePathTooLongException e) {
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {            
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_OBJ);
        }
    }

	public static ITreeEntity getParentTreeEntity(Object[] selectedObjects) throws Exception {
		for (Object object : selectedObjects) {
			if (object instanceof ITreeEntity) {
				if (((ITreeEntity) object).getObject() instanceof FolderEntity) {
					FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
					if (folder.getFolderType() == FolderType.WEBELEMENT) {
						return (ITreeEntity) object;
					}
				} else if (((ITreeEntity) object).getObject() instanceof WebElementEntity) {
					return (ITreeEntity) ((ITreeEntity) object).getParent();
				} else if (((ITreeEntity) object).getObject() instanceof WindowsElementEntity) {
					return (ITreeEntity) ((ITreeEntity) object).getParent();
				}
			}
		}
		return null;
	}

	@Inject
	@Optional
	private void catchObjectTreeEntitiesRoot(
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
