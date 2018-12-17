package com.kms.katalon.composer.explorer.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;

public class EntityViewerFilter extends AbstractEntityViewerFilter {
    private String searchString;
    private EntityProvider entityProvider;
    
    public static final String[] SEARCH_TAGS = new String[] { "id", "name", "tag", "comment", "description", "folder", "source name" };

    public EntityViewerFilter(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @SuppressWarnings("restriction")
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (searchString == null || searchString.equals(StringUtils.EMPTY)) {
            return true;
        }
        if (element instanceof ITreeEntity) {
            boolean returnValue = false;
            try {
                returnValue |= searchElement(element);
            } catch (Exception e) {
                LoggerSingleton.getInstance().getLogger().error(e);
            }

            if (returnValue) return true;
            ITreeEntity entity = ((ITreeEntity) element);
            try {
                if (searchString.startsWith(ExplorerPart.KEYWORD_SEARCH_ALL)
                        || searchString.startsWith(entity.getKeyWord())) {
                    if (entityProvider.getChildren(element) != null) {
                        for (Object child : entityProvider.getChildren(element)) {
                            if (child != null) {
                                returnValue |= select(viewer, element, child);
                            }
                        }
                    }
                    return returnValue;
                }
            } catch (Exception e) {
                return false;
            }

        }
        return false;
    }

    /**
     * filter all tree elements by searched string
     * 
     * @param element
     *            is a instance of ITreeEntity
     * @return
     */
    @SuppressWarnings("restriction")
    private boolean searchElement(Object element) {
        try {
            ITreeEntity entity = ((ITreeEntity) element);
            // entity keyword
            String keyWord = entity.getKeyWord() + ":";
            String regex = "^" + keyWord + ".*$";

            // keyword all
            String keyWordAll = ExplorerPart.KEYWORD_SEARCH_ALL + ":";
            String regexKeyWordAll = "^" + keyWordAll + ".*$";

            String contentString = searchString.toLowerCase().trim();
            if (searchString.matches(regex) || searchString.matches(regexKeyWordAll)) {
                // cut keyword
                if (searchString.matches(regex)) {
                    contentString = contentString.substring(keyWord.length()).trim();
                } else {
                    contentString = contentString.substring(keyWordAll.length()).trim();
                }

                if (contentString.isEmpty()) return true;

                if (entity.getText().toLowerCase().contains(contentString)) return true;

                Map<String, String> tagMap = parseSearchedString(SEARCH_TAGS, contentString);
                if (tagMap != null && !tagMap.isEmpty()) {
                    for (Entry<String, String> entry : tagMap.entrySet()) {
                        String entityValue = entity.getPropertyValue(entry.getKey());
                        if (entityValue != null) {
                            if (!entityValue.toLowerCase().contains(entry.getValue())) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                    return true;
                }

                if (entity.getSearchTags() != null) {
                    for (String tag : entity.getSearchTags()) {
                        String entityValue = entity.getPropertyValue(tag);
                        if (entityValue != null && entityValue.toLowerCase().contains(contentString)) return true;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
        return false;
    }

    /**
     * parse searched string into a map of search tags of an entity element
     * 
     * @param element
     *            is ITreeEntity
     * @return
     */
    @SuppressWarnings("restriction")
    public static Map<String, String> parseSearchedString(String[] searchTags, String contentString) {
        try {
            if (searchTags != null) {
                Map<String, String> tagMap = new HashMap<String, String>();
                for (int i = 0; i < searchTags.length; i++) {
                    String tagRegex = searchTags[i] + "=\\([^\\)]+\\)";
                    Matcher m = Pattern.compile(tagRegex).matcher(contentString);
                    while (m.find()) {
                        String tagContent = contentString
                                .substring(m.start() + searchTags[i].length() + 2, m.end() - 1);
                        tagMap.put(searchTags[i], tagContent);
                    }
                }
                return tagMap;
            } else {
                return null;
            }

        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
            return null;
        }
    }
}
