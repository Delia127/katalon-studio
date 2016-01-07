package com.kms.katalon.composer.testdata.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.dialog.TestDataReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DeleteTestDataFolderHandler extends AbstractDeleteReferredEntityHandler implements IDeleteFolderHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private UISynchronize sync;

    @Override
    public FolderType getFolderType() {
        return FolderType.DATAFILE;
    }

    @Override
    public boolean execute(FolderTreeEntity folderTreeEntity, IProgressMonitor monitor) {
        try {
            if (folderTreeEntity == null || folderTreeEntity.getObject() == null) {
                return false;
            }

            final FolderEntity folderEntity = (FolderEntity) folderTreeEntity.getObject();
            final String folderId = folderEntity.getIdForDisplay();
            List<Object> descendant = FolderController.getInstance().getAllDescentdantEntities(folderEntity);

            monitor.beginTask(MessageFormat.format(StringConstants.HAND_JOB_DELETING_FOLDER, folderId),
                    descendant.size() + 1);

            List<IFile> affectedTestCaseScripts = GroovyRefreshUtil.findReferencesInTestCaseScripts(folderId
                    + StringConstants.ENTITY_ID_SEPERATOR, folderEntity.getProject());

            List<IEntity> undeleteTestDatas = new ArrayList<IEntity>();

            for (Object descendantObject : descendant) {
                if (monitor.isCanceled()) {
                    return false;
                }

                if (descendantObject instanceof DataFileEntity) {
                    if (!deleteTestData((DataFileEntity) descendantObject, monitor, affectedTestCaseScripts)) {
                        undeleteTestDatas.add((DataFileEntity) descendantObject);
                    }
                } else if (descendantObject instanceof FolderEntity) {
                    FolderEntity descendantFolder = (FolderEntity) descendantObject;
                    deleteFolder(descendantFolder, undeleteTestDatas, monitor);
                }
            }

            deleteFolder(folderEntity, undeleteTestDatas, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            return true;
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return false;
        } finally {
            monitor.done();
        }
    }

    private boolean deleteTestData(final DataFileEntity testData, IProgressMonitor monitor,
            List<IFile> affectedTestCaseScripts) {
        try {
            monitor.subTask(MessageFormat.format(StringConstants.HAND_JOB_DELETING_FOLDER, testData.getName()));
            final String testDataId = testData.getIdForDisplay();
            final Map<String, List<TestSuiteTestCaseLink>> referencesInTestSuite = TestDataController.getInstance()
                    .getTestDataReferences(testData);
            final List<TestCaseEntity> referencesInTestCase = TestCaseEntityUtil
                    .getTestCaseEntities(affectedTestCaseScripts);

            if (!referencesInTestSuite.values().isEmpty()) {
                if (!canDelete()) {
                    if (!needToShowPreferenceDialog()) {
                        return false;
                    }

                    final AbstractDeleteReferredEntityHandler handler = this;

                    sync.syncExec(new Runnable() {

                        @Override
                        public void run() {
                            TestDataReferencesDialog dialog = new TestDataReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), testDataId, referencesInTestSuite, referencesInTestCase, handler);
                            dialog.open();
                        }
                    });
                }

                if (canDelete()) {
                    // remove test data references in test suite
                    DeleteTestDataHandler.removeReferencesInTestSuites(testData, referencesInTestSuite, eventBroker);

                    // remove test data references in test case script
                    GroovyRefreshUtil.removeReferencesInTestCaseScripts(testDataId, affectedTestCaseScripts);
                } else {
                    return false;
                }

            }

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(testData);

            TestDataController.getInstance().deleteDataFile(testData);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, testDataId);
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_DATA);
            return false;
        } finally {
            monitor.worked(1);
        }
    }

    private void deleteFolder(FolderEntity folderEntity, List<IEntity> undeletedTestDatas, IProgressMonitor monitor) {
        try {
            boolean canDelete = true;
            for (IEntity entity : undeletedTestDatas) {
                if (folderEntity.equals(entity.getParentFolder())) {
                    canDelete = false;
                    break;
                }
            }

            if (canDelete) {
                monitor.subTask(MessageFormat.format(StringConstants.HAND_JOB_DELETING_FOLDER,
                        folderEntity.getIdForDisplay()));
                FolderController.getInstance().deleteFolder(folderEntity);
            } else {
                undeletedTestDatas.add(folderEntity);
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } finally {
            monitor.worked(1);
        }
    }

}
