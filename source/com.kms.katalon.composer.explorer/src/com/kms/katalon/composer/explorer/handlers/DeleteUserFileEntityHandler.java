package com.kms.katalon.composer.explorer.handlers;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.UserFileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.UserFileController;
import com.kms.katalon.entity.file.UserFileEntity;

public class DeleteUserFileEntityHandler implements IDeleteEntityHandler {

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return UserFileTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        if (!(treeEntity instanceof UserFileTreeEntity)) {
            return false;
        }
        UserFileEntity userFileEntity = null;
        try {
            userFileEntity = (UserFileEntity) treeEntity.getObject();
        } catch (Exception ignored) {}
        
        monitor.subTask(MessageFormat.format("Deleteing file: ", userFileEntity.getIdForDisplay()));
        
        try {
            UserFileController.getInstance().deleteFile(userFileEntity);
            
            if (treeEntity.getParent() != null) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, treeEntity.getParent());
            } else {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_RELOAD_DATA, null);
            }
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

}
