package com.kms.katalon.composer.objectrepository.support;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.provider.ObjectPropetiesTableViewer;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertySelectedEditingSupport extends EditingSupport {

    private ObjectPropetiesTableViewer viewer;
    private IEventBroker eventBroker;

    public PropertySelectedEditingSupport(ObjectPropetiesTableViewer viewer, IEventBroker eventBroker) {
        super(viewer);
        this.viewer = viewer;
        this.eventBroker = eventBroker;
    }
    
    @Override
    protected CellEditor getCellEditor(Object element) {
        return new CheckboxCellEditor(viewer.getTable());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            return property.getIsSelected();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof Boolean) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            if (!value.equals(property.getIsSelected())) {
                property.setIsSelected((Boolean) value);
                viewer.refreshIsSelected();
                viewer.update(element, null);
                eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this.viewer);
            }
        }
        
    }

}
