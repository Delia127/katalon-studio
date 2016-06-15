package com.kms.katalon.composer.testsuite.collection.part.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class RunEnabledEditingSupport extends EditingSupportWithTableProvider {
    
    public RunEnabledEditingSupport(TableViewerProvider provider) {
        super(provider);
    }
    
    @Override
    protected CellEditor getCellEditorByElement(TestSuiteRunConfiguration element) {
        return new CheckboxCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected boolean canEditElement(TestSuiteRunConfiguration element) {
        return true;
    }

    @Override
    protected Object getElementValue(TestSuiteRunConfiguration element) {
        return element.isRunEnabled();
    }

    @Override
    protected void setElementValue(TestSuiteRunConfiguration element, Object value) {
        if (!(value instanceof Boolean)) {
            return;
        }
        
        boolean newValue = (boolean) value;
        if (element.isRunEnabled() != newValue) {
            element.setRunEnabled(newValue);
            provider.updateRunColumn();
            refreshElementAndMarkDirty(element);
        }
    }

}
