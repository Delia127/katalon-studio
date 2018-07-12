package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;

public enum HTMLAction implements IHTMLAction {
    LeftClick("click", "Click on the given element"),
    RightClick("rightClick", "Right click on the given web element"),
    DoubleClick("doubleClick", "Double click on the given web element"),
    Select("selectOptionByValue", "Select the option with the given value"),
    Deselect("deselectOptionByValue", "De-select the option with the given value"),
    Check("check", "Check a toggle-button (check-box/radio-button)"),
    Uncheck("uncheck", "Un-check a toggle-button (check-box/radio-button)"),
    SetText("setText", "Set text to the given element"),
    Navigate("navigateToUrl", "Navigate to the specified web page"),
    SwitchToWindow("switchToWindowTitle", "Switch to the window with given title"),
    SendKeys("sendKeys", "Send keys to a specific element"),
    SetEncryptedText("setEncryptedText", "Set encryted text to the given element");

    private String description;

    private String mappedKeywordClassName;

    private String mappedKeywordClassSimpleName;

    private String mappedKeywordMethod;

    protected HTMLActionParam[] params;

    private HTMLAction(String mappedKeywordMethod) {
        this(mappedKeywordMethod, "");
    }

    private HTMLAction(String mappedKeywordMethod, String description) {
        this(WebUiBuiltInKeywords.class.getName(), WebUiBuiltInKeywords.class.getSimpleName(), mappedKeywordMethod,
                description);
    }

    private HTMLAction(String mappedKeywordClassName, String mappedKeywordSimpleName, String mappedKeywordMethod,
            String description) {
        this.mappedKeywordClassName = mappedKeywordClassName;
        this.mappedKeywordClassSimpleName = mappedKeywordSimpleName;
        this.mappedKeywordMethod = mappedKeywordMethod;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
