package com.kms.katalon.composer.testdata.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractEntityDialog;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class NewTestDataDialog extends AbstractEntityDialog {

    private String dataSource = DataFileEntity.DataFileDriverType.stringValues()[0];

    private Combo cbDataSourceType;

    public NewTestDataDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell, parentFolder);
        setDialogTitle(StringConstants.VIEW_TITLE_TEST_DATA);
        setDialogMsg(StringConstants.VIEW_MSG_CREATE_NEW_TEST_DATA);
    }

    @Override
    public Control createDialogBodyArea(Composite parent) {
        super.createDialogBodyArea(parent);
        return createDataSourceTypeControl(container, 2);
    }

    private Control createDataSourceTypeControl(Composite parent, int column) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(column, false));
        Label labelDataSourceType = new Label(parent, SWT.NONE);
        labelDataSourceType.setText(StringConstants.VIEW_LBL_DATA_TYPE);

        cbDataSourceType = new Combo(parent, SWT.READ_ONLY);
        cbDataSourceType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbDataSourceType.setItems(DataFileEntity.DataFileDriverType.stringValues());
        cbDataSourceType.select(0);
        cbDataSourceType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setDataSource(((Combo) e.getSource()).getText());
            }
        });

        return parent;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}