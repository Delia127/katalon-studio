package com.kms.katalon.composer.webservice.view;


import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class ImportWebServiceObjectsFromWSDLDialog  extends TitleAreaDialog {

    private List<WebServiceRequestEntity> soapWebServiceRequestEntities;
    private List<WebServiceRequestEntity> soap12WebServiceRequestEntities;
    private String directory = "";
    
    public ImportWebServiceObjectsFromWSDLDialog(Shell parentShell) {
        super(parentShell);
    }


    
    private void createSoapWebServiceRequestEntities() throws Exception{
    	soapWebServiceRequestEntities = ObjectRepositoryController.getInstance().
    			newWSTestObjectsFromWSDL(WebServiceRequestEntity.SOAP, directory);  
    	if(soapWebServiceRequestEntities == null){
    		throw new Exception();
    	}
    }
    
    private void createSoap12WebServiceRequestEntities() throws Exception{
    	soap12WebServiceRequestEntities = ObjectRepositoryController.getInstance().
    			newWSTestObjectsFromWSDL(WebServiceRequestEntity.SOAP12, directory);  
    	if(soap12WebServiceRequestEntities == null){
    		throw new Exception();
    	}
    }
    
    private void createWebServiceRequestEntities() throws Exception{
    	createSoapWebServiceRequestEntities();
    	createSoap12WebServiceRequestEntities();
    }
    
    public List<WebServiceRequestEntity> getWebServiceRequestEntities(String requestMethod){
    	switch(requestMethod){
    		case WebServiceRequestEntity.SOAP:
    			return soapWebServiceRequestEntities;
    		case WebServiceRequestEntity.SOAP12:
    			return soap12WebServiceRequestEntities;
    		default:
    			return null;
    	}
    }

    @Override
    protected void okPressed() {
    	boolean closeTheDialog = true;
    	try{
        	createWebServiceRequestEntities();
    	} catch(Exception e){
    		closeTheDialog = false;
    		setErrorMessage(StringConstants.EXC_INVALID_WSDL_FILE);
    	} finally {
    		if(closeTheDialog == true){
    	        super.okPressed();
    		}
    	}
    }


	@Override
	protected Control createDialogArea(Composite parent) {
        setTitle(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ_WSDL);
		// top level composite
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        // create a composite with standard margins and spacing
        Composite composite = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    	Label label = new Label(composite, SWT.NONE);
    	label.setText("URL : ");
        Text text = new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	Composite methodComposite = new Composite(composite, SWT.NONE);
    	GridLayout glMethodComposite = new GridLayout();
        methodComposite.setLayout(glMethodComposite);
        ModifyListener listener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
              directory = ((Text) e.widget).getText();
            }
          };
        text.addModifyListener(listener);
		return parentComposite;
	}
	
	@Override
	protected boolean isResizable() {
	    return false;
	}
	
	@Override
	protected void configureShell(Shell newShell)
	{
	  super.configureShell(newShell);
	  newShell.setText(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ_WSDL);
	}
}
