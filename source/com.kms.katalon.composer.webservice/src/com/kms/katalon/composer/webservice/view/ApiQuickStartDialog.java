package com.kms.katalon.composer.webservice.view;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.tracking.service.Trackings;

public class ApiQuickStartDialog extends Dialog {

    private static final Color RIGHT_PART_BACKGROUND_COLOR = ColorUtil.getColor("#F7F7F7");
    
    private static final Point DIALOG_SIZE = new Point(800, 699);
    
    private static final Point LEFT_PART_SIZE = new Point(468, 699);
    
    private static final Point QUICKSTART_ITEM_SIZE = new Point(200, 47);

    private ITreeEntity parentTreeEntity;

    private ProjectType projectType;

    public ApiQuickStartDialog(ITreeEntity parentTreeEntity, Shell parentShell, ProjectType projectType) {
        super(parentShell);
        this.parentTreeEntity = parentTreeEntity;
        this.projectType = projectType;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        
        Composite body = new Composite(parent, SWT.NONE);
       
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = DIALOG_SIZE.x;
        gdBody.heightHint = DIALOG_SIZE.y;
        body.setLayoutData(gdBody);
        GridLayout glBody = new GridLayout(2, false);
        glBody.marginWidth = 0;
        glBody.marginHeight = 0;
        glBody.horizontalSpacing = 0;
        glBody.verticalSpacing = 0;
        body.setLayout(glBody);
       
        switch(projectType){
        case WEBSERVICE :{
            createLeftPart(body);
            createRightPart(body);
            break;
        }
        case WEB : {
            createLeftPart(body);
            break;
        }
        case MOBILE :{
            createLeftPart(body);
            break;
        }
           default: {
               break;
           }
        }
      
        
        
        return super.createDialogArea(parent);
    }
    
    public void createLeftPart(Composite parent) {
        
        ScrolledComposite c1 = new ScrolledComposite(parent, SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL|SWT.CENTER);
        c1.setExpandHorizontal(true);
        c1.setExpandVertical(true);
        switch(projectType){
        case WEBSERVICE :{
            c1.setMinSize(1000, 1900);
            break;
        }
        case WEB : {
            c1.setMinSize(900, 1700);
            break;
        }
        case MOBILE :{
            c1.setMinSize(900, 2100);
            break;
        }
           default: {
               break;
           }
        }
       
        Composite leftComposite = new Composite(c1, SWT.NONE);
        c1.setContent(leftComposite);
        GridLayout glLeft = new GridLayout(1, false);
        glLeft.marginWidth = 0;
        glLeft.marginHeight = 0;
        glLeft.marginLeft=0;
        glLeft.horizontalSpacing = 0;
        glLeft.verticalSpacing = 0;
        leftComposite.setLayout(glLeft);
        GridData gdLeft = new GridData(SWT.CENTER, SWT.FILL, false, true);
        gdLeft.widthHint = (int) (LEFT_PART_SIZE.x*2.16);
        gdLeft.heightHint = LEFT_PART_SIZE.y;
       // leftComposite.setLayoutData(gdLeft);
        c1.setLayoutData(gdLeft);
       c1.setVisible(true);
       
        
        switch(projectType){
        case WEBSERVICE :{
            Image backgroundImg = ImageConstants.API_QUICKSTART_BACKGROUND_LEFT;
            leftComposite.setBackgroundImage(backgroundImg);
            break;
        }
        case WEB : {
            Image backgroundWImg = ImageConstants.API_QUICKSTART_BACKGROUND_WEB_LEFT;
            leftComposite.setBackgroundImage(backgroundWImg);
            break;
        }
        case MOBILE :{
            Image backgroundMImg = ImageConstants.API_QUICKSTART_BACKGROUND_MOBILE_LEFT;
            leftComposite.setBackgroundImage(backgroundMImg);
            break;
        }
           default: {
               break;
           }
        }
      
    }
    
    public void createRightPart(Composite parent) {
        Composite rightComposite = new Composite(parent, SWT.NONE);
        GridLayout glRight = new GridLayout(1, false);
        glRight.marginWidth = 0;
        glRight.marginHeight = 0;
        glRight.marginLeft=0;
        glRight.marginTop = 200;
        rightComposite.setLayout(glRight);
        rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        rightComposite.setBackground(RIGHT_PART_BACKGROUND_COLOR);
        
        Composite quickStartItemComposite = new Composite(rightComposite, SWT.NONE);
        GridLayout glQuickStartItemComposite = new GridLayout(1, false);
       
        glQuickStartItemComposite.marginWidth = 0;
        glQuickStartItemComposite.marginHeight = 0;
        glQuickStartItemComposite.horizontalSpacing = 0;
        glQuickStartItemComposite.verticalSpacing = 20;
        quickStartItemComposite.setLayout(glQuickStartItemComposite);
        quickStartItemComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        quickStartItemComposite.setBackground(RIGHT_PART_BACKGROUND_COLOR);
        
        createNewRestRequestItem(quickStartItemComposite);
        createNewSoapRequestItem(quickStartItemComposite);
        createImportRestRequestItem(quickStartItemComposite);
        createImportSoapRequestItem(quickStartItemComposite);
        
        /*  Composite closeComposite = new Composite(rightComposite, SWT.NONE);
        GridLayout glClose = new GridLayout(1, false);
        glClose.marginWidth = 0;
        glClose.marginHeight = 0;
        glClose.marginRight = 20;
        glClose.marginBottom = 10;
        closeComposite.setLayout(glClose);
        closeComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        closeComposite.setBackground(RIGHT_PART_BACKGROUND_COLOR);
        
       Button btnClose = new Button(closeComposite, SWT.NONE);
        btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
        btnClose.setText(IDialogConstants.CLOSE_LABEL);
        btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ApiQuickStartDialog.this.close();
            }
        });*/
    }
    
