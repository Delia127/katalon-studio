package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.editors.EntitySelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class CheckpointSelectionMethodCallBuilderDialog extends EntitySelectionDialogCellEditor {

    private MethodCallExpressionWrapper methodCall;

    public CheckpointSelectionMethodCallBuilderDialog(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    public String getDialogTitle() {
        return ComposerTestcaseMessageConstants.DIA_TITLE_CHECKPOINT_BROWSER;
    }

    @Override
    public FolderEntity getRootFolder() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return null;
        }
        try {
            return FolderController.getInstance().getCheckpointRoot(currentProject);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    protected void doSetValue(Object value) {
        CheckpointEntity checkpoint = null;
        if (value instanceof MethodCallExpressionWrapper) {
            methodCall = ((MethodCallExpressionWrapper) value).clone();
            String checkpointId = AstEntityInputUtil.findCheckpointIdArgumentFromFindCheckpointMethodCall(methodCall);
            if (checkpointId == null) {
                return;
            }
            try {
                checkpoint = CheckpointController.getInstance().getByDisplayedId(checkpointId);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (value instanceof CheckpointTreeEntity) {
            try {
                checkpoint = ((CheckpointTreeEntity) value).getObject();
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        if (checkpoint == null) {
            return;
        }
        super.doSetValue(checkpoint);
    }

    @Override
    public ITreeEntity getInitialSelection() {
        try {
            String checkpointId = AstEntityInputUtil.findCheckpointIdArgumentFromFindCheckpointMethodCall(methodCall);
            CheckpointEntity selectedCheckpoint = CheckpointController.getInstance().getByDisplayedId(checkpointId);
            if (selectedCheckpoint == null) {
                return null;
            }
            return new CheckpointTreeEntity(selectedCheckpoint, TreeEntityUtil
                    .createSelectedTreeEntityHierachy(selectedCheckpoint.getParentFolder(), getRootFolder()));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    protected MethodCallExpressionWrapper doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof CheckpointEntity)) {
            return null;
        }
        AstEntityInputUtil.setCheckpointIdIntoFindCheckpointMethodCall(methodCall,
                ((CheckpointEntity) value).getIdForDisplay());
        return methodCall;
    }
}
