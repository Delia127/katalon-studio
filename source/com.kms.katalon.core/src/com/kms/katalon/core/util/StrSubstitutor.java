package com.kms.katalon.core.util;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import groovy.text.GStringTemplateEngine;

public class StrSubstitutor {

    private Map<String, Object> variables;
    
    public StrSubstitutor() {
        this(Collections.emptyMap());
    }
    
    public StrSubstitutor(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * Use {@link GStringTemplateEngine} to convert a template string into
     * a string with variable values.
     * 
     * @param str A template string
     * @return A string with variable values if the template string is not null. Otherwise return an empty string
     */
    public String replace(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        try {
            // escape special characters
            // do not switch the order of two replaceAll()
            // otherwise \ will be generated repeatedly
            // replace \ with \\ https://stackoverflow.com/a/1701876
            str = str.replaceAll("\\\\", "\\\\\\\\");
            // replace any $ that is not followed immediately by a { with \$
            str = str.replaceAll("\\$(?!\\{)", "\\\\\\$");
            GStringTemplateEngine engine = new GStringTemplateEngine();
            return engine.createTemplate(str).make(variables).toString();
        } catch (Exception e) {
            // return the original string if anything went wrong
            return str;
        }
    }
}
