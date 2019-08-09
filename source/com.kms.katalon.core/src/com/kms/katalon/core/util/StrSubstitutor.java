package com.kms.katalon.core.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.codehaus.groovy.control.CompilationFailedException;

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
        str = str.replaceAll("\\$(?!\\{)", "\\\\\\$");
        str = str.replaceAll("\\\\", "\\\\\\\\");
        try {
            GStringTemplateEngine engine = new GStringTemplateEngine();
            return engine.createTemplate(str).make(variables).toString();
        } catch (IOException | CompilationFailedException | ClassNotFoundException e) {
            return str;
        }
    }
}
