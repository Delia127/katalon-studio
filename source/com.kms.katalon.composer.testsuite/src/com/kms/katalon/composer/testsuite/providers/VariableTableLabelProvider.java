package com.kms.katalon.composer.testsuite.providers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.variable.VariableEntity;

public class VariableTableLabelProvider extends TypeCheckedStyleCellLabelProvider<VariableLink> {

    public static final int COLUMN_NOTIFICATION_INDEX = 0;

    public static final int COLUMN_NO_INDEX = 1;

    public static final int COLUMN_NAME_INDEX = 2;

    public static final int COLUMN_DEFAULT_VALUE_INDEX = 3;

    public static final int COLUMN_TYPE_INDEX = 4;

    public static final int COLUMN_TEST_DATA_ID_INDEX = 5;

    public static final int COLUMN_VALUE_INDEX = 6;

    private static TestSuiteController testSuiteController = TestSuiteController.getInstance();

    private int columnIndex;

    private TestSuitePartDataBindingView testSuiteDataBindingView;

    public VariableTableLabelProvider(int columnIndex, TestSuitePartDataBindingView testDataTreeView) {
        super(columnIndex);
        this.columnIndex = columnIndex;
        this.testSuiteDataBindingView = testDataTreeView;
    }

    @Override
    protected Class<VariableLink> getElementType() {
        return VariableLink.class;
    }

    @Override
    protected Image getImage(VariableLink variableLink) {
        VariableType type = variableLink.getType();
        if (type == VariableType.DEFAULT || type == VariableType.SCRIPT_VARIABLE) {
            return null;
        }

        switch (columnIndex) {
            case COLUMN_NOTIFICATION_INDEX:
                if (StringUtils.isEmpty(variableLink.getTestDataLinkId())
                        || StringUtils.isEmpty(variableLink.getValue())) {
                    return ImageConstants.IMG_16_ERROR_TABLE_ITEM;
                }
                return null;
            case COLUMN_TEST_DATA_ID_INDEX:
                if (StringUtils.isEmpty(variableLink.getTestDataLinkId())) {
                    return ImageConstants.IMG_16_WARN_TABLE_ITEM;
                }
                return null;
            case COLUMN_VALUE_INDEX:
                if (StringUtils.isEmpty(variableLink.getValue())) {
                    return ImageConstants.IMG_16_WARN_TABLE_ITEM;
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    protected String getText(VariableLink variableLink) {
        TestSuiteTestCaseLink testCaseLink = testSuiteDataBindingView.getSelectedTestCaseLink();
        if (testCaseLink == null) {
            return StringUtils.EMPTY;
        }

        try {
            VariableEntity variableEntity = testSuiteController.getVariable(testCaseLink, variableLink);
            if (variableEntity == null)
                return StringUtils.EMPTY;

            switch (columnIndex) {
                case COLUMN_NO_INDEX:
                    return Integer.toString(testSuiteDataBindingView.getSelectedTestCaseLink()
                            .getVariableLinks()
                            .indexOf(variableLink) + 1);
                case COLUMN_NAME_INDEX:
                    return variableEntity.getName();
                case COLUMN_DEFAULT_VALUE_INDEX:
                    return variableEntity.getDefaultValue();
                case COLUMN_TYPE_INDEX:
                    return variableLink.getType().toString();
                case COLUMN_TEST_DATA_ID_INDEX: {
                    if (StringUtils.isEmpty(variableLink.getTestDataLinkId())) {
                        return StringUtils.EMPTY;
                    }

                    TestCaseTestDataLink testDataLink = testSuiteController.getTestDataLink(
                            variableLink.getTestDataLinkId(), testCaseLink);
                    if (testDataLink == null) {
                        return StringUtils.EMPTY;
                    }
                    int order = testSuiteDataBindingView.getSelectedTestCaseLink()
                            .getTestDataLinks()
                            .indexOf(testDataLink) + 1;
                    return Integer.toString(order) + " - " + testDataLink.getTestDataId();
                }
                case COLUMN_VALUE_INDEX:
                    return variableLink.getType() == VariableType.DEFAULT ? StringUtils.EMPTY : variableLink.getValue();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected String getElementToolTipText(VariableLink variableLink) {
        VariableType type = variableLink.getType();
        if (type == VariableType.DEFAULT || type == VariableType.SCRIPT_VARIABLE) {
            return StringUtils.defaultIfEmpty(getText(variableLink), null);
        }

        switch (columnIndex) {
            case COLUMN_NOTIFICATION_INDEX:
                String variableType =  variableLink.getType().toString();
                if (StringUtils.equals(VariableType.SCRIPT_VARIABLE.toString(), variableType) ||
                        StringUtils.equals(VariableType.DEFAULT.toString(), variableType)) {
                    return null;
                }
                StringBuilder tooltipText = new StringBuilder();
                if (StringUtils.isEmpty(variableLink.getTestDataLinkId())) {
                    tooltipText.append(StringConstants.LP_WARN_MSG_SET_TEST_DATA_NOTIFY);
                }

                if (StringUtils.isEmpty(variableLink.getValue())) {
                    if (tooltipText.length() != 0) {
                        tooltipText.append("\n");
                    }
                    tooltipText.append(StringConstants.LP_WARN_MSG_SET_TEST_DATA_COLUMN_NOTIFY);
                }
                return StringUtils.defaultIfEmpty(tooltipText.toString(), null);
            case COLUMN_TEST_DATA_ID_INDEX: {
                if (StringUtils.isEmpty(variableLink.getTestDataLinkId())) {
                    return StringConstants.LP_WARN_MSG_SET_TEST_DATA;
                }
                return StringUtils.defaultIfEmpty(getText(variableLink), null);
            }
            case COLUMN_VALUE_INDEX: {
                if (StringUtils.isEmpty(variableLink.getValue())) {
                    return StringConstants.LP_WARN_MSG_SET_TEST_DATA_COLUMN;
                }
                return StringUtils.defaultIfEmpty(getText(variableLink), null);
            }
            default:
                return StringUtils.defaultIfEmpty(getText(variableLink), null);
        }
    }
}
