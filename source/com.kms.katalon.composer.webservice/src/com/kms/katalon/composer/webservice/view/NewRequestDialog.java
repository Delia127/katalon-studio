package com.kms.katalon.composer.webservice.view;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
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

import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class NewRequestDialog extends TitleAreaDialog {

    private String name;
    private String webServiveType;
	private Text txtName;
    private Combo cbbRequestType;
    private Composite container;
    
    @SuppressWarnings("unused")
	private FolderEntity parentFolder;

    public NewRequestDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell);
        this.parentFolder = parentFolder;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // Set window title for dialog
        if (getShell() != null) getShell().setText(StringConstants.WIEW_TITLE_NEW);

        Composite area = (Composite) super.createDialogArea(parent);

        container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gl_container_1 = new GridLayout(2, false);
        gl_container_1.verticalSpacing = 10;
        container.setLayout(gl_container_1);

        Label theLabel = new Label(container, SWT.NONE);
        theLabel.setText(StringConstants.VIEW_COL_NAME);

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //updateControlStates();
        /*txtName.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                validateName();
                updateStatus();
            }
        });
        */
        txtName.setText(name == null ? "" : name);
        theLabel = new Label(container, SWT.NONE);
        theLabel.setText(StringConstants.VIEW_LBL_REQ_TYPE);

        cbbRequestType = new Combo(container, SWT.READ_ONLY);
        cbbRequestType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbbRequestType.setItems(WebServiceRequestEntity.SERVICE_TYPES);
        cbbRequestType.select(0);
        cbbRequestType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                //updateStatus();
            }
        });

        // Build the separator line
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return area;
    }
    
    /*
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
        if (cbbRequestType.getSelectionIndex() < 0) {
            return false;
        } 
        return true;
    }
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        //updateStatus();
    }

    @Override
    public void create() {
      super.create();
      setTitle(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ);
      setMessage(StringConstants.VIEW_DIA_MSG_CREATE_NEW_WEBSERVICE_REQ, IMessageProvider.INFORMATION);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 300);
    }

    @Override
    protected void okPressed() {
        name = txtName.getText();
        webServiveType = cbbRequestType.getText();
        super.okPressed();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebServiveType() {
		return webServiveType;
	}

	public void setWebServiveType(String webServiveType) {
		this.webServiveType = webServiveType;
	}
}