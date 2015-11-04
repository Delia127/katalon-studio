package com.kms.katalon.composer.objectrepository.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyNameEditingSupport extends EditingSupport{

    private TableViewer viewer;
    private IEventBroker eventBroker;

    public PropertyNameEditingSupport(TableViewer viewer, IEventBroker eventBroker) {
        super(viewer);
        this.viewer = viewer;
        this.eventBroker = eventBroker;
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
            return property.getName();
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof String) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            if (!value.equals(property.getName())) {
                property.setName((String) value);
                this.viewer.update(element, null);
                eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this.viewer);
            }
        }
        
    }

}
