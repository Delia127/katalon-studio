package com.kms.katalon.composer.webservice.view;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;

public class ApiQuickStartDialog extends Dialog {

    private static final Point ITEM_IMG_SIZE = new Point(64, 64);
    private ITreeEntity parentTreeEntity;

    public ApiQuickStartDialog(ITreeEntity parentTreeEntity, Shell parentShell) {
        super(parentShell);
        this.parentTreeEntity = parentTreeEntity;

    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 400;
        body.setLayoutData(gdBody);
        body.setLayout(new GridLayout(2, false));

        createNewRestRequestItem(body);
        createNewSoapRequestItem(body);
        createImportRestRequestItem(body);
        createImportSoapRequestItem(body);

        return super.createDialogArea(parent);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.TITLE_QUICKSTART);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
    }

    private Composite createNewRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_NEW_REST_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_NEW_REST_REQUEST, e -> {
            createNewRestRequest();
        });
        return item;
    }

    private Composite createNewSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_NEW_SOAP_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_NEW_SOAP_REQUEST, e -> {
            createNewSoapRequest();
        });
        return item;
    }

    private Composite createImportRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_IMPORT_REST_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_IMPORT_SWAGGER_FROM_FILE_OR_URL, e -> {
            importSwaggerFromFileOrUrl();
        });
        return item;
    }

    private Composite createImportSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_IMPORT_SOAP_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_IMPORT_WSDL_FROM_URL, e -> {
        	importWsdlFromUrl();
        });
        return item;
    }

    private Composite createQuickStartItem(Composite parent, Image image) {
        Composite item = new Composite(parent, SWT.NONE);
        item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout glItem = new GridLayout(1, false);
        glItem.marginTop = 10;
        item.setLayout(glItem);
        item.setBackground(parent.getBackground());

        Composite imgComposite = new Composite(item, SWT.NONE);
        GridData gdImg = new GridData(SWT.CENTER, SWT.FILL, true, true);
        gdImg.widthHint = ITEM_IMG_SIZE.x;
        gdImg.heightHint = ITEM_IMG_SIZE.y;
        imgComposite.setLayoutData(gdImg);
        GridLayout glImg = new GridLayout(1, false);
        imgComposite.setLayout(glImg);
        imgComposite.addPaintListener(e -> {
            e.gc.drawImage(image, 0, 0);
        });

        return item;
    }

    private void addLinkTextToQuickStartItem(Composite item, String text, Listener selectionListener) {
        Color textColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);

        CLabel lblItemText = new CLabel(item, SWT.NONE);
        lblItemText.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
        lblItemText.setText(text);
        lblItemText.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        lblItemText.setForeground(textColor);
        lblItemText.setBackground(item.getBackground());
        lblItemText.addListener(SWT.MouseDown, selectionListener);
    }

    private void createNewRestRequest() {
        DraftWebServiceRequestEntity entity = ObjectRepositoryController.getInstance()
                .newDraftWebServiceEntity(ProjectController.getInstance().getCurrentProject());
        entity.setServiceType(DraftWebServiceRequestEntity.RESTFUL);
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_OPEN_DRAFT_WEBSERVICE, entity);
        close();
    }

    private void createNewSoapRequest() {
        DraftWebServiceRequestEntity entity = ObjectRepositoryController.getInstance()
                .newDraftWebServiceEntity(ProjectController.getInstance().getCurrentProject());
        entity.setServiceType(DraftWebServiceRequestEntity.SOAP);
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_OPEN_DRAFT_WEBSERVICE, entity);
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
}
