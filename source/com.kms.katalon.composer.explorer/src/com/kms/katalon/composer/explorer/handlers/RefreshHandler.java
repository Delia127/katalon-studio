package com.kms.katalon.composer.explorer.handlers;

import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;

public class RefreshHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        if (getSelection() == null) {
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        Object[] selectedObjects = getSelection();
        if (selectedObjects == null) {
            return;
        }

        for (Object selectedItem : selectedObjects) {
            if (!(selectedItem instanceof ITreeEntity)) {
                continue;
            }
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, selectedItem);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, selectedItem);
        }
    }
}
