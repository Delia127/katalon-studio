package com.kms.katalon.composer.objectrepository.support;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyConditionEditingSupport extends EditingSupport{

    private TableViewer viewer;
    private IEventBroker eventBroker;
    
    public PropertyConditionEditingSupport(TableViewer viewer, IEventBroker eventBroker) {
        super(viewer);
        this.viewer = viewer;
        this.eventBroker = eventBroker;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            return new ComboBoxCellEditor(viewer.getTable(), WebElementPropertyEntity.MATCH_CONDITION.getTextVlues(), SWT.NONE);
        } else {
            return null;
        }
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            WebElementPropertyEntity webProperty = (WebElementPropertyEntity)element;
            return WebElementPropertyEntity.MATCH_CONDITION.indexOf(webProperty.getMatchCondition());
        }
        return -1;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof Integer) {
            WebElementPropertyEntity webProperty = (WebElementPropertyEntity) element;
            if (((Integer) value) < 0) return;
            String newValue = WebElementPropertyEntity.MATCH_CONDITION.values()[(Integer)value].getText();
            if (!newValue.equals(webProperty.getMatchCondition())) {
                webProperty.setMatchCondition(newValue);
                this.viewer.update(element, null);
                eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, this.viewer);
            }
        }
        
    }

}
