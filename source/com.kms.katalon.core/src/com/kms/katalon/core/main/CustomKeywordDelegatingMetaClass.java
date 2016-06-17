package com.kms.katalon.core.main;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.kms.katalon.core.logging.ErrorCollector;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.util.ExceptionsUtil;

public class CustomKeywordDelegatingMetaClass extends DelegatingMetaClass {
    private GroovyClassLoader groovyClassLoader;

    private ErrorCollector errorCollector = ErrorCollector.getCollector();

    CustomKeywordDelegatingMetaClass(final Class<?> clazz, GroovyClassLoader groovyClassLoader) {
        super(clazz);
        initialize();
        this.groovyClassLoader = groovyClassLoader;
    }

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        List<Throwable> oldErrors = errorCollector.getCoppiedErrors();
        try {
            errorCollector.clearErrors();
            
            int classAndMethodSeparatorIndex = methodName.lastIndexOf(".");
            String customKeywordClassName = methodName.substring(0, classAndMethodSeparatorIndex);
            Class<?> customKeywordClass = getCustomKeywordClassAndSetMetaClass(customKeywordClassName);
            GroovyObject obj = (GroovyObject) customKeywordClass.newInstance();

            String customKeywordMethodName = methodName.substring(classAndMethodSeparatorIndex + 1, methodName.length());
            Object result = obj.invokeMethod(customKeywordMethodName, arguments);

            if (errorCollector.containsErrors()) {
                Throwable throwable = errorCollector.getFirstError();

                KeywordLogger.getInstance().logMessage(ErrorCollector.fromError(throwable),
                        ExceptionsUtil.getMessageForThrowable(throwable));
            } else {
                KeywordLogger.getInstance().logMessage(LogLevel.PASSED, methodName + " is PASSED");
            }

            return result;
        } catch (Throwable throwable) {
            errorCollector.addError(throwable);

            Throwable errorToThrow = errorCollector.getFirstError();
            if (errorToThrow != null) {
                ErrorCollector.throwError(errorToThrow);
            }
            return null;
        } finally {
            // return previous errors to error collector
            errorCollector.getErrors().addAll(0, oldErrors);
        }
    }

    private Class<?> getCustomKeywordClassAndSetMetaClass(String customKeywordClassName) throws ClassNotFoundException {
        Class<?> customKeywordClass = groovyClassLoader.loadClass(customKeywordClassName);

        MetaClass keywordMetaClass = InvokerHelper.metaRegistry.getMetaClass(customKeywordClass);
        if (!(keywordMetaClass instanceof KeywordClassDelegatingMetaClass)) {
            InvokerHelper.metaRegistry.setMetaClass(customKeywordClass, new KeywordClassDelegatingMetaClass(
                    customKeywordClass, groovyClassLoader));
        }
        return customKeywordClass;
    }
}
