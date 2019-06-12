package com.kms.katalon.core.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

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

    public String replace(String str) {
        try {
            GStringTemplateEngine engine = new GStringTemplateEngine();
            return engine.createTemplate(str).make(variables).toString();
        } catch (IOException | CompilationFailedException | ClassNotFoundException e) {
            return str;
        }
    }
}
