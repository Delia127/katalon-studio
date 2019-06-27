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
    
    private Composite readAsStringOptionComposite;
    
    private boolean shouldReadAsString;

    public NewTestDataDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(StringConstants.VIEW_TITLE_TEST_DATA);
        setDialogMsg(StringConstants.VIEW_MSG_CREATE_NEW_TEST_DATA);
        shouldReadAsString = false;
    }

    @Override
    protected Control createEntityCustomControl(Composite parent, int column, int span) {
        createDataSourceTypeControl(parent, column);
        createReadAsStringOption(parent, column);
        return super.createEntityCustomControl(parent, column, span);
    }
    
    private Control createReadAsStringOption(Composite parent, int column) {
        
        readAsStringOptionComposite = new Composite(parent, SWT.NONE);
        GridLayout glReadAsStringOption = new GridLayout(column, false);
        glReadAsStringOption.marginHeight = 0;
        glReadAsStringOption.marginWidth = 0;
        readAsStringOptionComposite.setLayout(glReadAsStringOption);
        readAsStringOptionComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1));
        
        Label labelDataSourceType = new Label(readAsStringOptionComposite, SWT.NONE);
        labelDataSourceType.setText(StringConstants.VIEW_LBL_READ_AS_STRING);
        Button ckcbReadAsString = new Button(readAsStringOptionComposite, SWT.CHECK);
        ckcbReadAsString.setSelection(shouldReadAsString);
        ckcbReadAsString.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                shouldReadAsString = !shouldReadAsString;
            }
        });

        return readAsStringOptionComposite;
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
                String txtSelectedDataSource = ((Combo) e.getSource()).getText();
                setDataSource(txtSelectedDataSource);
                handleShowReadAsStringOption(txtSelectedDataSource);                
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
    
    protected void handleShowReadAsStringOption(String text) {
        DataFileDriverType type = DataFileDriverType.fromValue(text);
        if (type.equals(DataFileDriverType.CSV) || type.equals(DataFileDriverType.InternalData)) {
            readAsStringOptionComposite.setVisible(false);
        } else {
            readAsStringOptionComposite.setVisible(true);
        }
    }
}
