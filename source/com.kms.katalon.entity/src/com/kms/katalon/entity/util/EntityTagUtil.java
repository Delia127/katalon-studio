package com.kms.katalon.entity.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class EntityTagUtil {
    
    private static final char TAG_SEPARATOR = ',';

    public static String joinTags(Set<String> tags) {
        String tagValues = StringUtils.join(tags, TAG_SEPARATOR);
        return tagValues;
    }

    public static Set<String> parse(String tagValues) {
        Set<String> parseResult = new HashSet<>();
        String[] tagArray = StringUtils.split(tagValues, TAG_SEPARATOR);
        if (tagArray != null) {
            for (String tag : tagArray) {
                if (!StringUtils.isBlank(tag)) {
                    parseResult.add(tag.trim());
                }
            }
        }
        return parseResult;
    }
    
    public static char getTagSeparator() {
        return TAG_SEPARATOR;
    }
}
