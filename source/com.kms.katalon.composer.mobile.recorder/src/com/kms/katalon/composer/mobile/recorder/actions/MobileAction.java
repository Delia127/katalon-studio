package com.kms.katalon.composer.mobile.recorder.actions;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.mobile.recorder.utils.MobileActionUtil;
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords;

public enum MobileAction implements IMobileAction {
    Tap("tap", "Tap on the given element"),
    TapAndHold("tapAndHold", "Tap and hold on the given element"),
    SetText("setText", "Set text to the given element"),
    ClearText("clearText", "Clear text on the given element"),
    PressBack("pressBack", "Press back button on the mobile device (Android only)"),
    SwitchToLandscape("switchToLandscape", "Switch the mobile device's orientation to landscape mode"),
    SwitchToPortrait("switchToPortrait", "Switch the mobile device's orientation to portrait mode"),
    StartApplication("startApplication", "Start the application", false),
    CloseApplication("closeApplication", "Close the application", false);

    private String description;

    private String mappedKeywordClassName;

    private String mappedKeywordClassSimpleName;

    private String mappedKeywordMethod;

    protected MobileActionParam[] params;

    private boolean hasElement = false;
    
    private boolean isUserInputAction = true;

    private MobileAction(String mappedKeywordMethod) {
        this(mappedKeywordMethod, "");
    }
    
    private MobileAction(String mappedKeywordMethod, String description) {
        this(MobileBuiltInKeywords.class.getName(), MobileBuiltInKeywords.class.getSimpleName(), mappedKeywordMethod,
                description, true);
    }

    private MobileAction(String mappedKeywordMethod, String description, boolean isUserInputAction) {
        this(MobileBuiltInKeywords.class.getName(), MobileBuiltInKeywords.class.getSimpleName(), mappedKeywordMethod,
                description, isUserInputAction);
    }

    private MobileAction(String mappedKeywordClassName, String mappedKeywordSimpleName, String mappedKeywordMethod,
            String description, boolean isUserInputAction) {
        this.mappedKeywordClassName = mappedKeywordClassName;
        this.mappedKeywordClassSimpleName = mappedKeywordSimpleName;
        this.mappedKeywordMethod = mappedKeywordMethod;
        this.description = description;
        this.isUserInputAction = isUserInputAction;
        params = MobileActionUtil.collectKeywordParam(mappedKeywordClassName, mappedKeywordMethod);
        hasElement = MobileActionUtil.hasElement(mappedKeywordClassName, mappedKeywordMethod);
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
    public MobileActionParam[] getParams() {
        return params;
    }

    public String getDescription() {
        return description;
    }

    public String getReadableName() {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(getName())), " ");
    }
}
