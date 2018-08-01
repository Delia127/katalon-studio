package com.kms.katalon.composer.integration.cucumber.handler;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class FeatureTreeRootCatcher {
    protected Object getFirstSelection(ESelectionService selectionService) {
        Object selectedObject = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);

        if (selectedObject == null || !selectedObject.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjectAsArray = (Object[]) selectedObject;
        return selectedObjectAsArray.length == 1 ? selectedObjectAsArray[0] : null;
    }

    protected FolderTreeEntity getParentFeatureTreeFolder(ESelectionService selectionService,
            boolean returnRootIfNull) {
        Object selectedObj = getFirstSelection(selectionService);

        if (!(selectedObj instanceof FolderTreeEntity)) {
            return null;
        }
        try {
            FolderEntity folder = ((FolderTreeEntity) selectedObj).getObject();
            if (folder.getFolderType() == FolderType.INCLUDE && 
                    !FolderController.getInstance().isSourceFolder(ProjectController.getInstance().getCurrentProject(), folder)) {
                return (FolderTreeEntity) selectedObj;
            }
            return null;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }
}
