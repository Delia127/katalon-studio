package com.kms.katalon.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.util.EntityTagUtil;

public class FilterController {

    private static final List<String> DEFAULT_KEYWORDS = Arrays.asList("id", "name", "tag", "comment", "description");

    private static FilterController instance;

    public static FilterController getInstance() {
        if (instance == null) {
            instance = new FilterController();
        }
        return instance;
    }

    public List<String> getDefaultKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.addAll(DEFAULT_KEYWORDS);
        if (isAdvancedTagPluginInstalled()) {
            keywords.add(getAdvancedTagKeyword());
        }
        return keywords;
    }
    
    public String getAdvancedTagKeyword() {
        return "tags";
    }

    public boolean isMatched(FileEntity fileEntity, String filteringText) {
        String trimmedText = filteringText.trim();
        List<String> keywordList = new ArrayList<>();
        keywordList.addAll(DEFAULT_KEYWORDS);
        Map<String, String> tagMap = parseSearchedString(keywordList.toArray(new String[0]), trimmedText);

        if (!tagMap.isEmpty()) {
            for (Entry<String, String> entry : tagMap.entrySet()) {
                String keyword = entry.getKey();
                if (DEFAULT_KEYWORDS.contains(keyword) && !compare(fileEntity, keyword, entry.getValue())) {
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
            case "id":
                return ObjectUtils.equals(fileEntity.getIdForDisplay(), text) ||
                        fileEntity.getIdForDisplay().startsWith(text + "/");
            case "name":
                return StringUtils.containsIgnoreCase(fileEntity.getName(), text);
            case "tag":
                return StringUtils.containsIgnoreCase(fileEntity.getTag(), text);
            case "description":
                return StringUtils.containsIgnoreCase(fileEntity.getDescription(), text);
            case "tags":
                return entityHasTags(fileEntity, text);
            default:
                return false;
        }
    }
    
    private boolean isAdvancedTagPluginInstalled() {
        Plugin plugin = ApplicationManager.getInstance().getPluginManager().getPlugin(IdConstants.PLUGIN_ADVANCED_TAGS);
        return plugin != null;
    }
    
    private boolean entityHasTags(FileEntity fileEntity, String searchTagValues) {
        if (StringUtils.isBlank(searchTagValues)) {
            return false;
        }
        
        String entityTagValues = fileEntity.getTag();
        if (StringUtils.isBlank(entityTagValues)) {
            return false;
        }
        
        Set<String> searchTags = EntityTagUtil.parse(searchTagValues).stream()
                .map(tag -> tag.toLowerCase())
                .collect(Collectors.toSet());
        
        Set<String> entityTags = EntityTagUtil.parse(entityTagValues).stream()
                .map(tag -> tag.toLowerCase())
                .collect(Collectors.toSet());
        
        return entityTags.containsAll(searchTags);
    }
}
