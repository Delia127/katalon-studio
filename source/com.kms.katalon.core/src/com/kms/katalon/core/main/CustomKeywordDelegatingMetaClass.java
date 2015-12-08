package com.kms.katalon.core.main;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyObject;

import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.ErrorCollector;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.util.ExceptionsUtil;

public class CustomKeywordDelegatingMetaClass extends DelegatingMetaClass {
	CustomKeywordDelegatingMetaClass(final Class<?> aclass) {
		super(aclass);
		initialize();
	}

	@Override
	public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
		List<Throwable> oldErrors = ErrorCollector.getCollector().getCoppiedErrors();
		try {
			ErrorCollector.getCollector().clearErrors();
			int index = methodName.lastIndexOf(".");
			String customKeywordClassName = methodName.substring(0, index);
			String customKeywordMethodName =  methodName.substring(index + 1, methodName.length());
			Class<?> customKeywordClass = Class.forName(customKeywordClassName);
			
			InvokerHelper.metaRegistry.setMetaClass(customKeywordClass, new KeywordClassDelegatingMetaClass(
					customKeywordClass));
			GroovyObject obj = (GroovyObject) customKeywordClass.newInstance();	
	        Object result = obj.invokeMethod(customKeywordMethodName, arguments);
	           
            if (ErrorCollector.getCollector().containsErrors()) {
                Throwable throwable = ErrorCollector.getCollector().getFirstError();
                if (throwable.getClass().getName().equals(StepFailedException.class.getName())
                        || throwable instanceof AssertionError) {
                    KeywordLogger.getInstance().logMessage(LogLevel.FAILED,
                            ExceptionsUtil.getMessageForThrowable(throwable));
                } else {
                    KeywordLogger.getInstance()
                            .logMessage(LogLevel.ERROR, ExceptionsUtil.getMessageForThrowable(throwable));
                }
            } else {
                KeywordLogger.getInstance().logMessage(LogLevel.PASSED, methodName + " is PASSED");
            }
            
	        return result;
		} catch (Throwable throwable) {
			ErrorCollector.getCollector().addError(throwable);		

			Throwable errorToThrow = ErrorCollector.getCollector().getFirstError();
			if (errorToThrow != null) {
				ErrorCollector.throwError(errorToThrow);
			}
			return null;
		} finally {
	        //return previous errors to error collector
			ErrorCollector.getCollector().getErrors().addAll(0, oldErrors);
		}
	}
}
