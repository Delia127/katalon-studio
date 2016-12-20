package com.kms.katalon.core.keyword.internal;

import org.apache.commons.lang.ObjectUtils

import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject

public abstract class AbstractKeyword implements IKeyword {

    protected static KeywordLogger logger = KeywordLogger.getInstance()

    protected TestObject getTestObject(Object param) {
        if (param instanceof TestObject) {
            return (TestObject) param
        }
        return ObjectRepository.findTestObject(ObjectUtils.toString(param))
    }
}
