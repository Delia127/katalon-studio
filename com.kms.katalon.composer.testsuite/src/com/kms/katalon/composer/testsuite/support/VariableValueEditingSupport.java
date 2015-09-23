package com.kms.katalon.composer.testsuite.support;

import java.text.MessageFormat;

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
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.project.ProjectEntity;

public class VariableValueEditingSupport extends EditingSupport {

    private String[] columnNames;
    private TestSuitePartDataBindingView testDataView;

    public VariableValueEditingSupport(ColumnViewer viewer, TestSuitePartDataBindingView mpart) {
        super(viewer);
        testDataView = mpart;
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

                TestSuiteTestCaseLink testCaseLink = testDataView.getSelectedTestCaseLink();
                if (testCaseLink == null) return false;

                String testDataLinkId = variableLink.getTestDataLinkId();
                TestCaseTestDataLink testDataLink = TestSuiteController.getInstance().getTestDataLink(testDataLinkId,
                        testCaseLink);

                if (testDataLink == null) {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN_TITLE,
                            StringConstants.SUP_WARN_MSG_TEST_DATA_NOT_AVAILABLE);
                    return false;
                } else {
                    columnNames = getTestDataColumnNames(testDataLink.getTestDataId());
                }
                return (columnNames != null);
            } else {
                return true;
            }
        }
        columnNames = null;
        return false;
    }

    public String[] getTestDataColumnNames(String testDataId) {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            if (testDataId != null && !testDataId.isEmpty()) {
                TestData testData = TestDataFactory.findTestDataForExternalBundleCaller(testDataId,
                        projectEntity.getFolderLocation());
                return testData.getColumnNames();
            }
        } catch (Exception e) {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    MessageFormat.format(StringConstants.PA_WARN_MSG_DATA_SRC_NOT_AVAILABLE, testDataId));
        }
        return null;
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
                testDataView.setDirty(true);
            }
        }
    }
}
