package com.kms.katalon.groovy.util;

import org.apache.commons.lang.StringEscapeUtils;

public class GroovyStringUtil {
    public static String escapeGroovy(String rawString) {
        if (rawString == null || rawString.isEmpty()) return rawString;
        return StringEscapeUtils.escapeJava(rawString).replace("'", "\\'");
    }
    
    public static String unescapeGroovy(String rawString) {
        if (rawString == null || rawString.isEmpty()) return rawString;
        return StringEscapeUtils.unescapeJava(rawString).replace("\\'", "'");
    }
}
