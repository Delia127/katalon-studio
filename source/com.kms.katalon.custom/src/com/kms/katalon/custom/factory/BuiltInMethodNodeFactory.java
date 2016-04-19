package com.kms.katalon.custom.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.keyword.KeywordContributorCollection;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;

public class BuiltInMethodNodeFactory {
    private static List<KeywordClass> keywordClasses;

    public static final String CALL_TEST_CASE_METHOD_NAME = "callTestCase";

    private static Map<String, Map<String, KeywordMethod>> filteredKeywordMethodsMap;

    private static Map<String, KeywordMethod> callTestCaseKeywordMethodMap;

    private static void initKeywordClasses() {
        List<IKeywordContributor> keywordContributors = KeywordContributorCollection.getKeywordContributors();
        keywordClasses = new ArrayList<KeywordClass>();
        Collections.sort(keywordContributors, new Comparator<IKeywordContributor>() {
            @Override
            public int compare(IKeywordContributor o1, IKeywordContributor o2) {
                if (o1 != null && o2 != null) {
                    return o1.getPreferredOrder() - o2.getPreferredOrder();
                }
                return 0;
            }
        });
        for (IKeywordContributor keywordContributor : keywordContributors) {
            keywordClasses.add(new KeywordClass(keywordContributor));
        }
    }

    public static List<KeywordClass> getKeywordClasses() {
        if (keywordClasses == null) {
            initKeywordClasses();
        }
        return keywordClasses;
    }

    public static KeywordMethod findMethod(String className, String methodName) {
        Map<String, KeywordMethod> keywordMethodMap = findKeywordMethodClassByName(className);
        if (keywordMethodMap == null) {
            return null;
        }
        return keywordMethodMap.get(methodName);
    }

    private static Map<String, KeywordMethod> findKeywordMethodClassByName(String className) {
        Map<String, KeywordMethod> keywordMap = getKeywordMethodsMap().get(className);
        if (keywordMap != null) {
            return keywordMap;
        }
        for (Entry<String, Map<String, KeywordMethod>> keywordMethodMapEntry : getKeywordMethodsMap().entrySet()) {
            if (keywordMethodMapEntry.getKey().contains(className)) {
                return keywordMethodMapEntry.getValue();
            }
        }
        return Collections.emptyMap();
    }

    public static List<KeywordMethod> getFilteredMethods(String className) {
        return new ArrayList<KeywordMethod>(findKeywordMethodClassByName(className).values());
    }

    public static KeywordMethod findCallTestCaseMethod(String className) {
        KeywordMethod keywordMethod = getCallTestCaseKeywordMethodMap().get(className);
        if (keywordMethod != null) {
            return keywordMethod;
        }
        for (Entry<String, KeywordMethod> keywordMethodMapEntry : getCallTestCaseKeywordMethodMap().entrySet()) {
            if (keywordMethodMapEntry.getKey().contains(className)) {
                return keywordMethodMapEntry.getValue();
            }
        }
        return null;
    }

    public static KeywordClass findClass(String keywordClassName) {
        for (KeywordClass keywordClass : getKeywordClasses()) {
            if (!keywordClass.getName().equals(keywordClassName)
                    && !keywordClass.getSimpleName().equals(keywordClassName)) {
                continue;
            }
            return keywordClass;
        }
        return null;
    }

    private static Map<String, Map<String, KeywordMethod>> getKeywordMethodsMap() {
        if (filteredKeywordMethodsMap == null) {
            initFilteredMethodMap();
        }
        return filteredKeywordMethodsMap;
    }

    private static void initFilteredMethodMap() {
        filteredKeywordMethodsMap = new HashMap<String, Map<String, KeywordMethod>>();
        for (KeywordClass keywordClass : getKeywordClasses()) {
            Map<String, KeywordMethod> keywordMethodMap = new LinkedHashMap<String, KeywordMethod>();
            List<KeywordMethod> keywordMethods = keywordClass.getKeywordMethods();
            Collections.sort(keywordMethods, new Comparator<KeywordMethod>() {
                @Override
                public int compare(KeywordMethod keywordMethod_1, KeywordMethod keywordMethod_2) {
                    return keywordMethod_1.getName().compareTo(keywordMethod_2.getName());
                }
            });
            for (KeywordMethod method : keywordMethods) {
                if (method.getName().equals(CALL_TEST_CASE_METHOD_NAME)) {
                    continue;
                }
                keywordMethodMap.put(method.getName(), method);
            }
            filteredKeywordMethodsMap.put(keywordClass.getName(), keywordMethodMap);
        }
    }

    private static Map<String, KeywordMethod> getCallTestCaseKeywordMethodMap() {
        if (callTestCaseKeywordMethodMap == null) {
            initCallTestCaseKeywordMethodMap();
        }
        return callTestCaseKeywordMethodMap;
    }

    private static void initCallTestCaseKeywordMethodMap() {
        callTestCaseKeywordMethodMap = new HashMap<String, KeywordMethod>();
        for (KeywordClass keywordClass : getKeywordClasses()) {
            for (KeywordMethod method : keywordClass.getKeywordMethods()) {
                if (method.getName().equals(CALL_TEST_CASE_METHOD_NAME)) {
                    callTestCaseKeywordMethodMap.put(keywordClass.getName(), method);
                    break;
                }
            }
        }
    }
}
