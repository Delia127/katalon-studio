package com.kms.katalon.composer.windows.element;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.entity.repository.WindowsElementEntity;

public class CapturedWindowsElement implements BasicWindowsElement {
    private TreeWindowsElement link;

    private boolean checked;
    
    private String name;

    private Map<String, String> properties;
    
    private WindowsElementEntity.LocatorStrategy locatorStrategy;
    
    private String locator;
    
    private String scriptId;

    public TreeWindowsElement getLink() {
        return link;
    }

    public void setLink(TreeWindowsElement link) {
        this.link = link;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public WindowsElementEntity.LocatorStrategy getLocatorStrategy() {
        return locatorStrategy;
    }

    public void setLocatorStrategy(WindowsElementEntity.LocatorStrategy locatorStrategy) {
        this.locatorStrategy = locatorStrategy;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public String getScriptId() {
        return StringUtils.isNotEmpty(scriptId) ? scriptId: name;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }
}
