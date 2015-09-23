package com.kms.katalon.composer.testsuite.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.editors.DataIterationCellEditor;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.entity.link.IterationEntity;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataIterationColumnEditingSupport extends EditingSupport {

    private TestSuitePartDataBindingView mpart;

    public TestDataIterationColumnEditingSupport(ColumnViewer viewer, TestSuitePartDataBindingView mpart) {
        super(viewer);
        this.mpart = mpart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element != null && element instanceof TestDataLinkTreeNode) {
            TestDataLinkTreeNode linkTreeNode = (TestDataLinkTreeNode) element;
            TestCaseTestDataLink testDataLink = linkTreeNode.getTestDataLink();
            if (testDataLink.getIterationEntity() != null) {
                return new DataIterationCellEditor((Composite) getViewer().getControl(), testDataLink
                        .getIterationEntity().clone());
            }
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element != null && element instanceof TestDataLinkTreeNode) {
            TestDataLinkTreeNode linkTreeNode = (TestDataLinkTreeNode) element;
            TestCaseTestDataLink testDataLink = linkTreeNode.getTestDataLink();

            if (testDataLink.getIterationEntity() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof TestDataLinkTreeNode) {
            TestDataLinkTreeNode linkTreeNode = (TestDataLinkTreeNode) element;
            TestCaseTestDataLink testDataLink = linkTreeNode.getTestDataLink();

            if (testDataLink.getIterationEntity() != null) {
                switch (testDataLink.getIterationEntity().getIterationType()) {
                    case ALL:
                        return testDataLink.getIterationEntity().getIterationType().name();
                    default:
                        return testDataLink.getIterationEntity().getValue();
                }

            }
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof TestDataLinkTreeNode && value != null
                && value instanceof IterationEntity) {
            TestDataLinkTreeNode linkTreeNode = (TestDataLinkTreeNode) element;
            TestCaseTestDataLink testDataLink = linkTreeNode.getTestDataLink();

            if (!testDataLink.getIterationEntity().equals(value)) {
                testDataLink.setIterationEntity((IterationEntity) value);

                getViewer().update(element, null);
                mpart.setDirty(true);
            }
        }
    }

}
