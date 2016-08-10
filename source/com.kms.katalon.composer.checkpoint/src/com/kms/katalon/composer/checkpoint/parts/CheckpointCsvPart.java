package com.kms.katalon.composer.checkpoint.parts;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.checkpoint.dialogs.EditCheckpointCsvSourceDialog;
import com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo;

public class CheckpointCsvPart extends CheckpointExcelPart {

    @Override
    protected void addSourceInfoConstrolListeners() {
        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EditCheckpointCsvSourceDialog dialog = new EditCheckpointCsvSourceDialog(Display.getCurrent()
                        .getActiveShell(), (CsvCheckpointSourceInfo) getCheckpoint().getSourceInfo());
                if (dialog.open() != Dialog.OK || !dialog.isChanged()) {
                    return;
                }
                CsvCheckpointSourceInfo sourceInfo = dialog.getSourceInfo();
                getCheckpoint().setSourceInfo(sourceInfo);
                save();
                loadCheckpointSourceInfo(sourceInfo);
            }
        });
    }

}
