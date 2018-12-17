package com.kms.katalon.composer.components.impl.handler;

import javax.inject.Inject;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;

public abstract class AbstractHandler extends WorkbenchUtilizer implements IHandler2 {

    @Inject
    protected IEventBroker eventBroker;

    @Inject
    protected ESelectionService selectionService;


    private ExecutionEvent executionEvent;

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
    public Object execute(ExecutionEvent executionEvent) throws ExecutionException {
        setExecutionEvent(executionEvent);
        execute();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return canExecute();
    }

    @Override
    public void setEnabled(Object evaluationContext) {
        // do nothing
    }

    @Override
    public boolean isHandled() {
        return true;
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
        // do nothing
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
        // do nothing
    }

    @Override
    public void dispose() {
        // do nothing
    }

    protected ExecutionEvent getExecutionEvent() {
        return executionEvent;
    }

    private void setExecutionEvent(ExecutionEvent executionEvent) {
        this.executionEvent = executionEvent;
    }

    protected EPartService getPartService() {
        return PartServiceSingleton.getInstance().getPartService();
    }
    
}
