package com.kms.katalon.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.katalon.platform.api.extension.filter.impl.InternalFilterAction;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.entity.file.FileEntity;

public class FilterController {

    private static final List<String> DEFAULT_KEYWORDS = Arrays.asList("id", "name", "tag", "comment", "description");

    private static FilterController instance;

    private InternalFilterAction filterAction;

    public static FilterController getInstance() {
        if (instance == null) {
            instance = new FilterController();
        }
        return instance;
    }

    private FilterController() {
        this.filterAction = ApplicationManager.getInstance().getActionService().getAction(InternalFilterAction.class);
    }
    
    public List<String> getDefaultKeywords() {
        return DEFAULT_KEYWORDS;
    }
    
    public List<String> getPluginKeywords() {
        if (filterAction.hasFilters()) {
            return filterAction.getFilterAdapters()
                    .entrySet()
                    .stream()
                    .map(e -> e.getValue().getKeywordName())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    
    public List<String> getAllKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.addAll(DEFAULT_KEYWORDS);
        keywords.addAll(getPluginKeywords());
        return keywords;
    }

    public boolean isMatched(FileEntity fileEntity, String filteringText) {
        String trimmedText = filteringText.trim();
        List<String> keywordList = new ArrayList<>();
        keywordList.addAll(DEFAULT_KEYWORDS);
        if (filterAction.hasFilters()) {
            keywordList.addAll(new ArrayList<>(filterAction.getFilterAdapters()
                    .entrySet()
                    .stream()
                    .map(e -> e.getValue().getKeywordName())
                    .collect(Collectors.toList())));
        }
        Map<String, String> tagMap = parseSearchedString(keywordList.toArray(new String[0]), trimmedText);

        if (!tagMap.isEmpty()) {
            for (Entry<String, String> entry : tagMap.entrySet()) {
                String keyword = entry.getKey();
                if (DEFAULT_KEYWORDS.contains(keyword)) {
                    String entityValue = getPropertyValue(fileEntity, keyword);
                    if (entityValue == null || !entityValue.toLowerCase().contains(entry.getValue().toLowerCase())) {
                        return false;
                    }
                }
            }
            if (filterAction.hasFilters()
                    && !(filterAction.filter(toPlatformEntity(fileEntity), tagMap, filteringText))) {
                return false;
            }
            return true;
        }
        return fileEntity.getName().toLowerCase().contains(filteringText.toLowerCase());
    }

    public <T extends FileEntity> List<T> filter(List<T> entities, String filteringText) {
        return entities.stream().filter(e -> isMatched(e, filteringText)).collect(Collectors.toList());
    }

    /**
     * parse searched string into a map of search tags of an entity element
     * 
     * @param element
     * is ITreeEntity
     * @return
     */
    public Map<String, String> parseSearchedString(String[] searchTags, String contentString) {
        if (searchTags != null) {
            Map<String, String> tagMap = new HashMap<String, String>();
            for (int i = 0; i < searchTags.length; i++) {
                String tagRegex = searchTags[i] + "=\\([^\\)]+\\)";
                Matcher m = Pattern.compile(tagRegex).matcher(contentString);
                while (m.find()) {
                    String tagContent = contentString.substring(m.start() + searchTags[i].length() + 2, m.end() - 1);
                    tagMap.put(searchTags[i], tagContent);
                }
            }
            return tagMap;
        } else {
            return Collections.emptyMap();
        }

    }

    public com.katalon.platform.api.model.Entity toPlatformEntity(FileEntity fileEntity) {
        return new com.katalon.platform.api.model.Entity() {

            @Override
            public String getName() {
                return fileEntity.getName();
            }

            @Override
            public String getId() {
                return fileEntity.getIdForDisplay();
            }

            @Override
            public String getFolderLocation() {
                return fileEntity.getParentFolder() != null ? fileEntity.getParentFolder().getLocation() : null;
            }

            @Override
            public String getFileLocation() {
                return fileEntity.getLocation();
            }
        };
    }

    public String getPropertyValue(FileEntity fileEntity, String keyword) {
        switch (keyword) {
            case "id":
                return fileEntity.getIdForDisplay();
            case "name":
                return fileEntity.getName();
            case "tag":
                return fileEntity.getTag();
            case "description":
                return fileEntity.getDescription();
            default:
                return "";
        }
    }
}
