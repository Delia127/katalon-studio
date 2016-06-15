package com.kms.katalon.composer.testsuite.collection.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class DeleteTestSuiteCollectionHandler implements IDeleteEntityHandler {

    @Inject
    private IEventBroker eventBroker;

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (!(treeEntity instanceof TestSuiteCollectionTreeEntity)) {
                return false;
            }

            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText() + "'...";
            monitor.beginTask(taskName, 1);

            TestSuiteCollectionEntity testRun = (TestSuiteCollectionEntity) treeEntity.getObject();

            EntityPartUtil.closePart(testRun);

            TestSuiteCollectionController.getInstance().deleteTestSuiteCollection(testRun);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testRun.getIdForDisplay());
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
        return TestSuiteCollectionTreeEntity.class;
    }
}
