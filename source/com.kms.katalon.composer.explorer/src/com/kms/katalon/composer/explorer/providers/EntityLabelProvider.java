package com.kms.katalon.composer.explorer.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleTreeCellLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;

public class EntityLabelProvider extends TypeCheckedStyleTreeCellLabelProvider<ITreeEntity> implements
        IEntityLabelProvider {
    private static final int FIRST_COLUMN_IDX = 0;

    private static final String COLON = ":";

    private static final String NAME_TAG = "name";

    public EntityLabelProvider() {
        super(FIRST_COLUMN_IDX);
    }

    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * Highlight partial texts of element if they match with searchString
     */
    @Override
    protected StyleRange[] getStyleRanges(ViewerCell cell, ITreeEntity element) {
        if (StringUtils.isEmpty(searchString)) {
            return null;
        }

        String highlightString = findNameValueInSearchString(element);

        if (StringUtils.isEmpty(highlightString)) {
            return null;
        }

        String lowerCaseCellText = StringUtils.defaultString(cell.getText()).toLowerCase();
        if (!lowerCaseCellText.contains(highlightString)) {
            return null;
        }

        List<StyleRange> range = new ArrayList<>();
        Matcher m = Pattern.compile(Pattern.quote(highlightString)).matcher(lowerCaseCellText);
        while (m.find()) {
            StyleRange highlightStyledRange = new StyleRange(m.start(), highlightString.length(), null,
                    ColorUtil.getHighlightBackgroundColor());
            range.add(highlightStyledRange);
        }
        return range.toArray(new StyleRange[range.size()]);
    }

    private String findNameValueInSearchString(ITreeEntity entity) {
        try {
            String contentString = getContentString(ExplorerPart.KEYWORD_SEARCH_ALL + COLON);

            if (contentString == null) {
                contentString = getContentString(entity.getKeyWord() + COLON);
            }

            if (contentString == null) {
                return trimAndLowerCase(searchString);
            }

            Map<String, String> tagMap = EntityViewerFilter.parseSearchedString(entity.getSearchTags(), contentString);
            if (tagMap == null || tagMap.isEmpty()) {
                return contentString;
            }
            String nameValue = tagMap.get(NAME_TAG);
            return nameValue != null ? nameValue : contentString;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }

    private String getContentString(String keyword) {
        String contentString = trimAndLowerCase(searchString);
        if (searchString.matches(getRegexForKeyword(keyword))) {
            return contentString.substring(keyword.length()).trim();
        }

        return null;
    }

    private String trimAndLowerCase(String searchString) {
        return StringUtils.trimToEmpty(searchString).toLowerCase();
    }

    private String getRegexForKeyword(String keyword) {
        return "^" + StringUtils.defaultString(keyword) + ".*$";
    }

    @Override
    protected Class<ITreeEntity> getElementType() {
        return ITreeEntity.class;
    }

    @Override
    protected Image getImage(ITreeEntity element) {
        try {
            return element.getImage();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    @Override
    protected String getText(ITreeEntity element) {
        try {
            return element.getText();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    protected void measure(Event event, Object element) {
        super.measure(event, element);
        if (canNotDrawSafely(element)) {
            return;
        }
        ViewerCell cell = getOwnedViewerCell(event);

        if (isCellNotExisted(cell)) {
            return;
        }
        Image image = cell.getImage();
        if (image == null) {
            return;
        }
        event.height = Math.max(event.height, image.getBounds().height + 6);
    }
}
