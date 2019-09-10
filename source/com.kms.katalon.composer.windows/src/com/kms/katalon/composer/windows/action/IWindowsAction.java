package com.kms.katalon.composer.windows.action;

public interface IWindowsAction {
    public String getName();
    
    public String getReadableName();

    public String getMappedKeywordClassSimpleName();

    public String getMappedKeywordClassName();

    public String getMappedKeywordMethod();

    public boolean hasElement();

    public boolean hasInput();

    public String getDescription();

    public WindowsActionParam[] getParams();
}
