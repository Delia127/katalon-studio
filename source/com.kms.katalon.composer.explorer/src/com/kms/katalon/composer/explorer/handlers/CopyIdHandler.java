package com.kms.katalon.composer.explorer.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.file.FileEntity;

public class CopyIdHandler extends CommonExplorerHandler {

    @CanExecute
    public boolean canExecute() {
        if (!isExplorerPartActive()) {
            return false;
        }

        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof ITreeEntity)) {
            return false;
        }
        ITreeEntity selectedEntity = (ITreeEntity) selectedObjects[0];
        try {
            return selectedEntity.getObject() instanceof FileEntity;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    @Execute
    public void execute() {
        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        ITreeEntity selectedEntity = (ITreeEntity) selectedObjects[0];
        try {
            FileEntity entity = (FileEntity) selectedEntity.getObject();
            Clipboard cb = new Clipboard(Display.getCurrent());
            cb.setContents(new String[] { entity.getIdForDisplay() }, new Transfer[] { TextTransfer.getInstance() });
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
