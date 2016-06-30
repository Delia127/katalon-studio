package com.kms.katalon.composer.testsuite.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.testsuite.editors.DataIterationCellEditor;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.entity.link.IterationEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataIterationColumnEditingSupport extends TypeCheckedEditingSupport<TestCaseTestDataLink> {

    private TestSuitePartDataBindingView mpart;

    public TestDataIterationColumnEditingSupport(ColumnViewer viewer, TestSuitePartDataBindingView mpart) {
        super(viewer);
        this.mpart = mpart;
    }

    @Override
    protected Class<TestCaseTestDataLink> getElementType() {
        return TestCaseTestDataLink.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(TestCaseTestDataLink element) {
        return new DataIterationCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected boolean canEditElement(TestCaseTestDataLink element) {
        return element.getIterationEntity() != null;
    }

    @Override
    protected Object getElementValue(TestCaseTestDataLink element) {
        return element.getIterationEntity();
    }

    @Override
    protected void setElementValue(TestCaseTestDataLink element, Object value) {
        if (value instanceof IterationEntity && !value.equals(element.getIterationEntity())) {
            element.setIterationEntity((IterationEntity) value);

            getViewer().update(element, null);
            mpart.setDirty(true);
        }
    }

}
