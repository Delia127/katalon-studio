package com.katalon.plugin.smart_xpath.settings.composites;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.adapter.CComboContentAdapter;
import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;

public class ExcludeKeywordsEditingSupport extends EditingSupport {

    private TableViewer viewer;

    private String[] keywordList;

    public ExcludeKeywordsEditingSupport(TableViewer viewer, String[] keywordList) {
        super(viewer);
        this.viewer = viewer;
        this.keywordList = keywordList;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        final StringComboBoxCellEditor editor = new ExcludeKeywordsCellEditor(element, keywordList);
        return editor;
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof String) {
            String keyword = (String) element;
            return keyword;
        }
        return "";
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof String && value instanceof String) {
            String property = (String) element;
            if (!value.equals(property)) {
                this.viewer.update(element, null);
            }
        }
    }

    private class ExcludeKeywordsCellEditor extends StringComboBoxCellEditor {

        private Object element;

        public ExcludeKeywordsCellEditor(Object element, String[] items) {
            super(viewer.getTable(), items);
            this.element = element;

            CCombo combo = (CCombo) getControl();
            combo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    String text = combo.getText();
                    ExcludeKeywordsEditingSupport.this.setValue(element, text);
                }
            });
            combo.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    String text = combo.getText();
                    ExcludeKeywordsEditingSupport.this.setValue(element, text);
                }
            });
        }

        @Override
        public AutoCompleteField getAutoCompleteField(String[] newItems) {
            return new AutoCompleteField(getControl(), new ExcludeKeywordsComboContentAdapter(), newItems);
        }

        private class ExcludeKeywordsComboContentAdapter extends CComboContentAdapter {
            @Override
            public void setControlContents(Control control, String text, int cursorPosition) {
                super.setControlContents(control, text, cursorPosition);
                ExcludeKeywordsEditingSupport.this.setValue(element, text);
            }
        }
    }
}
