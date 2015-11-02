package com.kms.katalon.composer.testcase.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class DeleteTestCaseFolderHandler extends DeleteTestCaseHandler implements IDeleteFolderHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private UISynchronize sync;

    @Override
    public FolderType getFolderType() {
        return FolderType.TESTCASE;
    }

    @Override
    public boolean execute(FolderTreeEntity folderTreeEntity, IProgressMonitor monitor) {
        try {
            FolderEntity folder = (FolderEntity) folderTreeEntity.getObject();
            if (folder == null) {
                return false;
            }
            
            String folderId = FolderController.getInstance().getIdForDisplay(folder);
            List<Object> descendant = FolderController.getInstance().getAllDescentdantEntities(folder);
            monitor.beginTask("Deleting folder '" + folder.getName() + "'...", descendant.size() + 1);

            List<IEntity> undeletedTestCases = new ArrayList<IEntity>();

            for (Object descendantObject : descendant) {

                if (monitor.isCanceled()) {
                    return false;
                }

                if (descendantObject instanceof TestCaseEntity) {
                    if (!deleteTestCase((TestCaseEntity) descendantObject, monitor)) {
                        undeletedTestCases.add((TestCaseEntity) descendantObject);
                    }
                } else if (descendantObject instanceof FolderEntity) {
                    FolderEntity descendantFolder = (FolderEntity) descendantObject;
                    deleteFolder(descendantFolder, undeletedTestCases, monitor);
                }
                monitor.worked(1);
            }

            deleteFolder(folder, undeletedTestCases, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, folderId);
            return true;

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, "Unable to delete folder.");
            return false;
        } finally {
            monitor.done();
        }
    }

    private void deleteFolder(FolderEntity folder, List<IEntity> undeletedTestCases, IProgressMonitor monitor) {
        try {
            String folderId = FolderController.getInstance().getIdForDisplay(folder);
            boolean canDelete = true;
            for (IEntity entity : undeletedTestCases) {
                if (folder.equals(entity.getParentFolder())) {
                    canDelete = false;
                    break;
                }
            }
            if (canDelete) {
                monitor.subTask("Deleting folder '" + folderId + "'...");
                FolderController.getInstance().deleteFolder(folder);
            } else {
                undeletedTestCases.add(folder);
            }

        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        } finally {
            monitor.worked(1);
        }
    }

    private boolean deleteTestCase(final TestCaseEntity testCase, IProgressMonitor monitor) {
        try {
            String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCase);
            monitor.subTask("Deleting '" + testCaseId + "'...");
            return deleteTestCase(testCase, sync, eventBroker);
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return false;
        } finally {
            monitor.worked(1);
        }
    }
}
