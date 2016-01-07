package com.kms.katalon.composer.testcase.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DeleteTestCaseFolderHandler extends DeleteTestCaseHandler implements IDeleteFolderHandler {

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

            List<Object> descendant = FolderController.getInstance().getAllDescentdantEntities(folder);
            monitor.beginTask(MessageFormat.format(StringConstants.HAND_JOB_DELETING_FOLDER, folder.getIdForDisplay()),
                    descendant.size() + 1);

            List<IFile> affectedTestCaseScripts = GroovyRefreshUtil.findReferencesInTestCaseScripts(
                    folder.getIdForDisplay() + StringConstants.ENTITY_ID_SEPERATOR, folder.getProject());

            List<IEntity> undeletedTestCases = new ArrayList<IEntity>();

            for (Object descendantObject : descendant) {

                if (monitor.isCanceled()) {
                    return false;
                }

                if (descendantObject instanceof TestCaseEntity) {
                    if (!deleteTestCase((TestCaseEntity) descendantObject, monitor, affectedTestCaseScripts)) {
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
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_CASE_FOLDER);
            return false;
        } finally {
            monitor.done();
        }
    }

    /**
     * Delete test case folder
     * 
     * @param folder
     * @param undeletedTestCases
     * @param monitor
     */
    private void deleteFolder(FolderEntity folder, List<IEntity> undeletedTestCases, IProgressMonitor monitor) {
        try {
            boolean canDelete = true;
            for (IEntity entity : undeletedTestCases) {
                if (folder.equals(entity.getParentFolder())) {
                    canDelete = false;
                    break;
                }
            }
            if (canDelete) {
                monitor.subTask(MessageFormat.format(StringConstants.HAND_JOB_DELETING_FOLDER, folder.getIdForDisplay()));
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

    /**
     * Delete test case
     * 
     * @param testCase
     * @param monitor
     * @param affectedTestCaseScripts
     * @return
     */
    private boolean deleteTestCase(final TestCaseEntity testCase, IProgressMonitor monitor,
            List<IFile> affectedTestCaseScripts) {
        try {
            String testCaseId = testCase.getIdForDisplay();
            monitor.subTask(MessageFormat.format(StringConstants.HAND_JOB_DELETING, testCaseId));
            return performDeleteTestCase(testCase, sync, eventBroker, affectedTestCaseScripts);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            monitor.worked(1);
        }
    }
}
