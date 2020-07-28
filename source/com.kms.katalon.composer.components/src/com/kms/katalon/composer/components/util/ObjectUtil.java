package com.kms.katalon.composer.components.util;

import java.lang.reflect.Field;

public class ObjectUtil {

    @SuppressWarnings("unchecked")
    public static <T> T clone(T original) {
        try {
            T clone = (T) original.getClass().newInstance();
            for (Field field : original.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(original));
            }
            return clone;
        } catch (Exception e) {
            return null;
        }
    }
}
