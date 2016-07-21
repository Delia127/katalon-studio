package com.kms.katalon.composer.mobile.objectspy.element.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;

public class BasicMobileElement implements MobileElement {
    private static final long serialVersionUID = -5432610719458440188L;

    private String name;

    private Map<String, String> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BasicMobileElement)) {
            return false;
        }
        BasicMobileElement other = (BasicMobileElement) obj;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        return true;
    }

    public String getXpath() {
        return getAttributes().get("xpath");
    }

    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public MobileElement clone() {
        try {
            return (MobileElement) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
