package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;

public enum HTMLAction implements IHTMLAction {
    LeftClick("click"), RightClick("rightClick"), DoubleClick("doubleClick"), Select("selectOptionByValue"), Deselect(
            "deselectOptionByValue"), Check("check"), Uncheck("uncheck"), Submit("submit"), SetText("setText"), Navigate(
            "navigateToUrl"), SwitchToWindow("switchToWindowTitle");

    private String mappedKeywordClassName;
    private String mappedKeywordClassSimpleName;
    private String mappedKeywordMethod;
    protected HTMLActionParam[] params;

    private HTMLAction(String mappedKeywordMethod) {
        this(WebUiBuiltInKeywords.class.getName(), WebUiBuiltInKeywords.class.getSimpleName(), mappedKeywordMethod);
    }

    private HTMLAction(String mappedKeywordClassName, String mappedKeywordSimpleName, String mappedKeywordMethod) {
        this.mappedKeywordClassName = mappedKeywordClassName;
        this.mappedKeywordClassSimpleName = mappedKeywordSimpleName;
        this.mappedKeywordMethod = mappedKeywordMethod;
        params = HTMLActionUtil.collectKeywordParam(mappedKeywordClassName, mappedKeywordMethod);
    }

    public String getMappedKeywordClassName() {
        return mappedKeywordClassName;
    }

    @Override
    public String getMappedKeywordClassSimpleName() {
        return mappedKeywordClassSimpleName;
    }

    public String getMappedKeywordMethod() {
        return mappedKeywordMethod;
    }

    @Override
    public String getName() {
        return name();
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
