package com.kms.katalon.execution.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.util.collections.Pair;

public class StringUtil {
    public static String wrap(String longString, int lineWidth) {
        List<String> flattenedStrings = flatten(longString, lineWidth);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < flattenedStrings.size(); i++) {
            builder.append(flattenedStrings.get(i));
            builder.append(GlobalStringConstants.CR_EOL);
        }
        builder.append(longString.substring(flattenedStrings.size() * lineWidth, longString.length()));
        return builder.toString();
    }

    private static List<String> flatten(String longString, int lineWidth) {
        List<String> childrenString = new ArrayList<>();
        int multiplier = 1;
        while (longString.length() > lineWidth * multiplier) {
            childrenString.add(longString.substring((multiplier - 1) * lineWidth, multiplier * lineWidth));
            multiplier++;
        }
        return childrenString;
    }
    
    /**
     * @param str String in the format of key1,value1;key2,value2;...
     * @return List&lt;Pair&lt;String, Boolean>>
     * @see #flatList
     * @see com.kms.katalon.util.collections.Pair
     */
    public static List<Pair<String, Boolean>> parseStringBooleanString(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Stream.of(str.split(";"))
                .map(i -> i.split(","))
                .map(i -> new Pair<String, Boolean>(i[0], Boolean.valueOf(i[1])))
                .collect(Collectors.toList());
    } 
}
