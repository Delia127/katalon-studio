package com.kms.katalon.composer.explorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.constants.IdConstants;

public class CutHandler implements IHandler {

    @CanExecute
    public static boolean canExecute(ESelectionService selectionService) {
        return CopyHandler.canExecute(selectionService);
    }

    @Execute
    public static void execute(ESelectionService selectionService) {
        CopyHandler.execute(selectionService);
        TransferMoveFlag.setMove(true);
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
