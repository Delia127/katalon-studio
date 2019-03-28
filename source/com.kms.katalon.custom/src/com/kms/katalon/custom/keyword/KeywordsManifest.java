package com.kms.katalon.custom.keyword;

import java.util.ArrayList;
import java.util.List;

public class KeywordsManifest {
    
    private List<String> keywords = new ArrayList<>();

    private List<String> listeners = new ArrayList<>();

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getListeners() {
        return listeners;
    }

    public void setListeners(List<String> listeners) {
        this.listeners = listeners;
    }
}
