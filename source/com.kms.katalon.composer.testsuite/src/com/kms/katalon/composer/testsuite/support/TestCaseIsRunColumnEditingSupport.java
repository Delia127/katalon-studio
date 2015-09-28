package com.kms.katalon.composer.testsuite.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import com.kms.katalon.composer.testsuite.parts.TestSuitePartTestCaseView;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;

public class TestCaseIsRunColumnEditingSupport extends EditingSupport {

    private TestCaseTableViewer viewer;
    private CheckboxCellEditor editor;
    private TestSuitePartTestCaseView testCaseTableView;
    private static final String FIELD_NAME = "isRun";

    public TestCaseIsRunColumnEditingSupport(ColumnViewer viewer, TestSuitePartTestCaseView testCaseTableView) {
        super(viewer);
        this.viewer = (TestCaseTableViewer) viewer;
        editor = new CheckboxCellEditor(this.viewer.getTable());
        this.testCaseTableView = testCaseTableView;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return editor;
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof TestSuiteTestCaseLink) {
            return ((TestSuiteTestCaseLink) element).getIsRun() ? true : false;
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof TestSuiteTestCaseLink && value != null && value instanceof Boolean) {
            ((TestSuiteTestCaseLink) element).setIsRun((boolean) value);
            viewer.update(element, new String[] { FIELD_NAME });

            if ((boolean) value != viewer.getIsRunAll()) {
                viewer.refreshIsRunAll();
            }

            testCaseTableView.setDirty(true);
        }
    }

}
