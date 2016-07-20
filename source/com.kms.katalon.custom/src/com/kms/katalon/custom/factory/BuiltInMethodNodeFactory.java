package com.kms.katalon.custom.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.keyword.KeywordContributorCollection;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;

public class BuiltInMethodNodeFactory {
    private static List<KeywordClass> keywordClasses;

    public static final String CALL_TEST_CASE_METHOD_NAME = "callTestCase";

    private static Map<String, List<KeywordMethod>> filteredKeywordMethodsMap;

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

    public static KeywordMethod findMethod(String className, String methodName, String[] paramTypes) {
        List<KeywordMethod> keywordMethods = findKeywordMethodClassByName(className);
        if (keywordMethods == null) {
            return null;
        }
        KeywordMethod mostLikelyMethod = null;
        for (KeywordMethod kwMethod : keywordMethods) {
            if (!kwMethod.getName().equals(methodName)) {
                continue;
            }
            if (paramTypes == null) {
                return kwMethod;
            }
            if (mostLikelyMethod == null) {
                mostLikelyMethod = kwMethod;
            }
            if (kwMethod.getParameters().length != paramTypes.length) {
                continue;
            }
            mostLikelyMethod = kwMethod;
            if (kwMethod.checkParametersAssignable(paramTypes)) {
                return kwMethod;
            }
        }
        return mostLikelyMethod;
    }

    private static List<KeywordMethod> findKeywordMethodClassByName(String className) {
        List<KeywordMethod> keywords = getKeywordMethodsMap().get(className);
        if (keywords != null) {
            return keywords;
        }
        for (Entry<String, List<KeywordMethod>> keywordMethodMapEntry : getKeywordMethodsMap().entrySet()) {
            if (keywordMethodMapEntry.getKey().contains(className)) {
                return keywordMethodMapEntry.getValue();
            }
        }
        return Collections.emptyList();
    }

    public static List<KeywordMethod> getFilteredMethods(String className) {
        return new ArrayList<KeywordMethod>(findKeywordMethodClassByName(className));
    }

    public static List<KeywordMethod> getFilteredMethods(String className, boolean excludeFlowControl) {
        List<KeywordMethod> kwMethods = getFilteredMethods(className);
        if (excludeFlowControl) {
            List<KeywordMethod> excludedMethods = new ArrayList<KeywordMethod>();
            for (KeywordMethod kwMethod : kwMethods) {
                for (KeywordParameter kwParam : kwMethod.getParameters()) {
                    if (kwParam.isFailureHandlingParam()) {
                        excludedMethods.add(kwMethod);
                        break;
                    }
                }
            }
            kwMethods.removeAll(excludedMethods);
        }
        return kwMethods;
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

    private static Map<String, List<KeywordMethod>> getKeywordMethodsMap() {
        if (filteredKeywordMethodsMap == null) {
            initFilteredMethodMap();
        }
        return filteredKeywordMethodsMap;
    }

    private static void initFilteredMethodMap() {
        filteredKeywordMethodsMap = new HashMap<String, List<KeywordMethod>>();
        for (KeywordClass keywordClass : getKeywordClasses()) {
            List<KeywordMethod> keywordMethods = keywordClass.getKeywordMethods();
            Collections.sort(keywordMethods, new Comparator<KeywordMethod>() {
                @Override
                public int compare(KeywordMethod keywordMethod_1, KeywordMethod keywordMethod_2) {
                    int order = keywordMethod_1.getName().compareTo(keywordMethod_2.getName());
                    if (order == 0 && keywordMethod_1.getParameters() != null
                            && keywordMethod_2.getParameters() != null) {
                        return keywordMethod_1.getParameters().length - keywordMethod_2.getParameters().length;
                    }
                    return order;
                }
            });
            for (KeywordMethod method : keywordMethods) {
                if (method.getName().equals(CALL_TEST_CASE_METHOD_NAME)) {
                    keywordMethods.remove(method);
                    break;
                }
            }
            filteredKeywordMethodsMap.put(keywordClass.getName(), keywordMethods);
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
