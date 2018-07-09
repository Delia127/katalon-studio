package com.kms.katalon.composer.execution.trace;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;

public class CheckpointStyleRangeMatcher extends ArtifactStyleRangeMatcher {

    private static final String CHECK_POINT_ID_PATTERN = "'Checkpoints\\/([^']*)'";

    @Override
    public String getPattern() {
        return CHECK_POINT_ID_PATTERN;
    }

    @Override
    protected void internalClick(String checkpointId) {
        CheckpointEntity checkpoint = getCheckpoint(checkpointId);
        
        if (checkpoint == null) {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                MessageFormat.format(ComposerExecutionMessageConstants.WARN_CHECK_POINT_NOT_FOUND, checkpointId));
            return;
        }

        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.CHECKPOINT_OPEN, checkpoint);
    }

    private CheckpointEntity getCheckpoint(String checkpointId) {
        CheckpointEntity checkpoint = null;
        try {
            checkpoint = CheckpointController.getInstance().getByDisplayedId(checkpointId);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return checkpoint;
    }
}
