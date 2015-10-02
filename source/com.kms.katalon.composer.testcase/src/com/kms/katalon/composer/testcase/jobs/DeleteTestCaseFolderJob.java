package com.kms.katalon.composer.testcase.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.dialogs.TestCaseReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteTestCaseFolderJob extends Job {

    private FolderEntity fFolder;
    private FolderTreeEntity fFolderTree;
    private IEventBroker eventBroker;
    private UISynchronize sync;
    private Shell activeShell;

    private boolean isDeleteConfirmed;
    private boolean isUserCanceled;

    public DeleteTestCaseFolderJob(FolderTreeEntity folderTreeEntity, UISynchronize sync, Shell shell) throws Exception {
        super("Delete test case's folder");
        fFolderTree = folderTreeEntity;
        fFolder = (FolderEntity) fFolderTree.getObject();
        eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        this.sync = sync;
        this.activeShell = shell;
        this.isDeleteConfirmed = false;
        this.isUserCanceled = false;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            List<Object> descendant = FolderController.getInstance().getAllDescentdantEntities(fFolder);
            monitor.beginTask("Deleting folder: " + fFolder.getName() + "...", descendant.size() + 1);
            
            List<IEntity> undeletedTestCases = new ArrayList<IEntity>();
            
            for (Object descendantObject : descendant) {

                if (isUserCanceled || monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
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

            
            deleteFolder(fFolder, undeletedTestCases, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, fFolderTree.getParent());
            monitor.worked(1);
            return Status.OK_STATUS;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }
    }
    
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
                monitor.subTask("Deleting folder: " + folder.getName() + "...");
                
                FolderController.getInstance().deleteFolder(folder);
            } else  {
                undeletedTestCases.add(folder);
            }
            
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
        
    }

    private boolean deleteTestCase(final TestCaseEntity testCase, IProgressMonitor monitor) {
        try {
            monitor.subTask("Deleting test case: " + testCase.getName() + "...");
            final List<TestSuiteEntity> testCaseReferences = TestCaseController.getInstance().getTestCaseReferences(
                    testCase);

            isDeleteConfirmed = true;
            if (!testCaseReferences.isEmpty()) {
                isDeleteConfirmed = false;
                performTestCaseDeletionConfirmation(testCase, testCaseReferences);
            }
            
            if (!isDeleteConfirmed) {
                return false;
            }

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(testCase);

            TestCaseController.getInstance().deleteTestCase(testCase);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, TestCaseController.getInstance()
                    .getIdForDisplay(testCase));
            return true;
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return false;
        }
    }
    
    private void performTestCaseDeletionConfirmation(final TestCaseEntity testCase, final List<TestSuiteEntity> testCaseReferences) {
        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                TestCaseReferencesDialog dialog = new TestCaseReferencesDialog(activeShell, testCase,
                        testCaseReferences);
                if (dialog.open() == Dialog.OK) {
                    String testCaseId;
                    try {
                        testCaseId = TestCaseController.getInstance().getIdForDisplay(testCase);

                        for (TestSuiteEntity testSuite : testCaseReferences) {

                            TestSuiteTestCaseLink testCaseLink = TestSuiteController.getInstance()
                                    .getTestCaseLink(testCaseId, testSuite);
                            testSuite.getTestSuiteTestCaseLinks().remove(testCaseLink);

                            eventBroker.post(EventConstants.TEST_SUITE_UPDATED,
                                    new Object[] { testSuite.getId(), testSuite });
                        }
                        
                        isDeleteConfirmed = true;
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}
