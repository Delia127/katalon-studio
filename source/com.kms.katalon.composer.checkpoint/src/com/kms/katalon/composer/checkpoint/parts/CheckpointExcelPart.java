package com.kms.katalon.composer.checkpoint.parts;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.checkpoint.dialogs.EditCheckpointExcelSourceDialog;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.ExcelCheckpointSourceInfo;

public class CheckpointExcelPart extends CheckpointAbstractPart {

    protected Button btnEdit;

    private Text txtSourceUrl;

    @Override
    protected Composite createSourceInfoPartDetails(Composite parent) {
        compSourceInfoDetails = new Composite(parent, SWT.NONE);
        GridLayout glCompositeSrcInfoDetails = new GridLayout(4, false);
        glCompositeSrcInfoDetails.marginWidth = 0;
        glCompositeSrcInfoDetails.marginHeight = 0;
        compSourceInfoDetails.setLayout(glCompositeSrcInfoDetails);
        compSourceInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSourceUrl = new Label(compSourceInfoDetails, SWT.NONE);
        lblSourceUrl.setText(StringConstants.PART_LBL_FILE_PATH);

        txtSourceUrl = new Text(compSourceInfoDetails, SWT.BORDER | SWT.READ_ONLY);
        txtSourceUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnEdit = new Button(compSourceInfoDetails, SWT.PUSH | SWT.FLAT);
        btnEdit.setText(StringConstants.EDIT);
        btnEdit.setLayoutData(new GridData(SWT.TRAIL, SWT.FILL, false, true));

        return parent;
    }

    @Override
    protected void addSourceInfoConstrolListeners() {
        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeExcelSourceInfoOperation());
            }
        });
    }

    @Override
    protected void loadCheckpointSourceInfo(CheckpointSourceInfo sourceInfo) {
        txtSourceUrl.setText(sourceInfo.getSourceUrl());
    }

    private class ChangeExcelSourceInfoOperation extends ChangeCheckpointSourceInfoOperation {
        public ChangeExcelSourceInfoOperation() {
            super(ChangeExcelSourceInfoOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            CheckpointEntity checkpoint = getCheckpoint();
            oldCheckpointSourceInfo = checkpoint.getSourceInfo().clone();
            EditCheckpointExcelSourceDialog dialog = new EditCheckpointExcelSourceDialog(
                    Display.getCurrent().getActiveShell(), (ExcelCheckpointSourceInfo) checkpoint.getSourceInfo());
            if (dialog.open() != Dialog.OK || !dialog.isChanged()) {
                return Status.CANCEL_STATUS;
            }
            ExcelCheckpointSourceInfo sourceInfo = dialog.getSourceInfo();
            newCheckpointSourceInfo = sourceInfo.clone();
            checkpoint.setSourceInfo(sourceInfo);
            loadCheckpointSourceInfo(sourceInfo);
            setDirty(true);
            return Status.OK_STATUS;
        }
    }
}
