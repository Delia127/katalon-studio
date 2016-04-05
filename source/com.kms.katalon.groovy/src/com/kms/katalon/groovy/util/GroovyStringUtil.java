package com.kms.katalon.groovy.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;

public class GroovyStringUtil {
    public static String toGroovyStringFormat(String rawString) {
        return (rawString != null) ? "'" + escapeGroovy(rawString) + "'" : rawString;
    }

    public static String escapeGroovy(String rawString) {
        return StringUtils.isNotEmpty(rawString) ? StringEscapeUtils.escapeJava(rawString).replace("'", "\\'")
                : rawString;
    }

    public static String unescapeGroovy(String rawString) {
        return StringUtils.isNotEmpty(rawString) ? StringEscapeUtils.unescapeJava(rawString).replace("\\'", "'")
                : rawString;
    }

    /**
     * Get Keyword/Package relative location to current project.
     * <p>
     * For example, <code>Keywords/NewKeyword.groovy</code>
     * 
     * @param path
     *            Keyword full path
     * @return Keyword relative path
     */
    public static String getKeywordsRelativeLocation(IPath path) {
        String[] segments = path.segments();
        for (int i = 0; i < segments.length; i++) {
            if (StringUtils.equals("Keywords", segments[i])) {
                return StringUtils.join(path.removeFirstSegments(i).segments(), "/");
            }
        }
        return null;
    }
}
