package com.kms.katalon.composer.objectrepository.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.dialog.ObjectSpyDialog;

public class SpyObjectHandler {

    @Inject
    private IEventBroker eventBroker;

    private ObjectSpyDialog objectSpyDialog;

    @PostConstruct
    public void registerAddToObjectSpyEvent() {
        eventBroker.subscribe(EventConstants.OBJECT_SPY_TEST_OBJECT_ADDED, new EventServiceAdapter() {
            
            @Override
            public void handleEvent(Event event) {
                openDialogAndAddObject(Display.getCurrent().getActiveShell(), getObjects(event)); 
            }
        });
        eventBroker.subscribe(EventConstants.OBJECT_SPY_WEB, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                if (!canExecute()) {
                    return;
                }
                execute(Display.getCurrent().getActiveShell());
            }
        });
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        openDialogAndAddObject(activeShell, null);
    }

    private void openDialogAndAddObject(Shell activeShell, Object[] selectedObjects) {
        try {
            if (objectSpyDialog == null || objectSpyDialog.isDisposed()) {
                objectSpyDialog = new ObjectSpyDialog(activeShell, LoggerSingleton.getInstance().getLogger(), eventBroker);
                objectSpyDialog.setBlockOnOpen(false);
            }
            objectSpyDialog.open();
            if (selectedObjects != null) {
                objectSpyDialog.addObjectsFromObjectRepository(selectedObjects);
            }
        } catch (Exception e) {
            if (objectSpyDialog != null) {
                objectSpyDialog.stop();
                objectSpyDialog.close();
            }
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    public static ITreeEntity getParentTreeEntity(Object[] selectedObjects) throws Exception {
        for (Object object : selectedObjects) {
            if (object instanceof ITreeEntity) {
                if (((ITreeEntity) object).getObject() instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
                    if (folder.getFolderType() == FolderType.WEBELEMENT) {
                        return (ITreeEntity) object;
                    }
                } else if (((ITreeEntity) object).getObject() instanceof WebElementEntity) {
                    return (ITreeEntity) ((ITreeEntity) object).getParent();
                }
            }
        }
        return null;
    }
}
