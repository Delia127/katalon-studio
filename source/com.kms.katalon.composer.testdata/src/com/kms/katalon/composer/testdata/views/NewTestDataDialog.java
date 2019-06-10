package com.kms.katalon.composer.testdata.views;

import java.util.HashMap;
import java.util.Map;

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

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

public class NewTestDataDialog extends CommonNewEntityDialog<DataFileEntity> {

    private String dataSource = DataFileEntity.DataFileDriverType.stringValues()[0];

    private Combo cbDataSourceType;
    
    private boolean shouldReadAsString;

    public NewTestDataDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(StringConstants.VIEW_TITLE_TEST_DATA);
        setDialogMsg(StringConstants.VIEW_MSG_CREATE_NEW_TEST_DATA);
        shouldReadAsString = true;
    }

    @Override
    protected Control createEntityCustomControl(Composite parent, int column, int span) {
        createDataSourceTypeControl(parent, column);
        createReadAsIsOption(parent);
        return super.createEntityCustomControl(parent, column, span);
    }
    
    private Control createReadAsIsOption(Composite parent) {
        Label labelDataSourceType = new Label(parent, SWT.NONE);
        labelDataSourceType.setText(StringConstants.VIEW_LBL_READ_AS_STRING);
        Button checkBox = new Button(parent, SWT.CHECK);
        checkBox.setSelection(shouldReadAsString);
        checkBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {                
               shouldReadAsString = !shouldReadAsString;                
            }
        });

        return parent;
    }

    private Control createDataSourceTypeControl(Composite parent, int column) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(column, false));
        Label labelDataSourceType = new Label(parent, SWT.NONE);
        labelDataSourceType.setText(StringConstants.VIEW_LBL_DATA_TYPE);

        cbDataSourceType = new Combo(parent, SWT.READ_ONLY);
        cbDataSourceType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbDataSourceType.setItems(DataFileDriverType.stringValues());
        cbDataSourceType.select(0);
        cbDataSourceType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setDataSource(((Combo) e.getSource()).getText());
            }
        });

        return parent;
    }

    private void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void createEntity() {
        try {
            entity = TestDataController.getInstance().newTestDataWithoutSave(parentFolder, getName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void setEntityProperties() {
        super.setEntityProperties();
        entity.setDriver(DataFileDriverType.fromValue(dataSource));
        entity.setContainsHeaders(true);
        Map<String, String> map = new HashMap<String, String>();
        map.put("readAsString", String.valueOf(shouldReadAsString));
        entity.setProperties(map);
    }
}
