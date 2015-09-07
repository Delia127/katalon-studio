package com.kms.katalon.composer.webservice.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.support.PropertyNameEditingSupport;
import com.kms.katalon.composer.webservice.support.PropertyValueEditingSupport;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.composer.webservice.view.ParameterTable;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public abstract class RequestObjectPart {

	protected MPart mpart;
	
	protected WebServiceRequestEntity originalObject;
	
	protected Composite mainComposite;
	
	//@Inject
    //private EventBroker eventBroker;
	
	protected ModifyListener modifyListener;
	
	protected Text txtHttpBody, txtID, txtDesc, txtName;
	protected ParameterTable tblHttpHeader;

	protected List<WebElementPropertyEntity> listHttpHeaderProps = new ArrayList<WebElementPropertyEntity>();

	 @Inject
	 protected MDirtyable dirtyable;
	
	public void createComposite(Composite parent, MPart part) {
		this.mpart = part;
		this.originalObject = (WebServiceRequestEntity) part.getObject();
		
		parent.setLayout(new FillLayout());
		
		mainComposite = new Composite(parent, SWT.NULL);
		GridLayout glMainComposite = new GridLayout(1, false);
		mainComposite.setLayout(glMainComposite);
		
		createModifyListener();
		
		//Init UI
		createEntityInfoComposite(mainComposite);
		createHttpComposite(mainComposite);
		createServiceInfoComposite(mainComposite);
		
		showEntityFieldsToUi();
		
		dirtyable.setDirty(false);

	}
	
	private void createEntityInfoComposite(Composite mainComposite){
		ExpandableComposite entityComposite = new ExpandableComposite(mainComposite, StringConstants.PA_TITLE_INFO, 1, true);
        Composite compositeDetails = entityComposite.createControl();
        
		Composite entityContainerComposite = new Composite(compositeDetails, SWT.NONE);
		entityContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		entityContainerComposite.setLayout(new GridLayout(4, false));
		
		GridData gdData;

		Label lblID = new Label(entityContainerComposite, SWT.NONE);
		lblID.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblID.setText(StringConstants.PA_LBL_ID);

		txtID = new Text(entityContainerComposite, SWT.BORDER);
		gdData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdData.heightHint = 20;
		txtID.setLayoutData(gdData);
		txtID.setEditable(false);
		txtID.addModifyListener(modifyListener);

		Label lblDesc = new Label(entityContainerComposite, SWT.NONE);
		lblDesc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblDesc.setText(StringConstants.PA_LBL_DESC);

		txtDesc = new Text(entityContainerComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gdData.heightHint = 45;
		txtDesc.setLayoutData(gdData);
		txtDesc.addModifyListener(modifyListener);
		
		Label lblName = new Label(entityContainerComposite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblName.setText(StringConstants.PA_LBL_NAME);

		txtName = new Text(entityContainerComposite, SWT.BORDER);
		gdData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdData.heightHint = 20;
		txtName.setLayoutData(gdData);
		txtName.addModifyListener(modifyListener);
		
	}
	
	private void createHttpComposite(Composite mainComposite){
    	
    	ExpandableComposite soapComposite = new ExpandableComposite(mainComposite, StringConstants.PA_TITLE_HTTP, 1, true);
        Composite compositeDetails = soapComposite.createControl();
        
		Composite httpContainerComposite = new Composite(compositeDetails, SWT.NONE);
		httpContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		httpContainerComposite.setLayout(new GridLayout(2, false));
		
		GridData gdData;

        //HTTP Header
		Label lblHttpHeader = new Label(httpContainerComposite, SWT.NONE);
		lblHttpHeader.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblHttpHeader.setText(StringConstants.PA_LBL_HTTP_HEADER);

		tblHttpHeader = createParamsTable(httpContainerComposite);
		tblHttpHeader.setInput(listHttpHeaderProps);		

		Label lblSupporter = new Label(httpContainerComposite, SWT.NONE);
		lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		
		//HTTP Body
		Label lblSoapBody = new Label(httpContainerComposite, SWT.NONE);
		lblSoapBody.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		lblSoapBody.setText(StringConstants.PA_LBL_HTTP_BODY);

		txtHttpBody = new Text(httpContainerComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI); 
		gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gdData.heightHint = 45;
		txtHttpBody.setLayoutData(gdData);
		txtHttpBody.addModifyListener(modifyListener);

		lblSupporter = new Label(httpContainerComposite, SWT.NONE);
		lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
    	
    }
    
	protected abstract void createServiceInfoComposite(Composite mainComposite);
	
	protected ParameterTable createParamsTable(Composite containerComposite) {
		Composite compositeTableDetails = new Composite(containerComposite, SWT.NONE);
		GridLayout glCompositeTableDetails = new GridLayout(1, false);
		glCompositeTableDetails.marginWidth = 0;
		glCompositeTableDetails.marginHeight = 0;
		compositeTableDetails.setLayout(glCompositeTableDetails);
		GridData gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gdData.heightHint = 100;
		compositeTableDetails.setLayoutData(gdData);
	
		ParameterTable tblProperties = new ParameterTable(compositeTableDetails, 
				SWT.BORDER | SWT.FULL_SELECTION, dirtyable);
		tblProperties.createTableEditor();
	
		Table table = tblProperties.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		gridDataTable.horizontalSpan = 3;
		gridDataTable.heightHint = 150;
		table.setLayoutData(gridDataTable);
	
		TableViewerColumn treeViewerColumnName = new TableViewerColumn(tblProperties, SWT.NONE);
		TableColumn trclmnColumnName = treeViewerColumnName.getColumn();
		trclmnColumnName.setText(ParameterTable.columnNames[0]);
		trclmnColumnName.setWidth(200);
		treeViewerColumnName.setEditingSupport(new PropertyNameEditingSupport(tblProperties, dirtyable));
		treeViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((WebElementPropertyEntity) element).getName();
			}
		});
	
		TableViewerColumn treeViewerColumnValue = new TableViewerColumn(tblProperties, SWT.NONE);
		TableColumn trclmnColumnValue = treeViewerColumnValue.getColumn();
		trclmnColumnValue.setText(ParameterTable.columnNames[1]);
		trclmnColumnValue.setWidth(400);
		treeViewerColumnValue.setEditingSupport(new PropertyValueEditingSupport(tblProperties, dirtyable));
		treeViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((WebElementPropertyEntity) element).getValue();
			}
		});
	
		tblProperties.setContentProvider(ArrayContentProvider.getInstance());
				
		return tblProperties;
	}

	private void createModifyListener() {
        modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                dirtyable.setDirty(true);
            }
        };
    }
	
	/**
	 * Update entity fields before saved, 
	 * Child class should override this method and do some more updates for it own properties
	 */
	protected void updateEntityBeforeSaved(){
		//Update object properties
		originalObject.setName(txtName.getText());
		originalObject.setDescription(txtDesc.getText());
		
		originalObject.setHttpBody(txtHttpBody.getText());
		
		tblHttpHeader.removeEmptyProperty();
		originalObject.setHttpHeaderProperties(tblHttpHeader.getInput());
	}
	
	protected void showEntityFieldsToUi(){
		String dispID = "";
		try{
			dispID = ObjectRepositoryController.getInstance().getIdForDisplay(originalObject);
		}
		catch(Exception ex){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, ex.getMessage());
		}
		txtID.setText(dispID);
		txtName.setText(originalObject.getName());
		txtDesc.setText(originalObject.getDescription());
		
		txtHttpBody.setText(originalObject.getHttpBody());
		
		listHttpHeaderProps.addAll(originalObject.getHttpHeaderProperties());		
		tblHttpHeader.refresh();
	}

	protected void save() {
    	try{
    		updateEntityBeforeSaved();
        	ObjectRepositoryController.getInstance().saveWebElement(originalObject);
        	dirtyable.setDirty(false);    		
    	}
    	catch (Exception e1) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, e1.getMessage());
		}
    }    
}
