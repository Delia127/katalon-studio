package com.kms.katalon.composer.checkpoint.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;

public class DeleteCheckpointFolderHandler extends DeleteCheckpointHandler implements IDeleteFolderHandler {

    @Override
    public FolderType getFolderType() {
        return FolderType.CHECKPOINT;
    }

    @Override
    public boolean execute(FolderTreeEntity folderTreeEntity, IProgressMonitor monitor) {
        try {
            FolderEntity folder = (FolderEntity) folderTreeEntity.getObject();
            if (folder == null) {
                return false;
            }

            List<Object> descendantEntities = FolderController.getInstance().getAllDescentdantEntities(folder);
            monitor.beginTask(format(StringConstants.HAND_MSG_DELETING_X_Y, StringConstants.FOLDER, folder.getName()),
                    descendantEntities.size() + 1);

            List<IFile> affectedTestCaseScripts = TestArtifactScriptRefactor.createForFolderEntity(folder)
                    .findReferrersInTestCaseScripts(folder.getProject());

            List<IEntity> undeletedCheckpoints = new ArrayList<>();
            for (Object entity : descendantEntities) {
                if (monitor.isCanceled()) {
                    return false;
                }

                if (entity instanceof CheckpointEntity
                        && !deleteCheckpoint((CheckpointEntity) entity, monitor, affectedTestCaseScripts)) {
                    undeletedCheckpoints.add((CheckpointEntity) entity);
                    continue;
                }

                if (entity instanceof FolderEntity) {
                    deleteFolder((FolderEntity) entity, undeletedCheckpoints, monitor);
                }
            }

            deleteFolder(folder, undeletedCheckpoints, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            return true;
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

    private boolean deleteCheckpoint(CheckpointEntity checkpoint, IProgressMonitor monitor,
            List<IFile> affectedTestCaseScripts) {
        try {
            String checkpointId = checkpoint.getIdForDisplay();
            monitor.subTask(format(StringConstants.HAND_MSG_DELETING_X_Y, StringConstants.CHECKPOINT, checkpointId));
            return deleteCheckpoint(checkpoint, TestArtifactScriptRefactor.createForCheckpointEntity(checkpointId)
                    .findReferrers(affectedTestCaseScripts));
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.worked(1);
        }
    }
}