    private Composite createQuickStartItem(Composite parent, Image image, String toolTip, Listener selectionListener) {
        CLabel lblItem = new CLabel(parent, SWT.NONE);
        GridData gdItem = new GridData(SWT.CENTER, SWT.FILL, true, false);
        gdItem.widthHint = QUICKSTART_ITEM_SIZE.x;
        gdItem.heightHint = QUICKSTART_ITEM_SIZE.y;
        lblItem.setLayoutData(gdItem);
        lblItem.setToolTipText(toolTip);
        lblItem.setBackground(RIGHT_PART_BACKGROUND_COLOR);
        
        lblItem.setBackground(image);
        lblItem.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        lblItem.addListener(SWT.MouseDown, selectionListener);
        return lblItem;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        switch(projectType){
        case WEBSERVICE :{
            shell.setBounds(30, 50, 1300, 650);
            break;
        }
        case WEB : {
            shell.setBounds(170, 50, 1035, 650);
            break;
        }
        case MOBILE :{
            shell.setBounds(170, 50, 1035, 650);
            break;
        }
           default: {
               break;
           }
        }
       
        shell.setText(StringConstants.TITLE_QUICKSTART);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    private Composite createNewRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.NEW_DRAFT_REST_REQUEST,
                StringConstants.QUICKSTART_NEW_DRAFT_REST_REQUEST, e -> createNewRestRequest());
        return item;
    }

    private Composite createNewSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.NEW_DRAFT_SOAP_REQUEST,
                StringConstants.QUICKSTART_NEW_DRAFT_SOAP_REQUEST, e -> createNewSoapRequest());
        return item;
    }

    private Composite createImportRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.IMPORT_SWAGGER,
                StringConstants.QUICKSTART_IMPORT_SWAGGER_FROM_FILE_OR_URL, e -> importSwaggerFromFileOrUrl());
        return item;
    }

    private Composite createImportSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.IMPORT_WSDL,
                StringConstants.QUICKSTART_IMPORT_WSDL_FROM_FILE_OR_URL, e -> importWsdlFromUrl());
        return item;
    }

    private void createNewRestRequest() {
        DraftWebServiceRequestEntity entity = ObjectRepositoryController.getInstance()
                .newDraftWebServiceEntity(ProjectController.getInstance().getCurrentProject());
        entity.setServiceType(DraftWebServiceRequestEntity.RESTFUL);
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_OPEN_DRAFT_WEBSERVICE, entity);
        Trackings.trackOpenDraftRequest(entity.getServiceType(), "apiQuickStart");
        close();
    }

    private void createNewSoapRequest() {
        DraftWebServiceRequestEntity entity = ObjectRepositoryController.getInstance()
                .newDraftWebServiceEntity(ProjectController.getInstance().getCurrentProject());
        entity.setServiceType(DraftWebServiceRequestEntity.SOAP);
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_OPEN_DRAFT_WEBSERVICE, entity);
        Trackings.trackOpenDraftRequest(entity.getServiceType(), "apiQuickStart");
        close();
    }

    private void importSwaggerFromFileOrUrl() {
        FolderEntity parentFolderEntity;
		try {
			parentFolderEntity = (FolderEntity) this.parentTreeEntity.getObject();
			ObjectRepositoryController toController = ObjectRepositoryController.getInstance();

	        ImportWebServiceObjectsFromSwaggerDialog dialog = new ImportWebServiceObjectsFromSwaggerDialog(Display.getCurrent().getActiveShell(), parentFolderEntity);
	        
	        if (dialog.open() == Dialog.OK) {
	        	
	        	 List<WebServiceRequestEntity> requestEntities = dialog.getWebServiceRequestEntities();
	             for(WebServiceRequestEntity entity : requestEntities){
	             	toController.saveNewTestObject(entity);
	             }
	             
	             EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
	             EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_SET_SELECTED_ITEM, parentTreeEntity);
	             close();
	        }
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
        
    }

    private void importWsdlFromUrl() {
		try {
	        FolderEntity parentFolderEntity;
			parentFolderEntity = (FolderEntity) this.parentTreeEntity.getObject();

	        ObjectRepositoryController toController = ObjectRepositoryController.getInstance();

	        ImportWebServiceObjectsFromWSDLDialog dialog = new ImportWebServiceObjectsFromWSDLDialog(Display.getCurrent().getActiveShell());
	        
	        String [] requestMethods = new String[]{WebServiceRequestEntity.SOAP, WebServiceRequestEntity.SOAP12};
	        if (dialog.open() == Dialog.OK) {
	        	for(int i = 0; i < requestMethods.length; i++){
	        		String requestMethod = requestMethods[i]; 

	            	List<WebServiceRequestEntity> soapRequestEntities = dialog.getWebServiceRequestEntities(requestMethod);
	            	if(soapRequestEntities != null && soapRequestEntities.size() > 0 ){
	                	FolderEntity folder = FolderController.getInstance().addNewFolder(parentFolderEntity, requestMethod);
	                    FolderTreeEntity newFolderTree = new FolderTreeEntity(folder, parentTreeEntity);
	                    for(WebServiceRequestEntity entity : soapRequestEntities){
	                    	entity.setElementGuidId(Util.generateGuid());
	                    	entity.setParentFolder(folder);
	                    	entity.setProject(folder.getProject());
	                    	toController.saveNewTestObject(entity);
	                    }
	            	}
	        	}
	        	EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
	            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_SET_SELECTED_ITEM, parentTreeEntity);
	            close();
	        }
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
    }
    
    @Override
    protected Point getInitialSize() {
       
        return getShell().computeSize(1300, 650,true);
        
    }
}
