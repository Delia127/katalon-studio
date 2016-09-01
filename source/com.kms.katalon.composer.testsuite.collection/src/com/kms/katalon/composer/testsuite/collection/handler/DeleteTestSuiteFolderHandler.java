package com.kms.katalon.composer.testsuite.collection.handler;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.handlers.DeleteTestSuiteHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.file.IFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteTestSuiteFolderHandler extends DeleteTestSuiteHandler implements IDeleteFolderHandler {

    @Override
    public FolderType getFolderType() {
        return FolderType.TESTSUITE;
    }

    @Override
    public boolean execute(FolderTreeEntity folderTreeEntity, IProgressMonitor monitor) {
        try {
            if (folderTreeEntity == null || folderTreeEntity.getObject() == null) {
                return false;
            }

            FolderEntity folderEntity = (FolderEntity) folderTreeEntity.getObject();
            String folderId = folderEntity.getIdForDisplay();
            List<Object> descendantEntities = FolderController.getInstance().getAllDescentdantEntities(folderEntity);

            monitor.beginTask(format(StringConstants.HAND_JOB_DELETING_FOLDER, folderId), descendantEntities.size() + 1);

            List<IEntity> undeleteTestSuites = new ArrayList<>();

            for (Object entity : descendantEntities) {
                if (monitor.isCanceled()) {
                    return false;
                }

                if (entity instanceof TestSuiteEntity && !deleteTestSuiteTask((TestSuiteEntity) entity, monitor)) {
                    undeleteTestSuites.add((TestSuiteEntity) entity);
                    continue;
                }

                if (entity instanceof TestSuiteCollectionEntity) {
                    deleteTestSuiteCollectionTask((TestSuiteCollectionEntity) entity, monitor);
                }

                if (entity instanceof FolderEntity) {
                    deleteFolder((FolderEntity) entity, undeleteTestSuites, monitor);
                }
            }

            deleteFolder(folderEntity, undeleteTestSuites, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            return true;
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

    private boolean deleteTestSuiteTask(final TestSuiteEntity testSuite, IProgressMonitor monitor) {
        return deleteEntityTask(testSuite, monitor, new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                performDeleteTestSuite(testSuite);
                return null;
            }
        });
    }

    private boolean deleteTestSuiteCollectionTask(final TestSuiteCollectionEntity testSuiteCollection,
            IProgressMonitor monitor) {
        return deleteEntityTask(testSuiteCollection, monitor, new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                EntityPartUtil.closePart(testSuiteCollection);

                TestSuiteCollectionController.getInstance().deleteTestSuiteCollection(testSuiteCollection);
                return null;
            }
        });
    }

    private boolean deleteEntityTask(IFileEntity fileEntity, IProgressMonitor monitor, Callable<Object> callable) {
        String testSuiteId = fileEntity.getIdForDisplay();
        monitor.subTask(format(StringConstants.HAND_JOB_DELETING_ENTITY, testSuiteId));
        try {
            callable.call();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            monitor.worked(1);
        }
    }

}
