package com.kms.katalon.composer.explorer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;

@SuppressWarnings("restriction")
public class OpenHandler extends CommonExplorerHandler {
    @Inject
    private Logger logger;

    @CanExecute
    public boolean canExecute() {
        if (!isExplorerPartActive()) {
            return false;
        }
        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length == 0) {
            return false;
        }
        for (Object item : selectedObjects) {
            if (item instanceof FolderTreeEntity || item instanceof PackageTreeEntity) {
                return false;
            }
        }
        return true;
    }

    @Execute
    public void execute() {
        try {
            for (ITreeEntity selectedItem : getElementSelection(ITreeEntity.class)) {
                eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, selectedItem.getObject());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
