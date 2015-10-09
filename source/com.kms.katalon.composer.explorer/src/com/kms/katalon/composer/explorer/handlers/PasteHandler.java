package com.kms.katalon.composer.explorer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class PasteHandler implements IHandler {

    @SuppressWarnings("restriction")
    @CanExecute
    public static boolean canExecute(ESelectionService selectionService) {
        try {
            if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) instanceof Object[]) {
                Object[] selectedNodes = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
                if (selectedNodes == null || selectedNodes.length != 1) {
                    return false;
                }
                Clipboard clipboard = new Clipboard(Display.getCurrent());
                for (Object node : selectedNodes) {
                    ITreeEntity entity = (ITreeEntity) node;

                    if (entity.getEntityTransfer() == null || clipboard.getContents(entity.getEntityTransfer()) == null) {
                        return false;
                    }

                }
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
        return false;
    }

    @SuppressWarnings("restriction")
    @Execute
    public static void execute(ESelectionService selectionService, IEventBroker eventBroker) {
        try {
            if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) instanceof Object[]) {
                Object[] selectedNodes = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
                if (selectedNodes != null && selectedNodes.length == 1) {
                    ITreeEntity target = (ITreeEntity) selectedNodes[0];
                    eventBroker.send(EventConstants.EXPLORER_PASTE_SELECTED_ITEM, target);
                }
            }
        } catch (Exception ex) {
            LoggerSingleton.getInstance().getLogger().error(ex);
        }
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String activePartId = HandlerUtil.getActivePartId(event);
        if (activePartId != null && activePartId.equals(IdConstants.EXPLORER_PART_ID)) {
            execute(SelectionServiceSingleton.getInstance().getSelectionService(), EventBrokerSingleton.getInstance()
                    .getEventBroker());
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
        // TODO Auto-generated method stub

    }
}
