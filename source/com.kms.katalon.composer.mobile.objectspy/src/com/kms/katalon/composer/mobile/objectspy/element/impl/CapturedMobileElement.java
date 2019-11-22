package com.kms.katalon.composer.mobile.objectspy.element.impl;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.entity.repository.MobileElementEntity;

public class CapturedMobileElement extends BasicMobileElement {
    private static final long serialVersionUID = 9135829243722317270L;

    private TreeMobileElement link;

    private boolean checked;

    private String scriptId;

    private MobileElementEntity.LocatorStrategy locatorStrategy;

    private String locator;

    public CapturedMobileElement() {
        this(null);
    }

    public CapturedMobileElement(TreeMobileElement link) {
        this(link, false);
    }
    
    public CapturedMobileElement(TreeMobileElement link, boolean checked) {
        this.link = link;
        this.checked = checked;
    }

    public TreeMobileElement getLink() {
        return link;
    }

    public void setLink(TreeMobileElement link) {
        this.link = link;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getScriptId() {
        return StringUtils.isNotEmpty(scriptId) ? scriptId : "";
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public MobileElementEntity.LocatorStrategy getLocatorStrategy() {
        return locatorStrategy;
    }

    public void setLocatorStrategy(MobileElementEntity.LocatorStrategy locatorStrategy) {
        this.locatorStrategy = locatorStrategy;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }
}
