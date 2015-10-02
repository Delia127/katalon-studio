package com.kms.katalon.composer.explorer.handlers;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class DeleteHandler implements IHandler {

    @CanExecute
    public static boolean canExecute(ESelectionService selectionService) {
        if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) instanceof Object[]) {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            for (Object entity : selectedObjects) {
                if (entity instanceof ITreeEntity) {
                    try {
                        return ((ITreeEntity) entity).isRemoveable();
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Execute
    public static void execute(ESelectionService selectionService, IEventBroker eventBroker) {
        if (selectionService != null && selectionService.getSelection(IdConstants.EXPLORER_PART_ID) != null) {
            delete(eventBroker, (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID), true);
        }
    }

    public static void delete(IEventBroker eventBroker, Object[] objects, boolean needConfirm) {
        try {
            if (needConfirm) {
                boolean canDelete = false;
                String message = "";
                if (objects.length == 1 && objects[0] instanceof ITreeEntity) {
                    message = MessageFormat.format(StringConstants.HAND_DELETE_CONFIRM_MSG,
                            ((ITreeEntity) objects[0]).getTypeName() + " '" + ((ITreeEntity) objects[0]).getText()
                                    + "'");
                } else if (objects.length > 1) {
                    message = MessageFormat.format(StringConstants.HAND_MULTI_DELETE_CONFIRM_MSG, objects.length);
                }
                canDelete = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                        StringConstants.HAND_DELETE_TITLE, message);
                if (canDelete) {
                    delete(eventBroker, objects);
                }
            } else {
                delete(eventBroker, objects);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private static void delete(final IEventBroker eventBroker, final Object[] objects) {
        Job job = new Job("Delete tree items") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Deleting items...", objects.length + 1);
                    Set<ITreeEntity> parentSetEntities = new HashSet<ITreeEntity>();
                    
                    for (Object selectedItem : objects) {
                        ITreeEntity treeEntity = (ITreeEntity) selectedItem;
                        try {
                            if (parentSetEntities.contains(treeEntity)) {
                                parentSetEntities.remove(treeEntity);
                            }
                            
                            if (treeEntity.getParent() != null) {
                                parentSetEntities.add(treeEntity.getParent());
                            }
                            
                            monitor.subTask("Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText()
                                    + "'...");
                        } catch (Exception e) {
                            continue;
                        }
                        if (selectedItem instanceof ITreeEntity) {
                            eventBroker.send(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, treeEntity);
                        }
                        monitor.worked(1);
                    }
                    
                    monitor.subTask("Refreshing explorer");
                    for (ITreeEntity parentEntity : parentSetEntities) {
                        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentEntity);
                    }
                    monitor.worked(1);
                    
                    return Status.OK_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };

        if (objects.length > 1) {
            job.setUser(true);
        }
        job.schedule(0);
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
    }
}
