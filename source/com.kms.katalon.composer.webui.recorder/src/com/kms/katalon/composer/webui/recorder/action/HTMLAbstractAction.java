package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;

public abstract class HTMLAbstractAction implements IHTMLAction {
    protected String name;
    protected String mappedKeywordClassName;
    protected String mappedKeywordClassSimpleName;
    protected String mappedKeywordMethod;
    protected HTMLActionParam[] params;

    public HTMLAbstractAction(String name, String mappedKeywordClassName, String mappedKeywordClassSimpleName, String mappedKeywordMethod) {
        this.name = name;
        this.mappedKeywordClassName = mappedKeywordClassName;
        this.mappedKeywordClassSimpleName = mappedKeywordClassSimpleName;
        this.mappedKeywordMethod = mappedKeywordMethod;
        params = HTMLActionUtil.collectKeywordParam(mappedKeywordClassName, mappedKeywordMethod);
    }

    public String getName() {
        return name;
    }

    public String getMappedKeywordClassName() {
        return mappedKeywordClassName;
    }

    public String getMappedKeywordMethod() {
        return mappedKeywordMethod;
    }

    @Override
    public String getMappedKeywordClassSimpleName() {
        return mappedKeywordClassSimpleName;
    }

    @Override
    public boolean hasElement() {
        return HTMLActionUtil.hasElement(mappedKeywordClassName, mappedKeywordMethod);
    }

    @Override
    public boolean hasInput() {
        return params != null && params.length > 0;
    }

    @Override
    public HTMLActionParam[] getParams() {
        return params;
    }
}
