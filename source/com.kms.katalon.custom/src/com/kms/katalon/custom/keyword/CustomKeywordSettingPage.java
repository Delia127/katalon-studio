package com.kms.katalon.custom.keyword;

import java.util.List;

public class CustomKeywordSettingPage {
    
    private String name;

    private List<SettingPageComponent> components;

    public static class SettingPageComponent {        
        private String key;

        private String type;
        
        private String label;
        
        private String defaultValue;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    public List<SettingPageComponent> getComponents() {
        return components;
    }

    public void setComponents(List<SettingPageComponent> components) {
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
