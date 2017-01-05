package com.kms.katalon.composer.webservice.support;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.webservice.constants.HttpHeaderConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyValueEditingSupport extends EditingSupport {

    private TableViewer viewer;

    private MDirtyable dirtyable;

    private boolean isHeaderField = false;

    public PropertyValueEditingSupport(TableViewer viewer, MDirtyable dirtyable) {
        this(viewer, dirtyable, false);
    }

    public PropertyValueEditingSupport(TableViewer viewer, MDirtyable dirtyable, boolean isHeaderField) {
        super(viewer);
        this.viewer = viewer;
        this.dirtyable = dirtyable;
        this.isHeaderField = isHeaderField;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (isHeaderField) {
            return new StringComboBoxCellEditor(viewer.getTable(),
                    HttpHeaderConstants.PRE_DEFINDED_HTTP_HEADER_FIELD_VALUES);
        }
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
        if (element != null && element instanceof WebElementPropertyEntity && value != null
                && value instanceof String) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            if (!value.equals(property.getValue())) {
                property.setValue((String) value);
                this.viewer.update(element, null);
                dirtyable.setDirty(true);
            }
        }
    }

}
