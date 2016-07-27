package com.kms.katalon.composer.testcase.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static java.text.MessageFormat.format;
import static org.eclipse.jface.dialogs.MessageDialog.openError;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;

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

            List<Object> descendantEntities = FolderController.getInstance().getAllDescentdantEntities(folder);
            String folderIdForDisplay = folder.getIdForDisplay();
            monitor.beginTask(format(StringConstants.HAND_JOB_DELETING_FOLDER, folderIdForDisplay),
                    descendantEntities.size() + 1);

            List<IFile> affectedTestCaseScripts = TestArtifactScriptRefactor.createForFolderEntity(folder)
                    .findReferrersInTestCaseScripts(ProjectController.getInstance().getCurrentProject());

            List<IEntity> undeletedTestCases = new ArrayList<IEntity>();

            for (Object entity : descendantEntities) {
                if (monitor.isCanceled()) {
                    return false;
                }

                if (entity instanceof TestCaseEntity
                        && !deleteTestCase((TestCaseEntity) entity, monitor, affectedTestCaseScripts)) {
                    undeletedTestCases.add((TestCaseEntity) entity);
                    continue;
                }

                if (entity instanceof FolderEntity) {
                    deleteFolder((FolderEntity) entity, undeletedTestCases, monitor);
                }
            }

            deleteFolder(folder, undeletedTestCases, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            return true;
        } catch (Exception e) {
            logError(e);
            openError(null, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_CASE_FOLDER);
            return false;
        } finally {
            monitor.done();
        }
    }

    /**
     * Delete Test Case
     * 
     * @param testCase Test Case to be removed
     * @param monitor Progress Monitor
     * @param affectedTestCaseScripts
     * @return
     */
    private boolean deleteTestCase(TestCaseEntity testCase, IProgressMonitor monitor,
            List<IFile> affectedTestCaseScripts) {
        try {
            String testCaseId = testCase.getIdForDisplay();
            monitor.subTask(format(StringConstants.HAND_JOB_DELETING, testCaseId));
            return performDeleteTestCase(testCase, TestArtifactScriptRefactor.createForTestCaseEntity(testCaseId)
                    .findReferrers(affectedTestCaseScripts));
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.worked(1);
        }
    }
}
