package com.kms.katalon.composer.explorer.handlers.deletion;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static java.text.MessageFormat.format;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public abstract class AbstractDeleteReferredEntityHandler implements IDeleteEntityHandler {

    @Inject
    protected IEventBroker eventBroker;

    @Inject
    protected UISynchronize sync;

    private int response;

    private int defaultResponse;

    private boolean needYesNoToAllButtons;

    protected boolean isDeleted;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RESET_USER_RESPONSE_FOR_DELETION, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                resetResponse();
            }
        });
    }

    public boolean needYesNoToAllButtons() {
        return needYesNoToAllButtons;
    }

    public void setNeedYesNoToAllButtons(boolean needYesNoToAllButtons) {
        this.needYesNoToAllButtons = needYesNoToAllButtons;
    }

    protected boolean isCancelResponse() {
        return response == IDialogConstants.CANCEL_ID;
    }

    protected boolean isYesResponse() {
        return response == IDialogConstants.YES_ID || response == IDialogConstants.YES_TO_ALL_ID;
    }

    protected boolean isYesNoToAllResponse() {
        return response == IDialogConstants.YES_TO_ALL_ID || response == IDialogConstants.NO_TO_ALL_ID;
    }
    protected boolean isDefaultResponse() {
        return response == defaultResponse;
    }

    protected void resetResponse() {
        setResponse(defaultResponse);
    }

    protected void setResponse(int response) {
        this.response = response;
    }

    /**
     * Delete folder
     * 
     * @param folderEntity
     * @param undeletedEntities
     * @param monitor
     */
    protected void deleteFolder(FolderEntity folderEntity, List<IEntity> undeletedEntities, IProgressMonitor monitor) {
        try {
            for (IEntity entity : undeletedEntities) {
                if (folderEntity.equals(entity.getParentFolder())) {
                    undeletedEntities.add(folderEntity);
                    return;
                }
            }

            monitor.subTask(format(StringConstants.HAND_JOB_DELETING_FOLDER, folderEntity.getIdForDisplay()));
            FolderController.getInstance().deleteFolder(folderEntity);
        } catch (Exception e) {
            logError(e);
        } finally {
            monitor.worked(1);
        }
    }
}
