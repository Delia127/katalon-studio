package com.kms.katalon.composer.objectrepository.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.objectrepository.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.repository.WebElementEntity;

public class DeleteTestObjectHandler implements IDeleteEntityHandler {

    @Inject
    private IEventBroker eventBroker;

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return WebElementTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof WebElementTreeEntity)) {
                return false;
            }
            
            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText()
                    + "'...";
            monitor.beginTask(taskName, 1);
            
            WebElementEntity webElement = (WebElementEntity) treeEntity.getObject();

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(webElement);

            ObjectRepositoryController.getInstance().deleteWebElement(webElement);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, ObjectRepositoryController.getInstance()
                    .getIdForDisplay(webElement));
            return true;
        } catch (EntityIsReferencedException e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, e.getMessage());
            return false;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ);
            return false;
        } finally {
            monitor.done();
        }
    }

}
