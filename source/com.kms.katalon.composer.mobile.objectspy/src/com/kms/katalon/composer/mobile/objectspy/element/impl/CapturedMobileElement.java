package com.kms.katalon.composer.mobile.objectspy.element.impl;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;

public class CapturedMobileElement extends BasicMobileElement {
    private static final long serialVersionUID = 9135829243722317270L;

    private TreeMobileElement link;
    
    private String name;

    private boolean checked;
    
    private String scriptId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
