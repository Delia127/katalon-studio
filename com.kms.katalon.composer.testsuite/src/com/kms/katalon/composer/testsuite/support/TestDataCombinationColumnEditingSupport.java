package com.kms.katalon.composer.testsuite.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestDataCombinationType;

public class TestDataCombinationColumnEditingSupport extends EditingSupport {
    private TestSuitePartDataBindingView mpart;

    public TestDataCombinationColumnEditingSupport(ColumnViewer viewer, TestSuitePartDataBindingView mpart) {
        super(viewer);
        this.mpart = mpart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element != null && element instanceof TestDataLinkTreeNode) {
            return new CheckboxCellEditor((Composite) getViewer().getControl());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element != null && element instanceof TestDataLinkTreeNode);
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof TestDataLinkTreeNode) {
            TestDataLinkTreeNode linkTreeNode = (TestDataLinkTreeNode) element;
            TestCaseTestDataLink link = linkTreeNode.getTestDataLink();
            return (link.getCombinationType() == TestDataCombinationType.ONE);
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        try {
            if (element != null && element instanceof TestDataLinkTreeNode && value instanceof Boolean) {

                TestDataLinkTreeNode linkTreeNode = (TestDataLinkTreeNode) element;
                TestCaseTestDataLink link = linkTreeNode.getTestDataLink();
                boolean isOneOne = (boolean) value;
                if ((link.getCombinationType() == TestDataCombinationType.ONE) != isOneOne) {
                    if (isOneOne) {
                        link.setCombinationType(TestDataCombinationType.ONE);
                    } else {
                        link.setCombinationType(TestDataCombinationType.MANY);
                    }
                    getViewer().update(element, null);
                    mpart.setDirty(true);
                }

            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

    }

}
