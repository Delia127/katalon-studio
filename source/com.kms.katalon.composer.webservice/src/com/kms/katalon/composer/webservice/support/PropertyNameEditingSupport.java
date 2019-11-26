package com.kms.katalon.composer.webservice.support;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.adapter.CComboContentAdapter;
import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.webservice.constants.HttpHeaderConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyNameEditingSupport extends EditingSupport {

    private TableViewer viewer;

    private MDirtyable dirtyable;

    private boolean isHeaderField = false;

    public PropertyNameEditingSupport(TableViewer viewer, MDirtyable dirtyable) {
        super(viewer);
        this.viewer = viewer;
        this.dirtyable = dirtyable;
    }

    public PropertyNameEditingSupport(TableViewer viewer, MDirtyable dirtyable, boolean isHeaderField) {
        super(viewer);
        this.viewer = viewer;
        this.dirtyable = dirtyable;
        this.isHeaderField = isHeaderField;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (isHeaderField) {
            return new HttpHeaderNameCellEditor(element, HttpHeaderConstants.PRE_DEFINDED_HTTP_HEADER_FIELD_NAMES);
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
            return property.getName();
        }
        return "";
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof String) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            if (!value.equals(property.getName())) {
                property.setName((String) value);
                if (this.dirtyable != null) this.dirtyable.setDirty(true);
                this.viewer.update(element, null);
            }
        }
    }
    
    private class HttpHeaderNameCellEditor extends StringComboBoxCellEditor {
        
        private Object element;

        public HttpHeaderNameCellEditor(Object element, String[] items) {
            super(viewer.getTable(), items);
            this.element = element;

            CCombo combo = (CCombo) getControl();
            combo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    String text = combo.getText();
                    PropertyNameEditingSupport.this.setValue(element, text);
                }
            });
            combo.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    String text = combo.getText();
                    PropertyNameEditingSupport.this.setValue(element, text);
                }
            });
        }
        
        @Override
        public AutoCompleteField getAutoCompleteField(String[] newItems) {
            return  new AutoCompleteField(getControl(), new HeaderNameComboContentAdapter(), newItems); 
        }

        private class HeaderNameComboContentAdapter extends CComboContentAdapter {
            @Override
            public void setControlContents(Control control, String text,
                    int cursorPosition) {
                super.setControlContents(control, text, cursorPosition);
                PropertyNameEditingSupport.this.setValue(element, text);
                
            }
        }
    }
}