package com.kms.katalon.composer.integration.cucumber.handler;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
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
    
    protected FolderTreeEntity getSelectedTreeEntity(Object[] selectedObjects ) {
        if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof ITreeEntity)) {
            return null;
        }

        if (selectedObjects[0] instanceof FolderTreeEntity) {
            FolderTreeEntity parentFolder = (FolderTreeEntity) selectedObjects[0];
            return isIncludeAndNotSystemFolder(parentFolder) ? parentFolder : null; 
        } else {
            ITreeEntity treeEntity = (ITreeEntity) selectedObjects[0];
            try {
                ITreeEntity parent = treeEntity.getParent();
                if (!(parent instanceof FolderTreeEntity)) {
                    return null;
                }
                FolderTreeEntity parentFolder = (FolderTreeEntity) parent;
                return isIncludeAndNotSystemFolder(parentFolder) ? parentFolder : null; 
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return null;
            }
        }
    }

    /**
     * KAT-3622
     */
    private boolean isIncludeAndNotSystemFolder(FolderTreeEntity folderTree) {
        try {
            FolderEntity folder = folderTree.getObject();
            return folder.getFolderType() == FolderType.INCLUDE
                    && !FolderController.getInstance().isSystemFolder(
                            ProjectController.getInstance().getCurrentProject(), folder);
        } catch (Exception e) {
           return false;
        }
    }
}
