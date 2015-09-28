package com.kms.katalon.composer.webservice.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class ParameterTable extends TableViewer {
	
	public static final String[] columnNames = new String[] { StringConstants.VIEW_COL_NAME, StringConstants.VIEW_COL_VALUE };
	
	private List<WebElementPropertyEntity> data;
	
	private MDirtyable dirtyable;

	public ParameterTable(Composite parent, int style, MDirtyable dirtyable) {
        super(parent, style);
        this.dirtyable = dirtyable;
    }
	    
    public void setInput(List<WebElementPropertyEntity> data) {
        this.data = data;
        super.setInput(data);
    }
    
    public void addRow(WebElementPropertyEntity property) {
        data.add(property);
        this.update(property, null);
        this.getTable().select(data.size() - 1);
        this.refresh();
        if(this.dirtyable != null){
        	this.dirtyable.setDirty(true);
        }
    }

    public void addRow(WebElementPropertyEntity property, int index) {
        data.add(index+1, property);
        this.update(property, null);
        this.getTable().select(data.size() - 1);
        this.refresh();
        if(this.dirtyable != null){
        	this.dirtyable.setDirty(true);
        }
    }

    public void deleteRows(List<WebElementPropertyEntity> properties) {
        data.removeAll(properties);
        this.refresh();
        if(this.dirtyable != null){
        	this.dirtyable.setDirty(true);
        }
    }
    
    public List<WebElementPropertyEntity> getInput() {
    	if(data == null){
    		data = new ArrayList<WebElementPropertyEntity>();
    	}
        return data;
    }
    
    public void removeEmptyProperty(){
    	List<WebElementPropertyEntity> excluded = new ArrayList<WebElementPropertyEntity>();
		for(WebElementPropertyEntity prop : data){
			if(prop.getName() == null || prop.getName().equals("")){
				excluded.add(prop);
			}
		}
		data.removeAll(excluded);
		this.refresh();
    }
    
    public void createTableEditor(){
    	
    	final Table table = this.getTable();
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		
		table.addListener(SWT.MenuDetect, new Listener() {
			
			public void handleEvent(Event event) {
				createContextMenu();
			}
		});

	}
    
    private Menu createContextMenu(){
		final Table table = this.getTable();
		Menu menu = table.getMenu();
		if (menu != null) 
			menu.dispose();
		menu = new Menu(table);

        MenuItem menuItem = null;
		
		menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(StringConstants.VIEW_MENU_CONTEXT_INSERT_PROP);
		menuItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				WebElementPropertyEntity newProp = new WebElementPropertyEntity();
				newProp.setName("");
				newProp.setValue("");
				newProp.setMatchCondition("equals");
				newProp.setIsSelected(true);
				TableItem[] items = table.getSelection();
				if(items.length > 0){					
					addRow(newProp, table.indexOf(items[0]));
				}
				else{
					addRow(newProp);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		if(table.getItemCount() > 1){
			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(StringConstants.VIEW_MENU_CONTEXT_DEL_PROPS);
			menuItem.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int[] indices = table.getSelectionIndices();
					List<WebElementPropertyEntity> removedList = new ArrayList<WebElementPropertyEntity>();
					for(int index : indices){
						removedList.add(data.get(index));
					}
					deleteRows(removedList);
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});			
		}
		
		table.setMenu(menu);
		
		return menu;		
	}
}
