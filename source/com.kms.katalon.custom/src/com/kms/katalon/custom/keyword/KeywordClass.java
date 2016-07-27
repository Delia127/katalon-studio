package com.kms.katalon.custom.keyword;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.core.keyword.IKeywordContributor;

public class KeywordClass {
    private String name;
    private String simpleName;
    private String labelName;
    private String aliasName;
    private Class<?> type;
    private List<KeywordMethod> keywordMethods = new ArrayList<KeywordMethod>();

    public KeywordClass(IKeywordContributor keywordContributor) {
        name = keywordContributor.getKeywordClass().getName();
        simpleName = keywordContributor.getKeywordClass().getSimpleName();
        labelName = keywordContributor.getLabelName();
        aliasName = keywordContributor.getAliasName();
        type = keywordContributor.getKeywordClass();
        for (Method method : keywordContributor.getKeywordClass().getMethods()) {
            if (!isBuiltinMethod(method)) {
                continue;
            }
            keywordMethods.add(new KeywordMethod(method));
        }
        // Alphabet sort
        Collections.sort(keywordMethods, new Comparator<KeywordMethod>() {
            @Override
            public int compare(KeywordMethod o1, KeywordMethod o2) {
                if (o1 != null && o2 != null) {
                    return o1.getName().compareTo(o2.getName());
                }
                return 0;
            }
        });
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public List<KeywordMethod> getKeywordMethods() {
        return keywordMethods;
    }

    private static boolean isBuiltinMethod(Method method) {
        int modifiers = method.getModifiers();
        Annotation ann = method.getAnnotation(Keyword.class);
        if (Modifier.isPublic(modifiers) && ann != null) {
            return true;
        } else {
            return false;
        }
    }

    public String getLabelName() {
        return labelName;
    }

    public Class<?> getType() {
        return type;
    }

    public String getAliasName() {
        return aliasName;
    }
}
