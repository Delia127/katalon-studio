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
}
