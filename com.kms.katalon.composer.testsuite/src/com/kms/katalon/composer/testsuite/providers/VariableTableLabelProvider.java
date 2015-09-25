package com.kms.katalon.composer.testsuite.providers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

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

public class VariableTableLabelProvider extends StyledCellLabelProvider {

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
        this.columnIndex = columnIndex;
        this.testSuiteDataBindingView = testDataTreeView;
    }

    @Override
    protected void paint(Event event, Object element) {
        if (element == null || !(element instanceof VariableLink)) {
            return;
        }

        GC gc = event.gc;

        if (columnIndex == COLUMN_NOTIFICATION_INDEX) {
            Image columnImage = getColumnImage(element);
            if (columnImage != null) {
                gc.drawImage(ImageConstants.IMG_16_NOTIFICATION_HEADER, event.getBounds().x + 2, event.getBounds().y);
            }
        } else {
            super.paint(event, element);
        }
    }

    public Image getColumnImage(Object element) {
        VariableLink variableLink = (VariableLink) element;
        if (variableLink == null) {
            return null;
        }
        if (variableLink.getType() == VariableType.SCRIPT) {
            return null;
        }

        switch (columnIndex) {
            case COLUMN_NOTIFICATION_INDEX:
                if (variableLink.getTestDataLinkId() == null || variableLink.getTestDataLinkId().isEmpty()
                        || variableLink.getValue() == null || variableLink.getValue().isEmpty()) {
                    return ImageConstants.IMG_16_ERROR_TABLE_ITEM;
                }
                return null;
            case COLUMN_TEST_DATA_ID_INDEX:
                if (variableLink.getTestDataLinkId() == null || variableLink.getTestDataLinkId().isEmpty()) {
                    return ImageConstants.IMG_16_WARN_TABLE_ITEM;
                }
                return null;
            case COLUMN_VALUE_INDEX:
                if (variableLink.getValue() == null || variableLink.getValue().isEmpty()) {
                    return ImageConstants.IMG_16_WARN_TABLE_ITEM;
                }
                return null;
            default:
                return null;
        }
    }

    public String getColumnText(Object element) {
        if (element == null || !(element instanceof VariableLink) || columnIndex < 0
                || columnIndex >= ((TableViewer) getViewer()).getTable().getColumnCount()) return StringUtils.EMPTY;

        TestSuiteTestCaseLink testCaseLink = testSuiteDataBindingView.getSelectedTestCaseLink();
        if (testCaseLink == null) {
            return StringUtils.EMPTY;
        }

        VariableLink variableLink = (VariableLink) element;

        try {
            VariableEntity variableEntity = testSuiteController.getVariable(testCaseLink, variableLink);
            if (variableEntity == null) return StringUtils.EMPTY;

            switch (columnIndex) {
                case COLUMN_NO_INDEX:
                    return Integer.toString(testSuiteDataBindingView.getSelectedTestCaseLink().getVariableLinks()
                            .indexOf(element) + 1);
                case COLUMN_NAME_INDEX:
                    return variableEntity.getName();
                case COLUMN_DEFAULT_VALUE_INDEX:
                    return variableEntity.getDefaultValue();
                case COLUMN_TYPE_INDEX:
                    return variableLink.getType().toString();
                case COLUMN_TEST_DATA_ID_INDEX:
                    if (!variableLink.getTestDataLinkId().isEmpty()) {
                        TestCaseTestDataLink testDataLink = testSuiteController.getTestDataLink(
                                variableLink.getTestDataLinkId(), testCaseLink);
                        if (testDataLink != null) {
                            int order = testSuiteDataBindingView.getSelectedTestCaseLink().getTestDataLinks()
                                    .indexOf(testDataLink) + 1;
                            return Integer.toString(order) + " - " + testDataLink.getTestDataId();
                        }
                    }
                    return StringUtils.EMPTY;
                case COLUMN_VALUE_INDEX:
                    return variableLink.getValue();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void update(ViewerCell cell) {
        cell.setText(getColumnText(cell.getElement()));
        cell.setImage(getColumnImage(cell.getElement()));

        super.update(cell);
    }

    @Override
    public String getToolTipText(Object element) {
        VariableLink variableLink = (VariableLink) element;
        if (variableLink == null) {
            return "";
        }

        switch (columnIndex) {
            case COLUMN_NOTIFICATION_INDEX:
                StringBuilder tooltipText = new StringBuilder();
                if (variableLink.getTestDataLinkId() == null || variableLink.getTestDataLinkId().isEmpty()) {
                    tooltipText.append(StringConstants.LP_WARN_MSG_SET_TEST_DATA_NOTIFY);
                }
                
                if (variableLink.getValue() == null || variableLink.getValue().isEmpty()) {
                    if (tooltipText.length() != 0) {
                        tooltipText.append("\n");
                    }
                    tooltipText.append(StringConstants.LP_WARN_MSG_SET_TEST_DATA_COLUMN_NOTIFY);
                }                
                return tooltipText.toString();
            case COLUMN_TEST_DATA_ID_INDEX: {
                if (variableLink.getTestDataLinkId() == null || variableLink.getTestDataLinkId().isEmpty()) {
                    return StringConstants.LP_WARN_MSG_SET_TEST_DATA;
                }
                break;
            }
            case COLUMN_VALUE_INDEX: {
                if (variableLink.getValue() == null || variableLink.getValue().isEmpty()) {
                    return StringConstants.LP_WARN_MSG_SET_TEST_DATA_COLUMN;
                }
                break;
            }
            default:
                break;
        }
        return getColumnText(element);
    }
}
