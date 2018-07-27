package com.kms.katalon.composer.integration.cucumber.handler;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.FeatureFolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FeatureTreeEntity;
import com.kms.katalon.constants.IdConstants;

public class FeatureTreeRootCatcher {
    private Object getFirstSelection(ESelectionService selectionService) {
        Object selectedObject = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);

        if (selectedObject == null || !selectedObject.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjectAsArray = (Object[]) selectedObject;
        return selectedObjectAsArray.length == 1 ? selectedObjectAsArray[0] : null;
    }

    protected FeatureFolderTreeEntity getParentFeatureTreeFolder(ESelectionService selectionService,
            boolean returnRootIfNull) {
        Object selectedObj = getFirstSelection(selectionService);

        if (selectedObj instanceof FeatureFolderTreeEntity) {
            return (FeatureFolderTreeEntity) selectedObj;
        }
        if (selectedObj instanceof FeatureTreeEntity) {
            try {
                return (FeatureFolderTreeEntity) ((FeatureTreeEntity) selectedObj).getParent();
            } catch (Exception ignored) {}
        }
        return null;
    }
}
