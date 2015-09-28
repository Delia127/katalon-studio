package com.kms.katalon.composer.execution.components;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class DriverPropertyNameEditingSupport extends EditingSupport {
    private TableViewer tableViewer;

    public DriverPropertyNameEditingSupport(TableViewer tableViewer) {
        super(tableViewer);
        this.tableViewer = tableViewer;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor(tableViewer.getTable());
    }

    @Override
    protected boolean canEdit(Object element) {
        return element instanceof Entry;
    }

    @Override
    protected Object getValue(Object element) {
        return ((Entry<?, ?>) element).getKey();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setValue(Object element, Object value) {
        if (value instanceof String && tableViewer.getInput() instanceof Map) {
            final Entry<String, Object> oldProperty = ((Entry<String, Object>) element);
            Map<String, Object> dictionary = (Map<String, Object>) tableViewer.getInput();
            Map<String, Object> tempDictionary = new LinkedHashMap<String, Object>(dictionary);

            int elementIndex = Iterables.indexOf(dictionary.entrySet(), new Predicate<Object>() {
                @Override
                public boolean apply(Object input) {
                    if (input instanceof Entry<?, ?>) {
                        Entry<?, ?> entry = ((Entry<?, ?>) input);
                        return entry.getKey().equals(oldProperty.getKey());
                    }
                    return false;
                }
            });

            tempDictionary.remove(oldProperty);
            dictionary.clear();
            int index = 0;
            for (Entry<String, Object> entry : tempDictionary.entrySet()) {
                if (index >= elementIndex) {
                    break;
                }
                dictionary.put(entry.getKey(), entry.getValue());
                index++;
            }
            dictionary.put(String.valueOf(value), oldProperty.getValue());

            index = 0;
            for (Entry<String, Object> entry : tempDictionary.entrySet()) {
                if (index <= elementIndex) {
                    index++;
                    continue;
                }
                dictionary.put(entry.getKey(), entry.getValue());
            }
            tableViewer.refresh();
        }
    }

}
