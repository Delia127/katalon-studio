package com.kms.katalon.composer.explorer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

@SuppressWarnings("restriction")
public class RenameHandler extends CommonExplorerHandler {

    @Inject
    private Logger logger;

    @Inject
    private ESelectionService selectionService;

    @Inject
    EPartService partService;

    @CanExecute
    public boolean canExecute() {
        if (!isExplorerPartActive()) {
            return false;
        }

        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (selectedObjects == null || selectedObjects.length != 1) {
            return false;
        }
        if (selectedObjects[0] instanceof ITreeEntity) {
            try {
                return ((ITreeEntity) selectedObjects[0]).isRenamable();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return true;
    }

    @Execute
    public void execute() {
        if (selectionService != null) {
            if (partService.getDirtyParts().size() > 0) {
                if (!MessageDialog.openConfirm(null, StringConstants.HAND_CONFIRM_TITLE,
                        StringConstants.HAND_CONFIRM_MSG_REQUIRE_SAVE_ALL_B4_CONTINUE)) {
                    return;
                }
            }

            if (partService.saveAll(true) && selectionService.getSelection(IdConstants.EXPLORER_PART_ID) != null) {
                Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
                if (selectedObjects.length > 0 && selectedObjects[0] instanceof ITreeEntity) {
                    try {
                        eventBroker
                                .post(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, (ITreeEntity) selectedObjects[0]);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
    }
}
