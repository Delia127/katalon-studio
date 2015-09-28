package com.kms.katalon.composer.testdata.views;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class NewTestDataDialog extends TitleAreaDialog {

    private String name;
    private String dataSource;

    private Text txtName;
    private Combo cbDataSourceType;
    private Composite container;
    
    private FolderEntity parentFolder;

    public NewTestDataDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell);
        this.parentFolder = parentFolder;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
		// Set window title for dialog
		if (getShell() != null) getShell().setText(StringConstants.VIEW_WINDOW_TITLE_NEW);

        Composite area = (Composite) super.createDialogArea(parent);

        container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.verticalSpacing = 10;
        container.setLayout(glContainer);

        Label theLabel = new Label(container, SWT.NONE);
        theLabel.setText(StringConstants.VIEW_LBL_NAME);

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        updateControlStates();
        txtName.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                validateName();
                updateStatus();
            }
        });
        theLabel = new Label(container, SWT.NONE);
        theLabel.setText(StringConstants.VIEW_LBL_DATA_TYPE);

        cbDataSourceType = new Combo(container, SWT.READ_ONLY);
        cbDataSourceType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbDataSourceType.setItems(DataFileEntity.DataFileDriverType.stringValues());
        cbDataSourceType.select(0);
        cbDataSourceType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateStatus();
            }
        });
        
        return area;
    }
    
    private void updateControlStates() {
        txtName.setText(name);
        txtName.selectAll();
    }

    private void updateStatus() {
        if (validate()) {
            super.getButton(OK).setEnabled(true);
        } else {
            super.getButton(OK).setEnabled(false);
        }
    }
    
    private boolean validateName() {
        try {
            TestDataController.getInstance().validateTestDataName(parentFolder, txtName.getText());
            setErrorMessage(null);
            return true;
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return false;
        }
    }
    
    private boolean validate() {
        return validateName() && validateType();
    }
    
    private boolean validateType() {
        if (cbDataSourceType.getSelectionIndex() < 0) {
            return false;
        } 
        return true;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        updateStatus();
    }

    @Override
    public void create() {
      super.create();
      setTitle(StringConstants.VIEW_TITLE_TEST_DATA);
      setMessage(StringConstants.VIEW_MSG_CREATE_NEW_TEST_DATA, IMessageProvider.INFORMATION);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 300);
    }

    @Override
    protected void okPressed() {
        name = txtName.getText();
        dataSource = cbDataSourceType.getText();
        super.okPressed();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}