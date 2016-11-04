package com.kms.katalon.composer.checkpoint.parts;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.checkpoint.dialogs.EditCheckpointCsvSourceDialog;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo;

public class CheckpointCsvPart extends CheckpointExcelPart {

    @Override
    protected void addSourceInfoConstrolListeners() {
        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeCSVSourceInfoOperation());
            }
        });
    }

    private class ChangeCSVSourceInfoOperation extends ChangeCheckpointSourceInfoOperation {
        public ChangeCSVSourceInfoOperation() {
            super(ChangeCSVSourceInfoOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            CheckpointEntity currentCheckpoint = getCheckpoint();
            oldCheckpointSourceInfo = currentCheckpoint.getSourceInfo().clone();
            EditCheckpointCsvSourceDialog dialog = new EditCheckpointCsvSourceDialog(Display.getCurrent()
                    .getActiveShell(), (CsvCheckpointSourceInfo) currentCheckpoint.getSourceInfo());
            if (dialog.open() != Dialog.OK || !dialog.isChanged()) {
                return Status.CANCEL_STATUS;
            }
            CsvCheckpointSourceInfo sourceInfo = dialog.getSourceInfo();
            newCheckpointSourceInfo = sourceInfo.clone();
            currentCheckpoint.setSourceInfo(sourceInfo);
            loadCheckpointSourceInfo(sourceInfo);
            setDirty(true);
            return Status.OK_STATUS;
        }
    }
}
