package com.kms.katalon.composer.checkpoint.dialogs;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class NewCheckpointFromTestDataDialog extends CommonNewEntityDialog<CheckpointEntity> {

    private DataFileEntity testdata;

    private Text txtId;

    public NewCheckpointFromTestDataDialog(Shell parentShell, DataFileEntity testdata, String suggestedName) {
        super(parentShell, getCheckpointRootFolder(), suggestedName);
        setDialogTitle(StringConstants.CHECKPOINT);
        setDialogMsg(StringConstants.DIA_MSG_CREATE_CHECKPOINT_FROM_TEST_DATA);
        this.testdata = testdata;
    }

    @Inject
    private static FolderEntity getCheckpointRootFolder() {
        try {
            return FolderController.getInstance()
                    .getCheckpointRoot(ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    protected void createEntity() {
        try {
            entity = CheckpointController.getInstance().initialNewCheckpoint(parentFolder, getName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Control createEntityCustomControl(Composite parent, int column, int span) {
        createDataSourceTypeControl(parent, column);
        return super.createEntityCustomControl(parent, column, span);
    }

    private Control createDataSourceTypeControl(Composite parent, int column) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(column, false));

        Label labelDataSourceType = new Label(parent, SWT.NONE);
        labelDataSourceType.setText(StringConstants.DIA_LBL_TEST_DATA_ID);

        txtId = new Text(parent, SWT.READ_ONLY | SWT.BORDER);
        txtId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtId.setText(testdata.getIdForDisplay());

        return parent;
    }

    @Override
    protected void setEntityProperties() {
        super.setEntityProperties();
        entity.setSourceInfo(new CheckpointSourceInfo(testdata.getIdForDisplay()));
    }

}
