package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;

public enum HTMLAction implements IHTMLAction {
    LeftClick("click"), RightClick("rightClick"), DoubleClick("doubleClick"), Select("selectOptionByValue"), Deselect(
            "deselectOptionByValue"), Check("check"), Uncheck("uncheck"), Submit("submit"), SetText("setText"), Navigate("navigateToUrl"), SwitchToWindow(
            "switchToWindowTitle");

    private String mappedKeywordClass;
    private String mappedKeywordMethod;

    private HTMLAction(String mappedKeywordMethod) {
        this(WebUiBuiltInKeywords.class.getName(), mappedKeywordMethod);
    }

    private HTMLAction(String mappedKeywordClass, String mappedKeywordMethod) {
        this.mappedKeywordClass = mappedKeywordClass;
        this.mappedKeywordMethod = mappedKeywordMethod;
    }

    public String getMappedKeywordClass() {
        return mappedKeywordClass;
    }

    public String getMappedKeywordMethod() {
        return mappedKeywordMethod;
    }

    @Override
    public String getName() {
        return name();
    }
}
