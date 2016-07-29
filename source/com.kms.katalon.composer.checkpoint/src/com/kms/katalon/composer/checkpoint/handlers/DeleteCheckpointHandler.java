package com.kms.katalon.composer.checkpoint.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static java.text.MessageFormat.format;
import static org.eclipse.jface.dialogs.MessageDialog.openError;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.checkpoint.dialogs.CheckpointReferencesDialog;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;

public class DeleteCheckpointHandler extends AbstractDeleteReferredEntityHandler {

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return CheckpointTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof CheckpointTreeEntity)) {
                return false;
            }
            CheckpointTreeEntity checkpointTreeEntity = (CheckpointTreeEntity) treeEntity;
            monitor.beginTask(
                    format(StringConstants.HAND_MSG_DELETING_X_Y, checkpointTreeEntity.getTypeName(),
                            checkpointTreeEntity.getText()), 1);

            CheckpointEntity checkpoint = checkpointTreeEntity.getObject();
            String checkpointId = checkpoint.getIdForDisplay();
            List<IFile> affectedTestCaseScripts = TestArtifactScriptRefactor.createForCheckpointEntity(checkpointId)
                    .findReferrersInTestCaseScripts(checkpoint.getProject());

            if (deleteCheckpoint(checkpoint, affectedTestCaseScripts)) {
                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, checkpointId);
                return true;
            }
        } catch (Exception e) {
            logError(e);
            openError(null, StringConstants.ERROR, StringConstants.HAND_MSG_UNABLE_TO_DELETE_CHECKPOINT);
        } finally {
            monitor.done();
        }
        return false;
    }

    /**
     * Delete Checkpoint
     * 
     * @param checkpoint Checkpoint entity
     * @param affectedTestCaseScripts list of Test Case script
     * @return true if delete checkpoint successfully. Otherwise, false.
     */
    protected boolean deleteCheckpoint(final CheckpointEntity checkpoint, final List<IFile> affectedTestCaseScripts) {
        isDeleted = false;
        sync.syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    List<FileEntity> testCaseRefList = new ArrayList<>();
                    testCaseRefList.addAll(TestCaseEntityUtil.getTestCaseEntities(affectedTestCaseScripts));

                    if (!testCaseRefList.isEmpty()) {
                        String checkpointId = checkpoint.getIdForDisplay();
                        if (isDefaultResponse()) {
                            CheckpointReferencesDialog dialog = new CheckpointReferencesDialog(Display.getCurrent()
                                    .getActiveShell(), checkpointId, testCaseRefList, needYesNoToAllButtons());
                            setResponse(dialog.open());
                        }

                        if (isCancelResponse()) {
                            return;
                        }

                        if (isYesResponse()) {
                            // remove references in test case
                            TestArtifactScriptRefactor.createForCheckpointEntity(checkpointId).removeReferences(
                                    affectedTestCaseScripts);
                        }
                    }

                    // Remove Checkpoint part from its partStack if it exists
                    EntityPartUtil.closePart(checkpoint);

                    // Delete Checkpoint
                    CheckpointController.getInstance().delete(checkpoint);

                    if (!isYesNoToAllResponse()) {
                        resetResponse();
                    }

                    isDeleted = true;
                } catch (Exception e) {
                    logError(e);
                }
            }
        });
        return isDeleted;
    }

}
