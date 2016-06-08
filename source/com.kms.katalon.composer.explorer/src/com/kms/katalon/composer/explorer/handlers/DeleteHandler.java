package com.kms.katalon.composer.explorer.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static java.text.MessageFormat.format;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.explorer.handlers.deletion.DeleteEntityHandlerFactory;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.constants.EventConstants;

public class DeleteHandler extends CommonExplorerHandler {

    @Override
    public boolean canExecute() {
        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length == 0) {
            return false;
        }
        try {
            for (Object entity : selectedObjects) {
                if (!(entity instanceof ITreeEntity)) {
                    continue;
                }
                if (!((ITreeEntity) entity).isRemoveable()) {
                    return false;
                }
            }
        } catch (Exception e) {
            logError(e);
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        if (isExplorerPartNotActive()) {
            return;
        }

        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length == 0) {
            return;
        }

        delete(selectedObjects, true);
    }

    private void delete(Object[] objects, boolean needConfirm) {
        try {
            if (!needConfirm) {
                delete(objects);
                return;
            }

            String message = format(StringConstants.HAND_MULTI_DELETE_CONFIRM_MSG, objects.length);
            if (objects.length == 1 && objects[0] instanceof ITreeEntity) {
                message = format(StringConstants.HAND_DELETE_CONFIRM_MSG, ((ITreeEntity) objects[0]).getTypeName()
                        + " '" + ((ITreeEntity) objects[0]).getText() + "'");
            }
            if (openQuestion(Display.getCurrent().getActiveShell(), StringConstants.HAND_DELETE_TITLE, message)) {
                delete(objects);
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    private void delete(final Object[] objects) throws InvocationTargetException, InterruptedException {
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        dialog.run(true, true, new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask("Deleting items...", objects.length + 1);
                    Set<ITreeEntity> parentSetEntities = new HashSet<ITreeEntity>();
                    boolean showYesNoToAllOptions = objects.length > 1;
                    for (Object selectedItem : objects) {
                        if (monitor.isCanceled()) {
                            return;
                        }

                        if (!(selectedItem instanceof ITreeEntity)) {
                            continue;
                        }

                        final ITreeEntity treeEntity = (ITreeEntity) selectedItem;
                        SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1,
                                SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
                        try {
                            if (parentSetEntities.contains(treeEntity)) {
                                parentSetEntities.remove(treeEntity);
                            }

                            if (treeEntity.getParent() != null) {
                                parentSetEntities.add(treeEntity.getParent());
                            }
                        } catch (Exception e) {
                            subMonitor.done();
                            continue;
                        }

                        IDeleteEntityHandler handler = DeleteEntityHandlerFactory.getInstance().getDeleteHandler(
                                treeEntity.getClass());
                        if (handler == null) {
                            continue;
                        }

                        if (handler instanceof AbstractDeleteReferredEntityHandler) {
                            ((AbstractDeleteReferredEntityHandler) handler).setNeedYesNoToAllButtons(showYesNoToAllOptions);
                        }

                        try {
                            handler.execute(treeEntity, subMonitor);
                        } catch (Exception e) {
                            logError(e);
                        }
                    }

                    monitor.subTask("Refreshing explorer");
                    for (ITreeEntity parentEntity : parentSetEntities) {
                        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentEntity);
                    }
                    monitor.worked(1);
                } finally {
                    monitor.done();
                    eventBroker.post(EventConstants.EXPLORER_RESET_USER_RESPONSE_FOR_DELETION, StringConstants.EMPTY);
                }
            }
        });
    }

}
