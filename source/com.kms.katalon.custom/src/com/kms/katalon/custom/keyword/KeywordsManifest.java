package com.kms.katalon.custom.keyword;

import java.util.ArrayList;
import java.util.List;

public class KeywordsManifest {

    private String id;

    private String name;

    private List<String> keywords = new ArrayList<>();

    private List<String> listeners = new ArrayList<>();

    private CustomKeywordConfiguration configuration;
    
    private CustomReportConfiguration report;

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

    public CustomKeywordConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CustomKeywordConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomReportConfiguration getReport() {
        return report;
    }

    public void setReport(CustomReportConfiguration report) {
        this.report = report;
    }
}
