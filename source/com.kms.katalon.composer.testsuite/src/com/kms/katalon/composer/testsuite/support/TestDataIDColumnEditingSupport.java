package com.kms.katalon.composer.testsuite.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.editors.TestDataCellEditor;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataIDColumnEditingSupport extends EditingSupport {

    private TestSuitePartDataBindingView mpart;

    public TestDataIDColumnEditingSupport(ColumnViewer viewer, TestSuitePartDataBindingView view) {
        super(viewer);
        this.mpart = view;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element != null && element instanceof TestCaseTestDataLink) {
            TestCaseTestDataLink link = (TestCaseTestDataLink) element;
            return new TestDataCellEditor((Composite) getViewer().getControl(), link.getTestDataId(),
                    link.getTestDataId());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element != null && element instanceof TestCaseTestDataLink);
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof TestCaseTestDataLink) {
            TestCaseTestDataLink link = (TestCaseTestDataLink) element;
            return link.getTestDataId();
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        try {
            if (element != null && element instanceof TestCaseTestDataLink && value instanceof TestDataTreeEntity) {
                TestCaseTestDataLink link = (TestCaseTestDataLink) element;

                TestDataTreeEntity treeEntity = (TestDataTreeEntity) value;
                DataFileEntity testDataEntity = (DataFileEntity) treeEntity.getObject();
                String testDataId = testDataEntity.getIdForDisplay();
                if (testDataId != null && !testDataId.equals(link.getTestDataId())) {
                    link.setTestDataId(testDataId);

                    getViewer().update(element, null);
                    refreshVariableLink(link.getId());
                    mpart.setDirty(true);
                }

            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void refreshVariableLink(String testDataLinkId) {
        if (mpart.getSelectedTestCaseLink() == null) return;
        for (VariableLink variableLink : mpart.getSelectedTestCaseLink().getVariableLinks()) {
            if (variableLink.getType() == VariableType.DATA_COLUMN
                    && testDataLinkId.equals(variableLink.getTestDataLinkId())) {

                variableLink.setValue(StringUtils.EMPTY);
                mpart.refreshVariableLink(variableLink);
            }
        }
    }

}
