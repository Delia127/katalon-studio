package com.kms.katalon.composer.explorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.IdConstants;

public class CopyHandler implements IHandler {

    @CanExecute
    public static boolean canExecute(ESelectionService selectionService) {

        // All selections should be on the same level, and may not be the Root nodes
        try {
            if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) instanceof Object[]) {
                Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
                if (selectedObjects == null || selectedObjects.length == 0) {
                    return false;
                }
                String entityTypeName = null;
                for (Object entity : selectedObjects) {
                    if (entity instanceof ITreeEntity) {
                        ITreeEntity treeEntity = (ITreeEntity) entity;

                        // Not allow to copy Root Folders
                        if (!(treeEntity.isRemoveable())) {
                            return false;
                        }

                        // Not allow keyword package from copy
                        if (treeEntity instanceof PackageTreeEntity) {
                            return false;
                        }

                        // Not allow to copy multiple types of tree entity
                        if (entityTypeName == null) {
                            entityTypeName = treeEntity.getCopyTag();
                        } else {
                            if (treeEntity.getCopyTag() == null
                                    || !(treeEntity.getCopyTag().equalsIgnoreCase(entityTypeName))) {
                                return false;
                            }
                        }
                    } else {

                        // Not allow to copy entity that is not tree entity
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
        return false;
    }

    @Execute
    public static void execute(ESelectionService selectionService) {
        if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) != null) {
            try {
                Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
                final Clipboard cb = new Clipboard(Display.getCurrent());

                if (selectedObjects != null && selectedObjects[0] instanceof KeywordTreeEntity) {
                    // Handle Keyword from copy
                    // Groovy Keyword class is Non-Serialize. Thus, cannot load it into memory as byte array.
                    String[] datas = new String[selectedObjects.length];
                    for (int i = 0; i < selectedObjects.length; i++) {
                        ((ITreeEntity) selectedObjects[i]).loadAllDescentdantEntities();
                        datas[i] = ((ICompilationUnit) ((KeywordTreeEntity) selectedObjects[i]).getObject()).getPath()
                                .toString();
                    }
                    cb.setContents(new Object[] { datas }, new Transfer[] { FileTransfer.getInstance() });
                } else {
                    // Handle other entities from copy
                    Object[] datas = new ITreeEntity[selectedObjects.length];
                    for (int i = 0; i < selectedObjects.length; i++) {
                        ((ITreeEntity) selectedObjects[i]).loadAllDescentdantEntities();
                        datas[i] = selectedObjects[i];
                    }
                    cb.setContents(new Object[] { datas },
                            new Transfer[] { ((ITreeEntity) datas[0]).getEntityTransfer() });
                }
                TransferMoveFlag.setMove(false);
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
        }
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String activePartId = HandlerUtil.getActivePartId(event);
        if (activePartId != null && activePartId.equals(IdConstants.EXPLORER_PART_ID)) {
            execute(SelectionServiceSingleton.getInstance().getSelectionService());
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return canExecute(SelectionServiceSingleton.getInstance().getSelectionService());
    }

    @Override
    public boolean isHandled() {
        return true;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
    }

}
