package com.kms.katalon.composer.testsuite.collection.part.support;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.collection.part.editor.RunConfigurationSelectionCellEditor;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class RunConfigurationChooserEditingSupport extends EditingSupportWithTableProvider {

    public RunConfigurationChooserEditingSupport(TableViewerProvider provider) {
        super(provider);
    }

    @Override
    protected CellEditor getCellEditorByElement(TestSuiteRunConfiguration element) {
        return new RunConfigurationSelectionCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected boolean canEditElement(TestSuiteRunConfiguration element) {
        return true;
    }

    @Override
    protected Object getElementValue(TestSuiteRunConfiguration element) {
        return element.getConfiguration();
    }

    @Override
    protected void setElementValue(TestSuiteRunConfiguration element, Object value) {
        if (!(value instanceof RunConfigurationDescription)) {
            return;
        }
        RunConfigurationDescription newValue = (RunConfigurationDescription) value;
        if (ObjectUtils.equals(element.getConfiguration(), newValue)) {
            return;
        }
        element.setConfiguration(newValue);
        refreshElementAndMarkDirty(element);
    }

}
