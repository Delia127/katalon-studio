package com.kms.katalon.composer.webservice.support;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyValueEditingSupport extends EditingSupport {

    private TableViewer viewer;
    
    private MDirtyable dirtyable;
    
    public PropertyValueEditingSupport(TableViewer viewer, MDirtyable dirtyable) {
        super(viewer);
        this.viewer = viewer;
        this.dirtyable = dirtyable;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor(viewer.getTable());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            return property.getValue();
        }
        return "";
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof String) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            if (!value.equals(property.getValue())) {
                property.setValue((String) value);
                this.viewer.update(element, null);
                
                dirtyable.setDirty(true);
            }
        }
        
    }

}
