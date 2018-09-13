package com.kms.katalon.composer.webservice.view;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import com.kms.katalon.composer.components.impl.dialogs.AbstractEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class ImportWebServiceObjectsFromSwaggerDialog  extends AbstractEntityDialog {

    private String directory = "";

    
    private List<WebServiceRequestEntity> webServiceRequestEntities;
    
    public ImportWebServiceObjectsFromSwaggerDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell, parentFolder);
        setDialogTitle(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ_SWAGGER);
        setDialogMsg(StringConstants.VIEW_DIA_MSG_CREATE_NEW_WEBSERVICE_REQ_SWAGGER);
    }

    @Override
    protected Control createEntityCustomControl(Composite parent, int column, int span) {
        createImportFromSwaggerControl(parent, column);
        return super.createEntityCustomControl(parent, column, span);
    }

    private Control createImportFromSwaggerControl(Composite parent, int column) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(column, false));        
        
        Label lblDirectory = new Label(parent, SWT.NONE);       
        Button button = new Button(parent, SWT.PUSH);

        button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        button.setText("Browse");
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e) {
            	DirectoryDialog directoryDialog = new DirectoryDialog(getParentShell());
                directoryDialog.open();
                String dialogResultPath = directoryDialog.getFilterPath();
                
                if (!dialogResultPath.isEmpty()) {
                	setDirectory(dialogResultPath);
                	lblDirectory.setText(dialogResultPath);
                }
            }
        });

        return parent;
    }

    private void setDirectory(String directory){
    	this.directory = directory;
    }
    
    public void createWebServiceRequestEntities(){
        try {
        	webServiceRequestEntities = ObjectRepositoryController.getInstance().newWSTestObjectsFromSwagger(parentFolder, directory);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
    
    public List<WebServiceRequestEntity> getWebServiceRequestEntities(){
		return webServiceRequestEntities;
    }
    
    @Override
    protected void okPressed() {
    	createWebServiceRequestEntities();
        super.okPressed();
    }

}
