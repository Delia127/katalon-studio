package com.kms.katalon.core.main;

import groovy.lang.DelegatingMetaClass;

import com.kms.katalon.core.constants.StringConstants;

public class KeywordClassDelegatingMetaClass extends DelegatingMetaClass {
    private ScriptEngine scriptEngine;
    KeywordClassDelegatingMetaClass(final Class<?> clazz, ScriptEngine scriptEngine) {
        super(clazz);
        initialize();
        this.scriptEngine = scriptEngine;
    }

    private Class<?> getGlobalVariableClassByPropertyName(String propertyName) {
        try {
            if (StringConstants.GLOBAL_VARIABLE_CLASS_NAME.equals(propertyName)) {
                return scriptEngine.getGroovyClassLoader().loadClass(StringConstants.GLOBAL_VARIABLE_CLASS_NAME);
            }
        } catch (ClassNotFoundException e) {
            // Cannot find GlobalVariable so let default Meta Class decide what to return
        }
        return null;
    }

    @Override
    public Object getProperty(Object object, String property) {
        Class<?> globalVariableClass = getGlobalVariableClassByPropertyName(property);
        return globalVariableClass != null ? globalVariableClass : super.getProperty(object, property);
    }

    @Override
    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        Class<?> globalVariableClass = getGlobalVariableClassByPropertyName(propertyName);
        return globalVariableClass != null ? globalVariableClass : super.invokeMissingProperty(instance, propertyName,
                optionalValue, isGetter);
    }
}
