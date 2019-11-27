package com.kms.katalon.core.testobject;

import org.apache.commons.lang3.StringUtils;

public class MobileTestObject extends TestObject {

    private boolean useMobileLocator = false;
    
    private MobileLocatorStrategy mobileLocatorStrategy;

    private String mobileLocator;

    public MobileTestObject(String objectId) {
        super(objectId);
    }

    public String getMobileLocator() {
        return mobileLocator;
    }

    public void setMobileLocator(String mobileLocator) {
        this.mobileLocator = mobileLocator;
    }

    public MobileLocatorStrategy getMobileLocatorStrategy() {
        return mobileLocatorStrategy;
    }

    public void setMobileLocatorStrategy(MobileLocatorStrategy mobileLocatorStrategy) {
        this.mobileLocatorStrategy = mobileLocatorStrategy;
    }

    public boolean isUseMobileLocator() {
        return useMobileLocator;
    }

    public void setUseMobileLocator(boolean useMobileLocator) {
        this.useMobileLocator = useMobileLocator;
    }

    public static enum MobileLocatorStrategy {
        ACCESSIBILITY("Accessibility ID"),
        CLASS_NAME("Class Name"),
        ID("ID"),
        NAME("Name"),
        XPATH("XPATH"),
        IMAGE("Image"),
        ANDROID_UI_AUTOMATOR("Android UI Automator"),
        ANDROID_VIEWTAG("Android View Tag"),
        IOS_PREDICATE_STRING("iOS Predicate String"),
        IOS_CLASS_CHAIN("iOS Class Chain"),
        CUSTOM("Custom");

        private final String locatorStrategy;

        private MobileLocatorStrategy(String locatorStrategy) {
            this.locatorStrategy = locatorStrategy;
        }

        public String getLocatorStrategy() {
            return locatorStrategy;
        }
        
        public static MobileLocatorStrategy valueOfStrategy(String strategy) {
            if (StringUtils.isEmpty(strategy)) {
                return null;
            }
            for (MobileLocatorStrategy str : values()) {
                if (str.getLocatorStrategy().equals(strategy)) {
                    return str;
                }
            }
            throw new IllegalArgumentException("Strategy: " + strategy + " not found");
        }
    }
}
