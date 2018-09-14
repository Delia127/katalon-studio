package com.kms.katalon.composer.webservice.view;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class ImportWebServiceObjectsFromSwaggerDialog  extends AbstractDialog {

    private FolderEntity parentFolder;
    private List<WebServiceRequestEntity> webServiceRequestEntities;
    private String directory = "";
    
    public ImportWebServiceObjectsFromSwaggerDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell);
    	this.parentFolder = parentFolder;
        setDialogTitle(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ_SWAGGER);
    }


    private Control createImportFromSwaggerControl(Composite parent, int column) {

        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
    	Composite methodComposite = new Composite(parent, SWT.NONE);
    	GridLayout glMethodComposite = new GridLayout();
        methodComposite.setLayout(glMethodComposite);
        
        Button button = new Button(methodComposite, SWT.PUSH);
        button.setText(StringConstants.BROWSE);
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e) {
            	FileDialog directoryDialog = new FileDialog(getParentShell());
                String filePath = directoryDialog.open();          
                label.setText(filePath);
                directory = filePath;
            }
        });

        return parent;
    }
    
    
    public void createWebServiceRequestEntities(){
        try {
        	webServiceRequestEntities = ObjectRepositoryController.getInstance().
        			newWSTestObjectsFromSwagger(parentFolder, directory);
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

	@Override
	protected void registerControlModifyListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Control createDialogContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout();
        container.setLayout(glMain);
        Composite bodyComposite = new Composite(container, SWT.NONE);
        bodyComposite.setLayout(new FillLayout(SWT.VERTICAL));
        bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        SashForm hSashForm = new SashForm(bodyComposite, SWT.NONE);
        hSashForm.setSashWidth(0);
        Composite leftPanelComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout glHtmlDomComposite = new GridLayout();
        leftPanelComposite.setLayout(glHtmlDomComposite);        
		createImportFromSwaggerControl(leftPanelComposite, 1);
		return null;
	}
	
	@Override
	protected boolean isResizable() {
	    return true;
	}
	
	@Override
	protected Point getInitialSize() {
	    final Point size = super.getInitialSize();

	    size.x = convertWidthInCharsToPixels(100);


	    return size;
	}

}
