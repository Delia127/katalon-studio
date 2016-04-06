package com.kms.katalon.custom.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.keyword.KeywordContributorCollection;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.custom.keyword.KeywordMethod;

public class BuiltInMethodNodeFactory {
    private static List<KeywordClass> keywordClasses;
    public static final String CALL_TEST_CASE_METHOD_NAME = "callTestCase";
    
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
        List<KeywordMethod> methods = getFilteredMethods(className);
        if (methods.isEmpty()) {
            return null;
        }
        for (KeywordMethod method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static List<KeywordMethod> getFilteredMethods(String className) {
        List<KeywordMethod> methods = findMethods(className);
        for (int i = 0; i < methods.size(); i++) {
            if (methods.get(i).getName().equals(CALL_TEST_CASE_METHOD_NAME)) {
                methods.remove(i);
                break;
            }
        }
        return methods;
    }

    public static KeywordMethod findCallTestCaseMethod(String className) throws IOException {
        for (KeywordMethod method : findMethods(className)) {
            if (method.getName().equals(CALL_TEST_CASE_METHOD_NAME)) {
                return method;
            }
        }
        return null;
    }

    private static List<KeywordMethod> findMethods(String className) {
        KeywordClass keywordClass = findClass(className);
        if (keywordClass == null) {
            return Collections.emptyList();
        }
        return new ArrayList<KeywordMethod>(keywordClass.getKeywordMethods());
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
}
