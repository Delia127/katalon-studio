package com.kms.katalon.composer.testcase.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.keywords.IKeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserControlTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntity;

public class KeywordTreeLabelProvider extends StyledCellLabelProvider implements IEntityLabelProvider {
    private String searchString = null;

    public KeywordTreeLabelProvider() {
        super();
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IKeywordBrowserTreeEntity) {
            if (element instanceof KeywordBrowserTreeEntity && !((KeywordBrowserTreeEntity) element).isCustom()) {
                return TreeEntityUtil.getReadableKeywordName(((IKeywordBrowserTreeEntity) element).getName());
            } else {
                return ((IKeywordBrowserTreeEntity) element).getName();
            }
        }
        return StringConstants.EMPTY;
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof IKeywordBrowserTreeEntity) {
            return ((IKeywordBrowserTreeEntity) element).getToolTip();
        }
        return StringConstants.EMPTY;
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof KeywordBrowserTreeEntity || element instanceof KeywordBrowserControlTreeEntity) {
            return ImageConstants.IMG_16_KEYWORD;
        }
        return ImageConstants.IMG_16_FOLDER;
    }

    @Override
    public void update(ViewerCell cell) {
        cell.setText(getText(cell.getElement()));

        List<StyleRange> range = new ArrayList<StyleRange>();
        if (searchString != null && !searchString.equals("")) {
            searchString = searchString.toLowerCase();
            if (cell.getText().toLowerCase().contains(searchString) && !searchString.isEmpty()) {

                Matcher m = Pattern.compile(searchString).matcher(cell.getText().toLowerCase());
                while (m.find()) {
                    StyleRange myStyledRange = new StyleRange(m.start(), searchString.length(), null,
                            ColorUtil.getHighlightBackgroundColor());
                    range.add(myStyledRange);
                }
            }
        }
        cell.setStyleRanges(range.toArray(new StyleRange[range.size()]));

        cell.setImage(getImage(cell.getElement()));
        super.update(cell);
    }

    @Override
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    protected void measure(Event event, Object element) {
        super.measure(event, element);

        // increase 1 pixel to prevent the last character of cell's text cut
        event.width++;
    }
}
