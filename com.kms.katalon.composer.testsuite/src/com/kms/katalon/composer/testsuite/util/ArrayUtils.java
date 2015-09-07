package com.kms.katalon.composer.testsuite.util;

public class ArrayUtils {
    public static String[] arrayStringToArray(String arrayString) {
        return arrayString.replace("[", "").replace("]", "").split(", ");
    }
}
