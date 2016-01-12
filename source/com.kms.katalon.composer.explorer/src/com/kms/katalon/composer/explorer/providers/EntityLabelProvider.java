package com.kms.katalon.composer.explorer.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;

@SuppressWarnings("restriction")
public class EntityLabelProvider extends StyledCellLabelProvider implements IEntityLabelProvider {
    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getText(Object element) {
        try {
            if (element instanceof ITreeEntity) {
                return ((ITreeEntity) element).getText();
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
        return StringUtils.EMPTY;
    }

    public Image getImage(Object element) {
        try {
            if (element instanceof ITreeEntity) {
                return ((ITreeEntity) element).getImage();
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
        return null;
    }

    /**
     * Highlight partial texts of element if they match with searchString
     */
    @Override
    public void update(ViewerCell cell) {
        cell.setText(getText(cell.getElement()));

        List<StyleRange> range = new ArrayList<StyleRange>();
        if (searchString != null && !searchString.equals("")) {
            String highlightString = findNameValueInSearchString(cell.getElement());
            if (cell.getText().toLowerCase().contains(highlightString) && !highlightString.equals("")) {

                Matcher m = Pattern.compile(Pattern.quote(highlightString)).matcher(cell.getText().toLowerCase());
                while (m.find()) {
                    StyleRange myStyledRange = new StyleRange(m.start(), highlightString.length(), null,
                            ColorUtil.getHighlightBackgroundColor());
                    range.add(myStyledRange);
                }
            }
        }
        cell.setStyleRanges(range.toArray(new StyleRange[range.size()]));

        cell.setImage(getImage(cell.getElement()));
        super.update(cell);
    }

    private String findNameValueInSearchString(Object element) {
        try {
            if (searchString != null && StringUtils.isNotEmpty(searchString) && element != null
                    && element instanceof ITreeEntity) {

                ITreeEntity entity = ((ITreeEntity) element);
                String keyWord = entity.getKeyWord() + ":";
                String regex = "^" + keyWord + ".*$";

                // keyword all
                String keyWordAll = ExplorerPart.KEYWORD_SEARCH_ALL + ":";
                String regexKeyWordAll = "^" + keyWordAll + ".*$";

                String contentString = searchString.toLowerCase().trim();
                if (searchString.matches(regex) || searchString.matches(regexKeyWordAll)) {
                    // cut keyWord
                    if (searchString.matches(regex)) {
                        contentString = contentString.substring(keyWord.length()).trim();
                    } else {
                        contentString = contentString.substring(keyWordAll.length()).trim();
                    }
                    Map<String, String> tagMap = EntityViewerFilter.parseSearchedString(entity.getSearchTags(),
                            contentString);
                    if (tagMap != null && !tagMap.isEmpty()) {
                        for (Entry<String, String> entry : tagMap.entrySet()) {
                            if (entry.getKey().equals("name")) {
                                return entry.getValue();
                            }
                        }
                    }
                }
                return contentString;
            }
            return StringUtils.EMPTY;
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
            return StringUtils.EMPTY;
        }
    }
}
