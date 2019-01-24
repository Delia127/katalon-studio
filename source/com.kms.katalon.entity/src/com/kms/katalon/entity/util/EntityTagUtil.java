package com.kms.katalon.entity.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class EntityTagUtil {
    
    private static String TAG_SEPARATORS = ",;";
    
    private static char DEFAULT_TAG_SEPARATOR = ',';

    public static String joinTags(Set<String> tags) {
        String tagValues = StringUtils.join(tags, DEFAULT_TAG_SEPARATOR);
        return tagValues;
    }

    public static Set<String> parse(String tagValues) {
        Set<String> parseResult = new HashSet<>();
        String[] tagArray = StringUtils.split(tagValues, TAG_SEPARATORS);
        if (tagArray != null) {
            for (String tag : tagArray) {
                if (!StringUtils.isBlank(tag)) {
                    parseResult.add(tag.trim());
                }
            }
        }
        return parseResult;
    }
    
    public static String appendTags(String tagValues, Set<String> appendedTags) {
        String appendedTagValues = joinTags(appendedTags);
        if (!StringUtils.isBlank(tagValues)) {
            return tagValues + DEFAULT_TAG_SEPARATOR + appendedTagValues;
        } else {
            return appendedTagValues;
        }
    }
}
