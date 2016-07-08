package com.kms.katalon.composer.checkpoint.dialogs;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.CsvCheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.DatabaseCheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.ExcelCheckpointSourceInfo;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

public class NewCheckpointDialog extends CommonNewEntityDialog<CheckpointEntity> {

    private DataFileEntity testdata;

    private String dataSourceType = null;

    private Combo cbDataSourceType;

    private Button chkIsTestDataSource;

    public NewCheckpointDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(StringConstants.CHECKPOINT);
        setDialogMsg(StringConstants.DIA_MSG_CREATE_CHECKPOINT);
    }

    public NewCheckpointDialog(Shell parentShell, DataFileEntity testdata, String suggestedName) {
        this(parentShell, getCheckpointRootFolder(), suggestedName);
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

    private boolean isCreatingFromTestData() {
        return testdata != null;
    }

    private Control createDataSourceTypeControl(Composite parent, int column) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(column, false));

        new Label(parent, SWT.NONE);

        chkIsTestDataSource = new Button(parent, SWT.CHECK);
        chkIsTestDataSource.setSelection(true);
        chkIsTestDataSource.setEnabled(!isCreatingFromTestData());
        chkIsTestDataSource.setText(StringConstants.DIA_CHK_IS_TEST_DATA_SOURCE);
        chkIsTestDataSource.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isSelected = ((Button) e.getSource()).getSelection();
                cbDataSourceType.select(0);
                if (isSelected) {
                    cbDataSourceType.deselectAll();
                }
                cbDataSourceType.setEnabled(!isSelected);
                setDataSourceType(StringUtils.defaultIfEmpty(cbDataSourceType.getText(), null));
            }
        });

        Label labelDataSourceType = new Label(parent, SWT.NONE);
        labelDataSourceType.setText(StringConstants.DIA_LBL_DATA_TYPE);

        cbDataSourceType = new Combo(parent, SWT.READ_ONLY);
        cbDataSourceType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbDataSourceType.setItems(CheckpointSourceInfo.SUPPORTED_SELF_DEFINED_DATA_SOURCE);
        cbDataSourceType.setEnabled(!chkIsTestDataSource.getSelection());
        cbDataSourceType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setDataSourceType(((Combo) e.getSource()).getText());
            }
        });

        return parent;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    @Override
    protected void setEntityProperties() {
        super.setEntityProperties();
        if (chkIsTestDataSource.getSelection()) {
            entity.setSourceInfo(new CheckpointSourceInfo(testdata != null ? testdata.getIdForDisplay()
                    : StringConstants.EMPTY));
            return;
        }

        if (StringUtils.equals(dataSourceType, DataFileDriverType.DBData.toString())) {
            entity.setSourceInfo(new DatabaseCheckpointSourceInfo());
            return;
        }

        if (StringUtils.equals(dataSourceType, DataFileDriverType.ExcelFile.toString())) {
            entity.setSourceInfo(new ExcelCheckpointSourceInfo());
            return;
        }

        if (StringUtils.equals(dataSourceType, DataFileDriverType.CSV.toString())) {
            entity.setSourceInfo(new CsvCheckpointSourceInfo());
        }
    }

}
