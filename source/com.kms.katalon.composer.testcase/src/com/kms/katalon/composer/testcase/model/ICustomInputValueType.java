package com.kms.katalon.composer.testcase.model;

public interface ICustomInputValueType extends IInputValueType {
    public static final String TAG_ALL = "all";
    public static final String TAG_BINARY = "binary";
    public static final String TAG_BOOLEAN = "boolean";
    public static final String TAG_CLOSURE_LIST = "closureList";
    public static final String TAG_FOR = "for";
    public static final String TAG_KEYWORD_INPUT = "keywordInput";
    public static final String TAG_LIST = "list";
    public static final String TAG_MAP = "map";
    public static final String TAG_METHOD_CALL = "methodCall";
    public static final String TAG_RANGE = "range";
    public static final String TAG_TEST_DATA_VALUE = "testDataValue";
    public static final String TAG_TEST_OBJECT = "testObject";
    public static final String TAG_SWITCH = "switch";
    public static final String TAG_CASE = "case";
    public static final String TAG_THROW = "throw";
    
    public String[] getTags();

}
