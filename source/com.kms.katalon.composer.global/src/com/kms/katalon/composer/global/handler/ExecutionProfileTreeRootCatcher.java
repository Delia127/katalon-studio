package com.kms.katalon.composer.global.handler;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.ProfileRootTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.constants.IdConstants;

public class ExecutionProfileTreeRootCatcher {

    private Object getFirstSelection(ESelectionService selectionService) {
        Object selectedObject = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);

        if (selectedObject == null || !selectedObject.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjectAsArray = (Object[]) selectedObject;
        return selectedObjectAsArray.length == 1 ? selectedObjectAsArray[0] : null;
    }

    protected ProfileRootTreeEntity getProfileTreeFolder(ESelectionService selectionService) {
        Object selectedObj = getFirstSelection(selectionService);

        if (selectedObj instanceof ProfileRootTreeEntity) {
            return (ProfileRootTreeEntity) selectedObj;
        }
        if (selectedObj instanceof ProfileTreeEntity) {
            try {
                return (ProfileRootTreeEntity) ((ProfileTreeEntity) selectedObj).getParent();
            } catch (Exception ignored) {}
        }
        return null;
    }
}
