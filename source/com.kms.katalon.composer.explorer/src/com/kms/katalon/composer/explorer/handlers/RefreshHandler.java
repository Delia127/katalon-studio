package com.kms.katalon.composer.explorer.handlers;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;

public class RefreshHandler extends CommonExplorerHandler {

    @Override
    public boolean canExecute() {
        return isExplorerPartActive();
    }

    @Override
    public void execute() {
        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length == 0) {
            eventBroker.post(EventConstants.EXPLORER_RELOAD_DATA, null);
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
