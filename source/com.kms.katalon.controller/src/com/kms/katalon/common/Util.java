package com.kms.katalon.common;

public class Util {
    public static final String[] PAGELOAD_TIMEOUT_VALUES = {"Default", "10", "30", "60", "90"};
    public static final String PAGELOAD_TIMEOUT_DEFAULT = PAGELOAD_TIMEOUT_VALUES[0];
    public static final short PAGELOAD_TIMEOUT_MIN_VALUE = 0;
    public static final short PAGELOAD_TIMEOUT_MAX_VALUE = 9999;
}
