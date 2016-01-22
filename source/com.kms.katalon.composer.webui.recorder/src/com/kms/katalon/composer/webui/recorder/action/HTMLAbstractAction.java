package com.kms.katalon.composer.webui.recorder.action;

public abstract class HTMLAbstractAction implements IHTMLAction {
    private String name;
    private String mappedKeywordClass;
    private String mappedKeywordMethod;

    public HTMLAbstractAction(String name, String mappedKeywordClass, String mappedKeywordMethod) {
        this.name = name;
        this.mappedKeywordClass = mappedKeywordClass;
        this.mappedKeywordMethod = mappedKeywordMethod;
    }
    
    public String getName() {
        return name;
    }

    public String getMappedKeywordClass() {
        return mappedKeywordClass;
    }

    public String getMappedKeywordMethod() {
        return mappedKeywordMethod;
    }
}
