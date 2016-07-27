package com.kms.katalon.composer.testdata.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;

public class DeleteTestDataFolderHandler extends DeleteTestDataHandler implements IDeleteFolderHandler {

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
            String folderId = folderEntity.getIdForDisplay();
            List<Object> descendantEntities = FolderController.getInstance().getAllDescentdantEntities(folderEntity);

            monitor.beginTask(format(StringConstants.HAND_JOB_DELETING_FOLDER, folderId), descendantEntities.size() + 1);

            List<IFile> affectedTestCaseScripts = TestArtifactScriptRefactor.createForFolderEntity(folderEntity)
                    .findReferrersInTestCaseScripts(ProjectController.getInstance().getCurrentProject());

            List<IEntity> undeleteTestDatas = new ArrayList<IEntity>();

            for (Object entity : descendantEntities) {
                if (monitor.isCanceled()) {
                    return false;
                }

                if (entity instanceof DataFileEntity
                        && !deleteTestData((DataFileEntity) entity, monitor, affectedTestCaseScripts)) {
                    undeleteTestDatas.add((DataFileEntity) entity);
                    continue;
                }

                if (entity instanceof FolderEntity) {
                    deleteFolder((FolderEntity) entity, undeleteTestDatas, monitor);
                }
            }

            deleteFolder(folderEntity, undeleteTestDatas, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            return true;
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

    private boolean deleteTestData(DataFileEntity testData, IProgressMonitor monitor,
            List<IFile> affectedTestCaseScripts) {
        try {
            String testDataId = testData.getIdForDisplay();
            monitor.subTask(format(StringConstants.HAND_JOB_DELETING_ENTITY, testDataId));
            return performDeleteTestData(testData, TestArtifactScriptRefactor.createForTestDataEntity(testDataId)
                    .findReferrers(affectedTestCaseScripts));
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.worked(1);
        }
    }
}
