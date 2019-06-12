package com.kms.katalon.composer.mobile.objectspy.actions;

public interface IMobileAction {
    public String getName();
    
    public String getReadableName();

    public String getMappedKeywordClassSimpleName();

    public String getMappedKeywordClassName();

    public String getMappedKeywordMethod();

    public boolean hasElement();

    public boolean hasInput();

    public String getDescription();

    public MobileActionParam[] getParams();
}
