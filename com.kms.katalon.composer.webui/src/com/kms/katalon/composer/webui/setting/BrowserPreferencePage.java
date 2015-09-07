package com.kms.katalon.composer.webui.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.composer.webui.dialog.AddNewDriverPropertyDialog;
import com.kms.katalon.composer.webui.setting.table.DriverPropertyLabelProvider;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.setting.DriverProperty;
import com.kms.katalon.core.webui.setting.DriverPropertyStore;
import com.kms.katalon.entity.project.ProjectEntity;

public abstract class BrowserPreferencePage extends PreferencePage {
	
	private List<DriverProperty> driverProperties; 
	private Composite fieldEditorParent;
	private Table table;
	private TableViewer tableViewer;
	
	private ToolItem tltmAddProperty;
	private ToolItem tltmRemoveProperty;
	private ToolItem tltmClearProperty;
	private ToolItem tltmUpProperty;
	private ToolItem tltmDownProperty;
	private ToolItem tltmEditProperty;

	@Override
	protected Control createContents(Composite parent) {
		fieldEditorParent = new Composite(parent, SWT.NONE);
		fieldEditorParent.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(fieldEditorParent, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tbvColumnPropertyName = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclPropertyName = tbvColumnPropertyName.getColumn();
		tblclPropertyName.setWidth(200);
		tblclPropertyName.setText(StringConstants.SETT_COL_PREFERENCE_NAME);
		
		TableViewerColumn tbvColumnPropertyType = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tbclPropertyType = tbvColumnPropertyType.getColumn();
		tbclPropertyType.setWidth(100);
		tbclPropertyType.setText(StringConstants.SETT_COL_TYPE);
		
		TableViewerColumn tbvColumnPropertyValue = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_2 = tbvColumnPropertyValue.getColumn();
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText(StringConstants.SETT_COL_VALUE);
		
		tableViewer.setLabelProvider(new DriverPropertyLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		Composite composite_1 = new Composite(fieldEditorParent, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		
		ToolBar toolBar = new ToolBar(composite_1, SWT.FLAT | SWT.RIGHT | SWT.VERTICAL);
		
		tltmAddProperty = new ToolItem(toolBar, SWT.NONE);
		tltmAddProperty.setText(StringConstants.SETT_TOOLITEM_ADD);
		
		tltmEditProperty = new ToolItem(toolBar, SWT.NONE);
		tltmEditProperty.setText(StringConstants.SETT_TOOLITEM_EDIT);
		
		tltmRemoveProperty = new ToolItem(toolBar, SWT.NONE);
		tltmRemoveProperty.setText(StringConstants.SETT_TOOLITEM_REMOVE);
		
		tltmClearProperty = new ToolItem(toolBar, SWT.NONE);
		tltmClearProperty.setText(StringConstants.SETT_TOOLITEM_CLEAR);
		
		tltmUpProperty = new ToolItem(toolBar, SWT.NONE);
		tltmUpProperty.setText(StringConstants.SETT_TOOLITEM_UP);
		
		tltmDownProperty = new ToolItem(toolBar, SWT.NONE);
		tltmDownProperty.setText(StringConstants.SETT_TOOLITEM_DOWN);
		
		initilize();
		addToolItemListeners();
		
		return fieldEditorParent;
	}

	private void addToolItemListeners() {
		tltmAddProperty.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {			
				List<String> existingNames = new ArrayList<String>();
				for (DriverProperty property : driverProperties) {
					existingNames.add(property.getName());
				}
				
				AddNewDriverPropertyDialog dialog = new AddNewDriverPropertyDialog(tltmAddProperty.getDisplay()
						.getActiveShell(), getDriverType(), existingNames, null);
				if (dialog.open() == Dialog.OK) {
					driverProperties.add(dialog.getDriverProperty());
					tableViewer.refresh();
				}
			}
		});
		
		tltmRemoveProperty.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				driverProperties.remove(selection.getFirstElement());
				tableViewer.refresh();
			}
		});
		
		tltmClearProperty.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				driverProperties.clear();
				tableViewer.refresh();
			}
		});
		
		tltmUpProperty.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if (selection == null || selection.getFirstElement() == null) return;
				DriverProperty property = (DriverProperty) selection.getFirstElement();
				int index = driverProperties.indexOf(property);
				
				if (index > 0) {
					Collections.swap(driverProperties, index, index - 1);
					tableViewer.refresh();
				}
			}
		});
		
		tltmDownProperty.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if (selection == null || selection.getFirstElement() == null) return;
				DriverProperty property = (DriverProperty) selection.getFirstElement();
				int index = driverProperties.indexOf(property);
				
				if (index >= 0) {
					Collections.swap(driverProperties, index, index + 1);
					tableViewer.refresh();
				}
			}
		});
		
		tltmEditProperty.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if (selection == null || selection.getFirstElement() == null) return;
				
				DriverProperty selectedProperty = (DriverProperty) selection.getFirstElement();
				
				List<String> existingNames = new ArrayList<String>();
				for (DriverProperty property : driverProperties) {
					if (!property.getName().equals(selectedProperty.getName())) {
						existingNames.add(property.getName());
					}
				}
				
				AddNewDriverPropertyDialog dialog = new AddNewDriverPropertyDialog(tltmAddProperty.getDisplay()
						.getActiveShell(), getDriverType(), existingNames, selectedProperty);
				if (dialog.open() == Dialog.OK) {
					DriverProperty returnValue = dialog.getDriverProperty();
					selectedProperty.setName(returnValue.getName());
					selectedProperty.setValue(returnValue.getValue());
					tableViewer.update(selectedProperty, null);
					
				}
			}
		});
		
	}
	
	protected abstract DriverType getDriverType();

	private void initilize() {
		ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
		if (projectEntity == null) {
			fieldEditorParent.setVisible(false);
			return;
		}		
		
		driverProperties = DriverPropertyStore.getProperties(getDriverType(),
				projectEntity.getFolderLocation());
		
		tableViewer.setInput(driverProperties);		
	}
	
	@Override
	public boolean performOk() {
		ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
		try {
			if (projectEntity == null || driverProperties == null)
				return true;
			DriverPropertyStore.saveProperties(getDriverType(), driverProperties, projectEntity.getFolderLocation());
			return true;
		} catch (Exception e) {
			MessageDialog.openError(null, StringConstants.ERROR_TITLE, 
					StringConstants.SETT_ERROR_MSG_UNABLE_TO_SAVE_PROJ_SETTS);
			return false;
		}
	}
	
	@Override 
	protected void performDefaults() {
		initilize();
		super.performDefaults();
	}
	
}
