package com.kms.katalon.composer.testlistener.handler;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.TestListenerFolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestListenerTreeEntity;
import com.kms.katalon.constants.IdConstants;

public class TestListenerTreeRootCatcher {

    private Object getFirstSelection(ESelectionService selectionService) {
        Object selectedObject = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);

        if (selectedObject == null || !selectedObject.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjectAsArray = (Object[]) selectedObject;
        return selectedObjectAsArray.length == 1 ? selectedObjectAsArray[0] : null;
    }

    protected TestListenerFolderTreeEntity getParentTestListenerTreeFolder(ESelectionService selectionService,
            boolean returnRootIfNull) {
        Object selectedObj = getFirstSelection(selectionService);

        if (selectedObj instanceof TestListenerFolderTreeEntity) {
            return (TestListenerFolderTreeEntity) selectedObj;
        }
        if (selectedObj instanceof TestListenerTreeEntity) {
            try {
                return (TestListenerFolderTreeEntity) ((TestListenerTreeEntity) selectedObj).getParent();
            } catch (Exception ignored) {}
        }
        return null;
    }
}
