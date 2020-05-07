package com.kms.katalon.util;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveAwareTypeUtil {

    private static Map<String, String> primitivesToWrappers = new HashMap<String, String>() {{
        put(boolean.class.getName(), Boolean.class.getName());
        put(char.class.getName(), Character.class.getName());
        put(byte.class.getName(), Byte.class.getName());
        put(short.class.getName(), Short.class.getName());
        put(int.class.getName(), Integer.class.getName());
        put(long.class.getName(), Long.class.getName());
        put(float.class.getName(), Float.class.getName());
        put(double.class.getName(), Double.class.getName());
        put(void.class.getName(), Void.class.getName());
    }};
    
    public static boolean isSameType(String first, String second) {
        if (first.equals(second)) {
            return true;
        }
        
        if (primitivesToWrappers.containsKey(first) && primitivesToWrappers.get(first).equals(second)) {
            return true;
        }
        
        if (primitivesToWrappers.containsKey(second) && primitivesToWrappers.get(second).equals(first)) {
            return true;
        }
        
        return false;

    }
    
    public static boolean areSameTypes(String[] first, String[] second) {
        if (first.length != second.length) {
            return false;
        }
        
        for (int i = 0; i < first.length; i++) {
            if (!isSameType(first[i], second[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean areSameTypesWithHeuristicMatching(String[] first, String[] second) {
        if (first.length != second.length) {
            return false;
        }
        
        for (int i = 0; i < first.length; i++) {
            boolean typeMatching = false;
            
            if (isSameType(first[i], second[i]) ||
                    first[i].endsWith(second[i]) ||
                    second[i].endsWith(first[i])) {
                typeMatching = true;
            }
            
            if (!typeMatching) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean isPrimitive(String type) {
        return primitivesToWrappers.containsKey(type);
    }
    
    public static String getWrapper(String primitiveType) {
        return primitivesToWrappers.get(primitiveType);
    }
}
