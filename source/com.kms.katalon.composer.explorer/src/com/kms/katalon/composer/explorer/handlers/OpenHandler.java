package com.kms.katalon.composer.explorer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

@SuppressWarnings("restriction")
public class OpenHandler {
    @Inject
    private Logger logger;
    @Inject
    private ESelectionService selectionService;

    @CanExecute
    public boolean canExecute() {
        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (selectedObjects == null || selectedObjects.length == 0) {
            return false;
        }
        return true;
    }

    @Execute
    public void execute(IEventBroker eventBroker) {
        if (selectionService != null && selectionService.getSelection(IdConstants.EXPLORER_PART_ID) != null) {
            for (Object selectedItem : (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID)) {
                if (selectedItem instanceof ITreeEntity) {
                    try {
                        eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM,
                                ((ITreeEntity) selectedItem).getObject());
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
    }
}
