package com.kms.katalon.composer.testsuite.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseTableLabelProvider extends StyledCellLabelProvider {

    public static final int COLUMN_NO_INDEX = 0;
    public static final int COLUMN_ID_INDEX = 1;
    public static final int COLUMN_NAME_INDEX = 2;
    public static final int COLUMN_DESCRIPTION_INDEX = 3;

    private static TestCaseController testCaseController = TestCaseController.getInstance();
    private int columnIndex;

    public TestCaseTableLabelProvider(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    private String getColumnText(Object element) {
        if (element == null || !(element instanceof TestSuiteTestCaseLink) || columnIndex < 0
                || columnIndex >= getTestCaseTableViewer().getTable().getColumnCount()) {
            return StringUtils.EMPTY;
        }

        try {
            TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
            List<TestSuiteTestCaseLink> allTestCaseLinks = (List<TestSuiteTestCaseLink>) getTestCaseTableViewer()
                    .getInput();
            TestCaseEntity testCase = testCaseController.getTestCaseByDisplayId(testCaseLink.getTestCaseId());

            switch (columnIndex) {
                case COLUMN_NO_INDEX:
                    return Integer.toString(allTestCaseLinks.indexOf(testCaseLink) + 1);

                case COLUMN_ID_INDEX:
                    return testCaseLink.getTestCaseId();

                case COLUMN_NAME_INDEX:
                    if (testCase == null) return StringUtils.EMPTY;
                    return testCase.getName();

                case COLUMN_DESCRIPTION_INDEX:
                    if (testCase == null) return StringUtils.EMPTY;
                    return testCase.getDescription();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        return StringUtils.EMPTY;
    }

    @Override
    public String getToolTipText(Object element) {
        return getColumnText(element);
    }

    @Override
    public void update(ViewerCell cell) {
        cell.setText(getColumnText(cell.getElement()));
        List<StyleRange> range = new ArrayList<>();
        if (columnIndex != COLUMN_NO_INDEX) {
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

    private String getHighlightedString(String searchedString) {
        Map<String, String> tagMap = EntityViewerFilter.parseSearchedString(TestCaseTreeEntity.SEARCH_TAGS,
                searchedString);
        String highlightedString = null;
        switch (columnIndex) {
            case COLUMN_ID_INDEX:
                highlightedString = tagMap.get("id");
                break;
            case COLUMN_NAME_INDEX:
                highlightedString = tagMap.get("name");
                break;
            case COLUMN_DESCRIPTION_INDEX:
                highlightedString = tagMap.get("description");
                break;
        }
        return (highlightedString == null || highlightedString.isEmpty()) ? searchedString : highlightedString;
    }
}
