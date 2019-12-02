package com.kms.katalon.composer.testsuite.support;

import java.io.File;
import java.net.URLClassLoader;
import java.text.MessageFormat;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.editors.DataColumnChooserEditor;
import com.kms.katalon.composer.testsuite.editors.VariableBindingScriptBuilderEditor;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.testdata.DBData;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class VariableValueEditingSupport extends TypeCheckedEditingSupport<VariableLink> {

    private TestSuitePartDataBindingView testDataView;

    private TestData testData;

    public VariableValueEditingSupport(ColumnViewer viewer, TestSuitePartDataBindingView mpart) {
        super(viewer);
        testDataView = mpart;
    }

    public String[] getTestDataColumnNames(String testDataId) {
        try {
            if (StringUtils.isNotEmpty(testDataId)) {
                TestData testData = TestDataFactory.findTestDataForExternalBundleCaller(testDataId,
                        getProjectFolderLocation());
                return testData.getColumnNames();
            }
        } catch (Exception ex) {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    MessageFormat.format(StringConstants.PA_WARN_MSG_DATA_SRC_NOT_AVAILABLE, testDataId));
            LoggerSingleton.logError(ex);
        }
        return null;
    }

    @Override
    protected Class<VariableLink> getElementType() {
        return VariableLink.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(VariableLink variableLink) {
        Composite composite = getComposite();
        switch (variableLink.getType()) {
            case DATA_COLUMN:
                return new DataColumnChooserEditor(composite, testData, variableLink.getValue());
            case DATA_COLUMN_INDEX:
                return new TextCellEditor(composite);
            case SCRIPT_VARIABLE:
                return new VariableBindingScriptBuilderEditor(composite, testDataView.getSelectedTestCaseLink(),
                        (VariableLink) variableLink.clone());
            default:
                return null;
        }
    }

    @Override
    protected boolean canEditElement(VariableLink variableLink) {
        switch (variableLink.getType()) {
            case DEFAULT:
                return false;
            case SCRIPT_VARIABLE:
                return true;
            case DATA_COLUMN:
            case DATA_COLUMN_INDEX: {
                return isTestDataAvailable(variableLink);
            }
            default:
                return false;
        }
    }

    private String getProjectFolderLocation() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    private boolean isTestDataAvailable(VariableLink variableLink) {
        testData = null;

        TestSuiteTestCaseLink testCaseLink = testDataView.getSelectedTestCaseLink();
        if (testCaseLink == null) {
            return false;
        }

        TestCaseTestDataLink testDataLink = TestSuiteController.getInstance().getTestDataLink(
                variableLink.getTestDataLinkId(), testCaseLink);

        if (!isTestDataLinkValid(variableLink, testDataLink)) {
            return false;
        }

        String testDataId = testDataLink.getTestDataId();
        if (!isTestDataIdValid(variableLink, testDataId)) {
            return false;
        }
        String projectLocation = getProjectFolderLocation();
        try {
			testData = TestDataController.getInstance().getTestDataInstance(testDataId, projectLocation);
			return true;
		} catch (Exception ex) {
            // Show data source of test data not found dialog.
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    MessageFormat.format(StringConstants.SUP_WARN_MSG_TEST_DATA_NOT_AVAILABLE, testDataId));
            LoggerSingleton.logError(ex);
		}
        return false;
    }

    private boolean isTestDataLinkValid(VariableLink variableLink, TestCaseTestDataLink testDataLink) {
        if (testDataLink != null) {
            return true;
        }

        if (StringUtils.isEmpty(variableLink.getValue())) {
            MessageDialog.openInformation(null, StringConstants.INFORMATION_TITLE,
                    StringConstants.SUP_INFORMATION_MSG_CHOOSE_TEST_DATA);
        } else {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    StringConstants.SUP_WARN_MSG_TEST_DATA_NOT_AVAILABLE);
        }
        return false;
    }

    private boolean isTestDataIdValid(VariableLink variableLink, String testDataId) {
        try {
            DataFileEntity dataFileEntity = TestDataController.getInstance().getTestDataByDisplayId(testDataId);
            if (dataFileEntity == null) {
                // Show Test data not found dialog
                MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                        MessageFormat.format(StringConstants.SUP_WARN_MSG_TEST_DATA_NOT_FOUND, testDataId));
                return false;
            }
            if (!dataFileEntity.isContainsHeaders() && variableLink.getType() == VariableType.DATA_COLUMN) {
                MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                        MessageFormat.format(StringConstants.SUP_WARN_MSG_TEST_DATA_NO_HEADER, testDataId));
                return false;
            }
            return true;
        } catch (Exception e) {
            // Show Test data not found dialog
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    MessageFormat.format(StringConstants.SUP_WARN_MSG_TEST_DATA_NOT_FOUND, testDataId));
            LoggerSingleton.logError(e);
            return false;
        }
    }

    @Override
    protected Object getElementValue(VariableLink variableLink) {
        return variableLink.getValue();
    }

    @Override
    protected void setElementValue(VariableLink variableLink, Object value) {
        if (!(value instanceof String) || ObjectUtils.equals(variableLink.getValue(), value)) {
            return;
        }
        variableLink.setValue((String) value);
        getViewer().refresh(variableLink);
        testDataView.setDirty(true);
    }
}
