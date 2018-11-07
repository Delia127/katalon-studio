package com.kms.katalon.composer.webservice.view;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.wsdl.WSDLException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CustomTitleAreaDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.parser.WSDLParserUtil;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.tracking.service.Trackings;

public class ImportWebServiceObjectsFromWSDLDialog  extends CustomTitleAreaDialog {

    private List<WebServiceRequestEntity> soapWebServiceRequestEntities;
    private List<WebServiceRequestEntity> soap12WebServiceRequestEntities;
    private String directory = "";
    
    public ImportWebServiceObjectsFromWSDLDialog(Shell parentShell) {
        super(parentShell);
        Trackings.trackOpenImportingWsdl();
    }
   
    private void createSoapWebServiceRequestEntities() throws InvocationTargetException, InterruptedException, WSDLException{
    	soapWebServiceRequestEntities = WSDLParserUtil.newWSTestObjectsFromWSDL(WebServiceRequestEntity.SOAP, directory); 
    }
    
    private void createSoap12WebServiceRequestEntities() throws InvocationTargetException, InterruptedException, WSDLException{
    	soap12WebServiceRequestEntities = WSDLParserUtil.newWSTestObjectsFromWSDL(WebServiceRequestEntity.SOAP12, directory); 
    	
    }
    
    private void createWebServiceRequestEntities() throws InvocationTargetException, InterruptedException, WSDLException{
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

	@SuppressWarnings("static-access")
	@Override
    protected void okPressed() {
    	Button ok = getButton(IDialogConstants.OK_ID);
    	boolean closeTheDialog = true;
    	try{
        	createWebServiceRequestEntities();
    	} catch(Exception ex){
    		closeTheDialog = false;
    		setMessage(StringConstants.EXC_INVALID_WSDL_FILE, IMessageProvider.ERROR);


    		if(ex instanceof WSDLException){
    			LoggerSingleton.getInstance().logError("Method getOperationNamesByRequestMethod has thrown WSDLException: " + ex.getCause());
    		}
    		if(ex instanceof InterruptedException){
    			LoggerSingleton.getInstance().logError("Method generateInputSOAPMessageText in runnable thread was interrupted:" + ex.getCause());
    		}
    		if(ex instanceof InvocationTargetException){
    			 Throwable cause = ex.getCause();
    		        if(cause == null) {
    		            throw new IllegalStateException(
    		            		  "Got InvocationTargetException, but the cause is null.", ex);
    		        } else if(cause instanceof RuntimeException) {
    		            throw (RuntimeException) cause;
    		        } else if(cause instanceof Exception) {
    		        	LoggerSingleton.getInstance().logError("Invocation failed with cause: " + cause);
    		        } else {
    		        	LoggerSingleton.getInstance().logError("Invocation failed with error: " + cause);

    		        }
    		}

    		ok.setEnabled(false);

    	} finally {
    		if(closeTheDialog == true){
    	        super.okPressed();
    		}
    	}
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



	@Override
	protected Composite createContentArea(Composite parent) {

        setDialogTitle(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ_WSDL);
        setMessage(StringConstants.DIA_MSG_IMPORT_WEBSERVICE_REQ_WSDL, IMessageProvider.INFORMATION);

        // create a composite with standard margins and spacing
        Composite composite = new Composite(parent, SWT.NONE);
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
        
        Button button = new Button(methodComposite, SWT.PUSH);
        button.setText(StringConstants.BROWSE);
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e) {
            	FileDialog directoryDialog = new FileDialog(getParentShell());
                String filePath = directoryDialog.open();          
                text.setText(filePath);
                directory = filePath;
            }
        });
        
        
        
        ModifyListener listener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	Button ok = getButton(IDialogConstants.OK_ID);
            	if(ok.isEnabled() == false){
            		ok.setEnabled(true);
            	}
            	directory = ((Text) e.widget).getText();
            }
          };
        text.addModifyListener(listener);
		messageLabel.addSelectionListener(new SelectionAdapter(){
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        Program.launch("https://www.w3.org/TR/wsdl/");
		    }
		});
		return composite;
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
    protected Point getInitialSize() {
    	final Point size = super.getInitialSize();
        size.x = convertWidthInCharsToPixels(75);
        return size;
    }
    
    public String getWSDLSpecLocation() {
        return directory;
    }
}
