package com.kms.katalon.entity.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MobileElementEntity extends WebElementEntity {

    private static final long serialVersionUID = 2167108843763514730L;

    private String locator;

    private LocatorStrategy locatorStrategy = LocatorStrategy.XPATH;

    public LocatorStrategy getLocatorStrategy() {
        return locatorStrategy;
    }

    public void setLocatorStrategy(LocatorStrategy locatorStrategy) {
        this.locatorStrategy = locatorStrategy;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public static enum LocatorStrategy {
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

        private LocatorStrategy(String locatorStrategy) {
            this.locatorStrategy = locatorStrategy;
        }

        public String getLocatorStrategy() {
            return locatorStrategy;
        }
        
        public static String[] getStrategies() {
            List<String> strategies = new ArrayList<>();
            for (LocatorStrategy str : values()) {
                strategies.add(str.getLocatorStrategy());
            }
            return strategies.toArray(new String[0]);
        }
        
        public static LocatorStrategy valueOfStrategy(String strategy) {
            if (StringUtils.isEmpty(strategy)) {
                return null;
            }
            for (LocatorStrategy str : values()) {
                if (str.getLocatorStrategy().equals(strategy)) {
                    return str;
                }
            }
            throw new IllegalArgumentException("Strategy: " + strategy + " not found");
        }
    }
}
