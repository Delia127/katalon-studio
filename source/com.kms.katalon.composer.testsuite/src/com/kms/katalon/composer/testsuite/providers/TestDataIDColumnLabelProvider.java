package com.kms.katalon.composer.testsuite.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataIDColumnLabelProvider extends StyledCellLabelProvider {
    private StyledText textSearch;

    public TestDataIDColumnLabelProvider(StyledText textSearch) {
        this.textSearch = textSearch;
    }

    @Override
    public void update(ViewerCell cell) {
        TestCaseTestDataLink testDataLink = (TestCaseTestDataLink) cell.getElement();

        cell.setText(testDataLink.getTestDataId());
        switch (testDataLink.getCombinationType()) {
            case MANY:
                cell.setImage(ImageConstants.IMG_16_DATA_CROSS);
                break;
            case ONE:
                cell.setImage(ImageConstants.IMG_16_DATA_ONE_ONE);
                break;
        }

        String cellText = cell.getText().toLowerCase();

        String searchString = textSearch.getText().toLowerCase();
        if (searchString.isEmpty()) {
            cell.setStyleRanges(null);
        } else {
            List<StyleRange> styleRanges = new ArrayList<StyleRange>();
            Matcher matcher = Pattern.compile(searchString).matcher(cellText);
            while (matcher.find()) {
                int rangeLength = matcher.end() - matcher.start();
                StyleRange myStyledRange = new StyleRange(matcher.start(), rangeLength, null, cell.getControl()
                        .getDisplay().getSystemColor(SWT.COLOR_YELLOW));
                styleRanges.add(myStyledRange);
            }

            cell.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
        }
        super.update(cell);
    }
}
