package com.kms.katalon.composer.testdata.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.dialog.TestDataReferencesDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testdata.DataFileEntity;

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

            FolderEntity folderEntity = (FolderEntity) folderTreeEntity.getObject();

            String folderId = FolderController.getInstance().getIdForDisplay(folderEntity);

            List<Object> descendant = FolderController.getInstance().getAllDescentdantEntities(folderEntity);
            monitor.beginTask("Deleting folder: " + folderId + "...", descendant.size() + 1);

            List<IEntity> undeleteTestDatas = new ArrayList<IEntity>();

            for (Object descendantObject : descendant) {
                if (monitor.isCanceled()) {
                    return false;
                }

                if (descendantObject instanceof DataFileEntity) {
                    if (!deleteTestData((DataFileEntity) descendantObject, monitor)) {
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

    private boolean deleteTestData(final DataFileEntity testData, IProgressMonitor monitor) {
        try {
            monitor.subTask("Deleting folder: " + testData.getName() + "...");

            final Map<String, List<TestSuiteTestCaseLink>> testDataReferences = TestDataController.getInstance()
                    .getTestDataReferences(testData);

            if (!testDataReferences.values().isEmpty()) {
                if (!canDelete()) {
                    if (!needToShowPreferenceDialog()) {
                        return false;
                    }
                    
                    final AbstractDeleteReferredEntityHandler handler = this;
    
                    sync.syncExec(new Runnable() {
                        
                        @Override
                        public void run() {
                            TestDataReferencesDialog dialog = new TestDataReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), testData, testDataReferences, handler);
                            dialog.open();
                        }
                    });
                }

                if (canDelete()) {
                    DeleteTestDataHandler.deleteTestDataReferences(testData, testDataReferences, eventBroker);
                } else {
                    return false;
                }

            }

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(testData);

            TestDataController.getInstance().deleteDataFile(testData);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, TestDataController.getInstance()
                    .getIdForDisplay(testData));
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
                String folderId = FolderController.getInstance().getIdForDisplay(folderEntity);

                monitor.subTask("Deleting folder: " + folderId + "...");
                FolderController.getInstance().deleteFolder(folderEntity);
            } else {
                undeletedTestDatas.add(folderEntity);
            }

        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        } finally {
            monitor.worked(1);
        }
    }

}
