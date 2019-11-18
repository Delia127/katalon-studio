package com.kms.katalon.composer.windows.action;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords;

public enum WindowsAction implements IWindowsAction {
    Click("click", "Click on the given element"),
    DoubleClick("doubleClick", "Double click the given element"),
    RightClick("rightClick", "Right click on the given element"),
    GetText("getText", "Get text of the given element"),
    SetText("setText", "Set text to the given element"),
    SendKeys("sendKeys", "Send combined keys to the application window"),
    ClearText("clearText", "Clear text on the given element"),
    SwitchToWindow("switchToWindow", "Switch to Window"),
    SwitchToWindowTitle("switchToWindowTitle", "Switch to window title"),
    CloseApplication("closeApplication", "Close the application"),
    SwitchToDesktop("switchToDesktop", "Switch to desktop window"),
    SwitchToApplication("switchToApplication", "Switch to application window"),
    StartApplication("startApplication", "Start windows application", false);

    private String description;

    private String mappedKeywordClassName;

    private String mappedKeywordClassSimpleName;

    private String mappedKeywordMethod;

    protected WindowsActionParam[] params;

    private boolean hasElement = false;

    private boolean isUserInputAction = true;
    
    private boolean isCanceled = false;

    private WindowsAction(String mappedKeywordMethod) {
        this(mappedKeywordMethod, "");
    }

    private WindowsAction(String mappedKeywordMethod, String description, boolean isUserInputAction) {
        this(WindowsBuiltinKeywords.class.getName(), WindowsBuiltinKeywords.class.getSimpleName(), mappedKeywordMethod,
                description, isUserInputAction);
    }

    private WindowsAction(String mappedKeywordMethod, String description) {
        this(WindowsBuiltinKeywords.class.getName(), WindowsBuiltinKeywords.class.getSimpleName(), mappedKeywordMethod,
                description, true);
    }

    private WindowsAction(String mappedKeywordClassName, String mappedKeywordSimpleName, String mappedKeywordMethod,
            String description, boolean isUserInputAction) {
        this.mappedKeywordClassName = mappedKeywordClassName;
        this.mappedKeywordClassSimpleName = mappedKeywordSimpleName;
        this.mappedKeywordMethod = mappedKeywordMethod;
        this.description = description;
        this.isUserInputAction = isUserInputAction;
        params = WindowsActionUtil.collectKeywordParam(mappedKeywordClassName, mappedKeywordMethod);
        hasElement = WindowsActionUtil.hasElement(mappedKeywordClassName, mappedKeywordMethod);
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
        return hasElement;
    }

    @Override
    public boolean hasInput() {
        return params != null && params.length > 0;
    }

    public boolean isUserInputAction() {
        return isUserInputAction;
    }

    @Override
    public WindowsActionParam[] getParams() {
        return params;
    }

    public String getDescription() {
        return description;
    }

    public String getReadableName() {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(getName())), " ");
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }
}
