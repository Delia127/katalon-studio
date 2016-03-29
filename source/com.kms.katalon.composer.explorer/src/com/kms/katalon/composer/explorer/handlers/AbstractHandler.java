package com.kms.katalon.composer.explorer.handlers;

import static org.eclipse.ui.handlers.HandlerUtil.getActivePartId;

import javax.inject.Inject;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.constants.IdConstants;

public abstract class AbstractHandler implements IHandler {

    @Inject
    protected IEventBroker eventBroker;

    @Inject
    protected ESelectionService selectionService;

    public AbstractHandler() {
        if (eventBroker == null) {
            eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        }

        if (selectionService == null) {
            selectionService = SelectionServiceSingleton.getInstance().getSelectionService();
        }
    }

    @CanExecute
    public abstract boolean canExecute();

    @Execute
    public abstract void execute();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (IdConstants.EXPLORER_PART_ID.equals(getActivePartId(event))) {
            execute();
        }
        return null;
    }

    /**
     * Get explorer selected Tree Entities with NULL and empty check
     * 
     * @return Object[] selected Tree Entities
     */
    protected Object[] getSelection() {
        Object o = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (o == null || !o.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjects = (Object[]) o;
        if (selectedObjects.length == 0) {
            return null;
        }

        return selectedObjects;
    }

    @Override
    public boolean isEnabled() {
        return canExecute();
    }

    @Override
    public boolean isHandled() {
        return true;
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void dispose() {
    }

}
