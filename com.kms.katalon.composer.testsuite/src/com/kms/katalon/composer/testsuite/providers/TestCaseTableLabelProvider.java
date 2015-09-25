package com.kms.katalon.composer.testsuite.providers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseTableLabelProvider extends StyledCellLabelProvider {

    public static final int COLUMN_NOTIFICATION_INDEX = 0;
    public static final int COLUMN_ORDER_INDEX = 1;
    public static final int COLUMN_ID_INDEX = 2;
    public static final int COLUMN_DESCRIPTION_INDEX = 3;
    public static final int COLUMN_RUN_INDEX = 4;

    private static TestCaseController testCaseController = TestCaseController.getInstance();
    private int columnIndex;

    public TestCaseTableLabelProvider(int columnIndex) {
        super(COLORS_ON_SELECTION);
        this.columnIndex = columnIndex;
    }

    @Override
    protected void paint(Event event, Object element) {
        if (element == null || !(element instanceof TestSuiteTestCaseLink)) {
            return;
        }

        GC gc = event.gc;

        if (columnIndex == COLUMN_RUN_INDEX) {
            if (((TestSuiteTestCaseLink) element).getIsRun()) {
                gc.drawImage(ImageConstants.IMG_16_CHECKBOX_CHECKED, event.getBounds().x + 5, event.getBounds().y);
            } else {
                gc.drawImage(ImageConstants.IMG_16_CHECKBOX_UNCHECKED, event.getBounds().x + 5, event.getBounds().y);
            }
        } else if (columnIndex == COLUMN_NOTIFICATION_INDEX) {
            if (getTestCase(element) == null) {
                gc.drawImage(ImageConstants.IMG_16_NOTIFICATION_HEADER, event.getBounds().x + 2, event.getBounds().y);
            }
        } else {
            super.paint(event, element);
        }
    }

    private String getColumnText(Object element) {
        TestCaseEntity testCase = getTestCase(element);

        try {
            TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
            List<TestSuiteTestCaseLink> allTestCaseLinks = (List<TestSuiteTestCaseLink>) getTestCaseTableViewer()
                    .getInput();

            switch (columnIndex) {
                case COLUMN_ORDER_INDEX:
                    return Integer.toString(allTestCaseLinks.indexOf(testCaseLink) + 1);

                case COLUMN_ID_INDEX:
                    return testCaseLink.getTestCaseId();

                case COLUMN_DESCRIPTION_INDEX:
                    if (testCase == null) {
                        return "";
                    }
                    return testCase.getDescription();

                case COLUMN_RUN_INDEX:
                    return StringUtils.EMPTY;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        return StringUtils.EMPTY;
    }

    @Override
    public String getToolTipText(Object element) {
        if (columnIndex == COLUMN_NOTIFICATION_INDEX && getTestCase(element) == null) {
            TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
            return MessageFormat.format(StringConstants.LP_WARN_MSG_TEST_CASE_MISSING, testCaseLink.getTestCaseId());
        } else {
            return getColumnText(element);
        }
    }

    @Override
    public void update(ViewerCell cell) {
        String cellText = getColumnText(cell.getElement());
        cell.setText(cellText);
        cell.setImage(null);
        if (columnIndex == COLUMN_ID_INDEX) {
            TextLayout textLayout = new TextLayout(cell.getControl().getDisplay());
            textLayout.setText(cellText);

            Table table = (Table) getViewer().getControl();

            if (textLayout.getBounds().width + 5 > table.getColumn(columnIndex).getWidth()) {
                int textNameIdx = Math.max(0, cellText.lastIndexOf("/"));
                cell.setText(".." + cellText.substring(textNameIdx, cellText.length()));
            }

        }

        if (getTestCase(cell.getElement()) == null) {
            // If test case does not exist, highlight its table item.
            if (columnIndex == COLUMN_ID_INDEX) {
                cell.setForeground(ColorUtil.getErrorTableItemForegroundColor());
            }
        } else {
            cell.setForeground(ColorUtil.getDefaultTextColor());
        }

        List<StyleRange> range = new ArrayList<>();

        if (columnIndex != COLUMN_ORDER_INDEX) {
            String searchString = getTestCaseTableViewer().getSearchedString().trim().toLowerCase();
            if (searchString != null && !searchString.isEmpty()) {
                String highlightedString = getHighlightedString(searchString);
                if (cell.getText().toLowerCase().contains(highlightedString) && !highlightedString.isEmpty()) {
                    Matcher m = Pattern.compile(Pattern.quote(highlightedString)).matcher(cell.getText().toLowerCase());
                    while (m.find()) {
                        StyleRange myStyledRange = new StyleRange(m.start(), highlightedString.length(), null,
                                ColorUtil.getHighlightBackgroundColor());
                        range.add(myStyledRange);
                    }
                }
            }
        }
        cell.setStyleRanges(range.toArray(new StyleRange[range.size()]));
        super.update(cell);
    }

    private TestCaseTableViewer getTestCaseTableViewer() {
        return (TestCaseTableViewer) getViewer();
    }

    private TestCaseEntity getTestCase(Object element) {
        if (!(element instanceof TestSuiteTestCaseLink)) {
            return null;
        }

        try {
            TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
            return testCaseController.getTestCaseByDisplayId(testCaseLink.getTestCaseId());

        } catch (Exception e) {
            return null;
        }
    }

    private String getHighlightedString(String searchedString) {
        Map<String, String> tagMap = EntityViewerFilter.parseSearchedString(TestCaseTreeEntity.SEARCH_TAGS,
                searchedString);
        String highlightedString = null;
        switch (columnIndex) {
            case COLUMN_ID_INDEX:
                highlightedString = tagMap.get("id");
                break;
            case COLUMN_DESCRIPTION_INDEX:
                highlightedString = tagMap.get("description");
                break;
        }
        return (highlightedString == null || highlightedString.isEmpty()) ? searchedString : highlightedString;
    }
}
