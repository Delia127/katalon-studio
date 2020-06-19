package com.kms.katalon.composer.mobile.objectspy.element.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.mobile.objectspy.element.SnapshotMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.entity.repository.MobileElementEntity;
import com.kms.katalon.entity.repository.MobileElementEntity.LocatorStrategy;

import io.appium.java_client.AppiumDriver;

public abstract class RenderedTreeSnapshotMobileElement<T> extends BasicMobileElement implements
        SnapshotMobileElement<T> {
    private static final long serialVersionUID = -6452866868131771671L;

    private final RenderedTreeSnapshotMobileElement<T> parentElement;

    private List<RenderedTreeSnapshotMobileElement<T>> childrenElement;

    private CapturedMobileElement capturedElement;
    
    private String locator = "";

    private MobileElementEntity.LocatorStrategy locatorStrategy = LocatorStrategy.XPATH;

    protected RenderedTreeSnapshotMobileElement() {
        this(null);
    }

    protected RenderedTreeSnapshotMobileElement(RenderedTreeSnapshotMobileElement<T> parentElement) {
        this.parentElement = parentElement;
    }

    public RenderedTreeSnapshotMobileElement<T> getParentElement() {
        return parentElement;
    }

    protected String makeXpath() {
        String tagName = getTagName();
        int index = getIndexPropertyForElement(tagName);
        String xpath = StringUtils.isEmpty(tagName) ? "//*" : ("/" + tagName);
        if (index > 0) {
            xpath += "[" + index + "]";
        }
        if (parentElement == null) {
            // top node, add "/" to select all
            return "/" + xpath;
        }

        String parentXpath = parentElement.getXpath();
        xpath = (StringUtils.isEmpty(parentXpath) ? "//*" : parentXpath) + xpath;
        return xpath;
    }

    private int getIndexPropertyForElement(String tagName) {
        if (StringUtils.isEmpty(tagName) || parentElement == null) {
            return 0;
        }
        int index = 1;
        for (RenderedTreeSnapshotMobileElement<T> sibling : parentElement.getChildrenElement()) {
            if (sibling == this) {
                continue;
            }
            if (tagName.equals(sibling.getTagName())) {
                index += 1;
            }
        }
        return index;
    }

    @Override
    public List<RenderedTreeSnapshotMobileElement<T>> getChildrenElement() {
        if (childrenElement == null) {
            childrenElement = new ArrayList<>();
        }
        return childrenElement;
    }

    public void setChildrenElement(List<RenderedTreeSnapshotMobileElement<T>> childrenElement) {
        this.childrenElement = childrenElement;
    }

    @Override
    public CapturedMobileElement getCapturedElement() {
        return capturedElement;
    }

    public void setCapturedElement(CapturedMobileElement capturedElement) {
        this.capturedElement = capturedElement;
    }

    @Override
    public CapturedMobileElement newCapturedElement(AppiumDriver<?> appiumDriver) {
        CapturedMobileElement capturedElement = new CapturedMobileElement(this, true);
        capturedElement.setName(getName());
        capturedElement.setAttributes(getAttributes());
        buildLocator(appiumDriver);
        capturedElement.setLocator(getLocator());
        capturedElement.setLocatorStrategy(getLocatorStrategy());
        setCapturedElement(capturedElement);
        return capturedElement;
    }

    protected void buildLocator(AppiumDriver<?> appiumDriver) {
	}

	@Override
    public TreeMobileElement findBestMatch(final CapturedMobileElement needToVerify) {
        if (needToVerify == null) {
            return null;
        }

        if (containsAllAttributes(needToVerify.getAttributes())) {
            return this;
        }

        for (TreeMobileElement child : getChildrenElement()) {
            TreeMobileElement found = child.findBestMatch(needToVerify);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private boolean containsAllAttributes(Map<String, String> attributesToVerify) {
        if (attributesToVerify == null || attributesToVerify.isEmpty()) {
            return false;
        }
        Map<String, String> attributes = getAttributes();

        for (Entry<String, String> entryToVerify : attributesToVerify.entrySet()) {
            String key = entryToVerify.getKey();
            if (!attributes.containsKey(key) || !ObjectUtils.equals(attributes.get(key), entryToVerify.getValue())) {
                return false;
            }
        }
        return true;
    }

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

	public MobileElementEntity.LocatorStrategy getLocatorStrategy() {
		return locatorStrategy;
	}

	public void setLocatorStrategy(MobileElementEntity.LocatorStrategy locatorStrategy) {
		this.locatorStrategy = locatorStrategy;
	}
}
