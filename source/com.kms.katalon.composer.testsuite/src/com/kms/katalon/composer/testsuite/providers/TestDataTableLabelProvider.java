package com.kms.katalon.composer.testsuite.providers;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataTableLabelProvider extends StyledCellLabelProvider {

    public static final int COLUMN_NOTIFICATION_INDEX = 0;
    public static final int COLUMN_ORDER_INDEX = 1;
    public static final int COLUMN_ID_INDEX = 2;
    public static final int COLUMN_ITERATION_INDEX = 3;
    public static final int COLUMN_COMBINATION_INDEX = 4;

    private static TestDataController testDataControler = TestDataController.getInstance();

    private TestSuitePartDataBindingView testSuiteDataBindingView;
    private int columnIndex;

    public TestDataTableLabelProvider(int columnIndex, TestSuitePartDataBindingView testSuiteDataBindingView) {
        super();
        this.columnIndex = columnIndex;
        this.testSuiteDataBindingView = testSuiteDataBindingView;
    }

    @Override
    protected void paint(Event event, Object element) {
        if (element == null || !(element instanceof TestCaseTestDataLink)) {
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
        if (element == null || !(element instanceof TestCaseTestDataLink)) {
            return null;
        }

        TestCaseTestDataLink link = (TestCaseTestDataLink) element;

        if (columnIndex == COLUMN_NOTIFICATION_INDEX) {
            String testDataId = link.getTestDataId();
            try {
                if (testDataId == null || testDataControler.getTestDataByDisplayId(testDataId) == null) {
                    return ImageConstants.IMG_16_NOTIFICATION_HEADER;
                }
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
        } else if (columnIndex == COLUMN_COMBINATION_INDEX) {
            switch (link.getCombinationType()) {
                case MANY:
                    return ImageConstants.IMG_16_DATA_CROSS;
                case ONE:
                    return ImageConstants.IMG_16_DATA_ONE_ONE;
                default:
                    return null;
            }
        }
        return null;
    }

    public String getColumnText(Object element) {
        if (element == null || !(element instanceof TestCaseTestDataLink)) {
            return StringUtils.EMPTY;
        }
        TestCaseTestDataLink link = (TestCaseTestDataLink) element;
        switch (columnIndex) {
            case COLUMN_ORDER_INDEX:
                int order = testSuiteDataBindingView.getSelectedTestCaseLink().getTestDataLinks().indexOf(link) + 1;
                return Integer.toString(order);
            case COLUMN_ID_INDEX:
                return link.getTestDataId();
            case COLUMN_ITERATION_INDEX:
                return link.getIterationEntity().getDisplayString();
            case COLUMN_COMBINATION_INDEX:
                return link.getCombinationType().toString();

        }
        return StringUtils.EMPTY;
    }

    @Override
    public void update(ViewerCell cell) {
        cell.setText(getColumnText(cell.getElement()));
        cell.setImage(getColumnImage(cell.getElement()));

        TestCaseTestDataLink link = (TestCaseTestDataLink) cell.getElement();
        String testDataId = link.getTestDataId();
        try {
            if (testDataId == null || testDataControler.getTestDataByDisplayId(testDataId) == null) {
                if (columnIndex == COLUMN_NOTIFICATION_INDEX) {
                    cell.setForeground(ColorUtil.getDefaultTextColor());
                } else if (columnIndex == COLUMN_ID_INDEX) {
                    cell.setForeground(ColorUtil.getErrorTableItemForegroundColor());
                }
            } else {
                cell.setForeground(ColorUtil.getDefaultTextColor());
            }
        } catch (Exception e) {

        }
        super.update(cell);
    }

    @Override
    public String getToolTipText(Object element) {
        if (element == null || !(element instanceof TestCaseTestDataLink)) {
            return "";
        }

        TestCaseTestDataLink link = (TestCaseTestDataLink) element;
        
        if (columnIndex == COLUMN_NOTIFICATION_INDEX) {
            String testDataId = link.getTestDataId();
            try {
                if (testDataId == null || testDataControler.getTestDataByDisplayId(testDataId) == null) {
                    return MessageFormat.format(StringConstants.LP_WARN_MSG_TEST_DATA_MISSING, testDataId);
                }
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
        } else {
            return getColumnText(element);
        }
        return "";
    }

}
