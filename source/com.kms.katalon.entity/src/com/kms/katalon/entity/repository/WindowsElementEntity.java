package com.kms.katalon.entity.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.entity.file.FileEntity;

public class WindowsElementEntity extends FileEntity {

    private static final long serialVersionUID = -1579378597063358564L;

    public static final String FILE_EXTENSION = ".wrs";
    
    private List<WebElementPropertyEntity> properties = new ArrayList<>();

    private String locator;

    private LocatorStrategy locatorStrategy = LocatorStrategy.XPATH;

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    public List<WebElementPropertyEntity> getProperties() {
        return properties;
    }

    public void setProperties(List<WebElementPropertyEntity> properties) {
        this.properties = properties;
    }

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
        ACCESSIBILITY_ID("Accessibility ID"),
        CLASS_NAME("Class Name"),
        ID("ID"),
        NAME("Name"),
        TAG_NAME("Tag Name"),
        XPATH("XPATH");

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
