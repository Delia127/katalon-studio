package com.kms.katalon.groovy.util;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyStringUtil {
    public static String toGroovyStringFormat(String rawString) {
        return (rawString != null) ? "'" + escapeGroovy(rawString) + "'" : rawString;
    }

    public static String escapeGroovy(String rawString) {
        return StringUtils.isNotEmpty(rawString) ? StringEscapeUtils.escapeJava(rawString).replace("'", "\\'")
                : rawString;
    }

    public static String evaluate(String rawString, Map<String, Object> variables) {
        GroovyShell shell = new GroovyShell(new Binding(variables));
        return (String) shell.evaluate("(\"\"\"\\\n" + rawString + "\n\"\"\").toString();");
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
