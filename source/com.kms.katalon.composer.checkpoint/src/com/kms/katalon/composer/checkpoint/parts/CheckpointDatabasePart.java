package com.kms.katalon.composer.checkpoint.parts;

import org.eclipse.jface.window.Window;
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
import com.kms.katalon.composer.checkpoint.dialogs.EditCheckpointDatabaseDialog;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.DatabaseCheckpointSourceInfo;

public class CheckpointDatabasePart extends CheckpointAbstractPart {

    private Text txtQuery;

    private Button btnEdit;

    @Override
    protected Composite createSourceInfoPartDetails(Composite parent) {
        compSourceInfoDetails = new Composite(parent, SWT.NONE);
        GridLayout glCompositeSrcInfoDetails = new GridLayout(4, false);
        glCompositeSrcInfoDetails.marginWidth = 0;
        glCompositeSrcInfoDetails.marginHeight = 0;
        compSourceInfoDetails.setLayout(glCompositeSrcInfoDetails);
        compSourceInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblQuery = new Label(compSourceInfoDetails, SWT.NONE);
        lblQuery.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblQuery.setText(StringConstants.PART_LBL_SQL_QUERY);

        txtQuery = new Text(compSourceInfoDetails, SWT.BORDER | SWT.READ_ONLY);
        txtQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        btnEdit = new Button(compSourceInfoDetails, SWT.PUSH | SWT.FLAT);
        btnEdit.setText(StringConstants.EDIT);
        btnEdit.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

        return parent;
    }

    @Override
    protected void addSourceInfoConstrolListeners() {
        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                CheckpointEntity currentCheckpoint = getCheckpoint();
                EditCheckpointDatabaseDialog dialog = new EditCheckpointDatabaseDialog(Display.getCurrent()
                        .getActiveShell(), (DatabaseCheckpointSourceInfo) currentCheckpoint.getSourceInfo());
                if (dialog.open() != Window.OK || !dialog.isChanged()) {
                    return;
                }
                DatabaseCheckpointSourceInfo sourceInfo = dialog.getSourceInfo();
                currentCheckpoint.setSourceInfo(sourceInfo);
                loadCheckpointSourceInfo(sourceInfo);
                save();
            }
        });
    }

    @Override
    protected void loadCheckpointSourceInfo(CheckpointSourceInfo sourceInfo) {
        txtQuery.setText(((DatabaseCheckpointSourceInfo) sourceInfo).getQuery());
    }

}
