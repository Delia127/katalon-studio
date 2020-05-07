package com.kms.katalon.composer.testcase.factory;

import java.util.HashMap;
import java.util.Map;

public class CustomKeywordJavadocFactory {
    
    private static CustomKeywordJavadocFactory instance;

    private Map<String, String> javadocs = new HashMap<>();
   
    private CustomKeywordJavadocFactory() {
    }
    
    public static CustomKeywordJavadocFactory getInstance() {
        if (instance == null) {
            instance = new CustomKeywordJavadocFactory();
        }
        return instance;
    }
    
    public void add(String keywordName, String javadoc) {
        javadocs.put(keywordName, javadoc);
    }
    
    public void add(Map<String, String> javadocs) {
        javadocs.putAll(javadocs);
    }
    
    public String getJavadoc(String keywordName) {
        return javadocs.get(keywordName);
    }
    
    public void reset() {
        javadocs.clear();
    }
}
