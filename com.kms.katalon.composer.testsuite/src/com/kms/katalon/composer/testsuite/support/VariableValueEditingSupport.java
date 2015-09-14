package com.kms.katalon.composer.testsuite.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.editors.DataColumnChooserEditor;
import com.kms.katalon.composer.testsuite.parts.TestSuitePart;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;

public class VariableValueEditingSupport extends EditingSupport {

    private String[] columnNames;
    private TestSuitePart testSuitePart;

    public VariableValueEditingSupport(ColumnViewer viewer, TestSuitePart mpart) {
        super(viewer);
        testSuitePart = mpart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element != null && element instanceof VariableLink) {
            VariableLink link = (VariableLink) element;
            switch (link.getType()) {
                case SCRIPT:
                    return new TextCellEditor((Composite) getViewer().getControl());
                case DATA_COLUMN:
                    return new DataColumnChooserEditor((Composite) getViewer().getControl(), columnNames,
                            link.getValue());
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element != null && element instanceof VariableLink) {
            VariableLink variableLink = (VariableLink) element;
            if (variableLink.getType() == VariableType.DATA_COLUMN) {

                TestSuiteTestCaseLink testCaseLink = testSuitePart.getSelectedTestCaseLink();
                if (testCaseLink == null) return false;

                String testDataLinkId = variableLink.getTestDataLinkId();
                TestCaseTestDataLink testDataLink = TestSuiteController.getInstance().getTestDataLink(testDataLinkId,
                        testCaseLink);

                if (testDataLink == null) {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN_TITLE,
                            StringConstants.SUP_WARN_MSG_TEST_DATA_NOT_AVAILABLE);
                    return false;
                } else {
                    columnNames = testSuitePart.getTestDataColumnNames(testDataLink.getTestDataId());
                }
                return (columnNames != null);
            } else {
                return true;
            }
        }
        columnNames = null;
        return false;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof VariableLink) {
            VariableLink link = (VariableLink) element;
            switch (link.getType()) {
                case DATA_COLUMN:
                    return link.getValue();
                case SCRIPT:
                    return link.getValue();
                default:
                    break;
            }

        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element == null || !(element instanceof VariableLink) || value == null) {
            return;
        }
        
        VariableLink link = (VariableLink) element;
        if (value instanceof String) {
            if (!value.equals(link.getValue())) {
                link.setValue((String) value);
                getViewer().update(element, null);
                testSuitePart.setDirty(true);
            }
        }
    }
}
