package com.kms.katalon.composer.windows.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.WindowsElementController;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class DeleteWindowsObjectHandler implements IDeleteEntityHandler {

    @Inject
    private IEventBroker eventBroker;

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (!(treeEntity instanceof WindowsElementTreeEntity)) {
                return false;
            }

            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText() + "'...";
            monitor.beginTask(taskName, 1);

            WindowsElementEntity windowsElementEntity = (WindowsElementEntity) treeEntity.getObject();

            UISynchronizeService.syncExec(() -> EntityPartUtil.closePart(windowsElementEntity));

            WindowsElementController.getInstance().deleteWindowsElementEntity(windowsElementEntity);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, windowsElementEntity.getIdForDisplay());
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return WindowsElementTreeEntity.class;
    }
}
