package com.kms.katalon.composer.testsuite.collection.part.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.collection.part.editor.TestSuiteSelectionCellEditor;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteIdEditingSupport extends EditingSupportWithTableProvider {

    public TestSuiteIdEditingSupport(TableViewerProvider provider) {
        super(provider);
    }

    @Override
    protected CellEditor getCellEditorByElement(TestSuiteRunConfiguration element) {
        return new TestSuiteSelectionCellEditor((Composite) getViewer().getControl(), element.getTestSuiteEntity());
    }

    @Override
    protected boolean canEditElement(TestSuiteRunConfiguration element) {
        return true;
    }

    @Override
    protected Object getElementValue(TestSuiteRunConfiguration element) {
        if (element instanceof TestSuiteRunConfiguration) {
            return ((TestSuiteRunConfiguration)element).getTestSuiteEntity();
        }
        return null;
    }

    @Override
    protected void setElementValue(TestSuiteRunConfiguration element, Object value) {
        if (!(value instanceof TestSuiteEntity)) {
            return;
        }

        TestSuiteEntity newTestSuite = (TestSuiteEntity) value;
        if (value.equals(element.getTestSuiteEntity())) {
            return;
        }

        if (provider.containsTestSuite(newTestSuite)) {
            return;
        }

        element.setTestSuiteEntity(newTestSuite);
        refreshElementAndMarkDirty(element);
    }

}
