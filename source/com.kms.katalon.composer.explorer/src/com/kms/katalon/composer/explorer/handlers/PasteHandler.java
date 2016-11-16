package com.kms.katalon.composer.explorer.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class PasteHandler extends CommonExplorerHandler {

    @Override
    public boolean canExecute() {
        if (!isExplorerPartActive()) {
            return false;
        }

        ITreeEntity entity = getValidSelection();
        if (entity == null) {
            return false;
        }

        // Get clipboard
        Clipboard clipboard = new Clipboard(Display.getCurrent());

        try {
            // Disable Paste function in Report region
            // Handle Keyword entity from paste
            String copyTag = entity.getCopyTag();
            if (StringUtils.equals(copyTag, FolderType.REPORT.toString())
                    || (StringUtils.equals(copyTag, FolderType.KEYWORD.toString())
                            && clipboard.getContents(FileTransfer.getInstance()) == null)) {
                return false;
            }

            // Handle other entities from paste
            Transfer entityTransfer = entity.getEntityTransfer();
            Object transferingObjects = entityTransfer != null ? clipboard.getContents(entityTransfer) : null;

            if (entityTransfer == null || transferingObjects == null || !transferingObjects.getClass().isArray()
                    || ((Object[]) transferingObjects).length == 0) {
                return false;
            }

            if (!TransferMoveFlag.isMove()) {
                return true;
            }

            // parent tree entity is destination folder tree entity
            if (TreeEntityTransfer.getInstance().equals(entityTransfer)) {
                ITreeEntity parentTreeEntity = ((ITreeEntity) ((Object[]) transferingObjects)[0]).getParent();
                ITreeEntity destinationTreeEntity = (entity instanceof FolderTreeEntity) ? entity : entity.getParent();
                return !parentTreeEntity.equals(destinationTreeEntity);
            }
            return true;
        } catch (Exception e) {
            logError(e);
            return false;
        }
    }

    @Override
    public void execute() {
        ITreeEntity entity = getValidSelection();
        if (entity == null) {
            return;
        }

        eventBroker.send(EventConstants.EXPLORER_PASTE_SELECTED_ITEM, entity);
    }

    private ITreeEntity getValidSelection() {
        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length != 1) {
            return null;
        }

        if (!(selectedObjects[0] instanceof ITreeEntity)) {
            return null;
        }

        return (ITreeEntity) selectedObjects[0];
    }
}
