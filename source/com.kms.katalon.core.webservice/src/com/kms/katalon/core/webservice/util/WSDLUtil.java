package com.kms.katalon.core.webservice.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.extensions.ExtensibilityElement;

public class WSDLUtil {

    public static <T extends ExtensibilityElement> T getExtensiblityElement(List<?> list, Class<T> clazz) {
        List<T> elements = getExtensiblityElements(list, clazz);
        return elements.isEmpty() ? null : elements.get(0);
    }

    public static <T extends ExtensibilityElement> List<T> getExtensiblityElements(List list, Class<T> clazz) {
        List<T> result = new ArrayList<T>();

        for (Iterator<T> i = list.iterator(); i.hasNext(); ) {
            T elm = i.next();
            if (clazz.isAssignableFrom(elm.getClass())) {
                result.add(elm);
            }
        }

        return result;
    }
}
