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

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.entity.file.FileEntity;

public class FilterController {

    private static final List<String> DEFAULT_KEYWORDS = Arrays.asList("ids", "id", "name", "tag", "comment", "description");
    
    private static final List<String> DEFAULT_KEYWORDS_FOR_INPUTS = Arrays.asList("id", "name", "tag", "comment", "description");
    
    private static final String CONTENT_DELIMITER = ",";

    private static FilterController instance;

    public static FilterController getInstance() {
        if (instance == null) {
            instance = new FilterController();
        }
        return instance;
    }
    
    private FilterController() {
    }

    public List<String> getDefaultKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.addAll(DEFAULT_KEYWORDS);
        return keywords;
    }
    
    public List<String> getDefaultKeywordsForInputs(){
        List<String> keywordsForInputs = new ArrayList<>();
        keywordsForInputs.addAll(DEFAULT_KEYWORDS_FOR_INPUTS);
        return keywordsForInputs;
    }

    public boolean isMatched(FileEntity fileEntity, String filteringText) {
        String trimmedText = filteringText.trim();
        if(trimmedText.equals(StringUtils.EMPTY)){
            return true;
        }        
        List<String> keywordList = getDefaultKeywords();
        Map<String, String> tagMap = parseSearchedString(keywordList.toArray(new String[0]), trimmedText);

        if (!tagMap.isEmpty()) {
            for (Entry<String, String> entry : tagMap.entrySet()) {
                String keyword = entry.getKey();
                if (keywordList.contains(keyword) && !compare(fileEntity, keyword, entry.getValue())) {
                    return false;
                }
            }
            return true;
        }
        return false;
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
    
    public boolean compare(FileEntity fileEntity, String keyword, String text) {
        if (fileEntity == null || keyword == null || text == null) {
            return false;
        }
        switch (keyword) {
            case "ids":
                return textContainsEntityId(text.toLowerCase(), fileEntity);
            case "id":
                return StringUtils.equalsIgnoreCase(fileEntity.getIdForDisplay(), text) 
                        || StringUtils.startsWithIgnoreCase(fileEntity.getIdForDisplay(), text + "/");
            case "name":
                return StringUtils.containsIgnoreCase(fileEntity.getName(), text);
            case "tag":
                return StringUtils.containsIgnoreCase(fileEntity.getTag(), text);
            case "description":
                return StringUtils.containsIgnoreCase(fileEntity.getDescription(), text);
            default:
                return false;
        }
    }
    
    private boolean textContainsEntityId(String text, FileEntity fileEntity) {
        // Allow spaces before and after delimiter
        return Arrays.asList(text.split(CONTENT_DELIMITER))
                .stream()
                .map(a -> a.trim())
                .filter(a -> StringUtils.equalsIgnoreCase(fileEntity.getIdForDisplay(), a) 
                        || StringUtils.startsWithIgnoreCase(fileEntity.getIdForDisplay(), a + "/"))
                .findAny().isPresent();
    }
}