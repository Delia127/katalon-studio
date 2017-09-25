package com.kms.katalon.composer.execution.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class MapUtil {
    private static final String MAP_DATA_SEPARATOR = ", ";
    
    public static Map<String, String> convertObjectToStringMap(Object object) {
        if (!(object instanceof Map<?, ?>)) {
            return new HashMap<String, String>();
        }
        Map<String, String> newMap = new HashMap<String, String>();
        for (Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
            Object entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            if (entryKey instanceof String && entryValue instanceof String) {
                newMap.put((String) entryKey, (String) entryValue);
            }
        }
        return newMap;
    }
    
    public static String buildStringForMap(Map<String, String> map) {
        if (map == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(map.values(), MAP_DATA_SEPARATOR);
    }
}
