package com.kms.katalon.custom.keyword;

import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.testobject.WindowsTestObject;

public class KeywordParameter {
    private String name;
    private Class<?> type;
    
    public KeywordParameter(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
    
    public boolean isGeneralParam() {
        return !isTestObjectParam() && !isFailureHandlingParam();
    }

    public boolean isTestObjectParam() {
        return TestObject.class.getName().equals(getType().getName());
    }
    
    public boolean isWindowsTestObjectParam() {
        return WindowsTestObject.class.getName().equals(getType().getName());
    }

    public boolean isFailureHandlingParam() {
        return FailureHandling.class.getName().equals(getType().getName());
    }
    
    public boolean isClassAssignable(String childClass) {
        try {
            return Class.forName(this.getType().getName()).isAssignableFrom(Class.forName(childClass));
        } catch (ClassNotFoundException cnf) {}
        return false;
    }
}
