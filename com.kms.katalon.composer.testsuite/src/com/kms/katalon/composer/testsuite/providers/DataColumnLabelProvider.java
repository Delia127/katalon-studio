package com.kms.katalon.composer.testsuite.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

public class DataColumnLabelProvider extends StyledCellLabelProvider{
    
    private String searchString;
    
    public DataColumnLabelProvider() {
        super();
        searchString = StringUtils.EMPTY;
    }
    
     
    
    public void setSearchString(String text) {
        searchString = text.trim().toLowerCase();
    }
    
    @Override
    public void update(ViewerCell cell) {
        String cellText = cell.getElement().toString();
        cell.setText(cellText);
        if (searchString.equals(StringUtils.EMPTY)) {
            cell.setStyleRanges(null);
        } else {
            Matcher matcher = Pattern.compile(searchString).matcher(cellText.toLowerCase());
            List<StyleRange> styleRanges = new ArrayList<StyleRange>();
            while (matcher.find()) {
                int rangeLength = matcher.end() - matcher.start();
                StyleRange myStyledRange = 
                        new StyleRange(matcher.start(), rangeLength, null, 
                            Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
                styleRanges.add(myStyledRange);
            }
            cell.setStyleRanges(styleRanges.toArray(new StyleRange[0]));
        }
        super.update(cell);
    }
}
