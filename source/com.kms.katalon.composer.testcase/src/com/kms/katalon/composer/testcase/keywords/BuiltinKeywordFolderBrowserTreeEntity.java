package com.kms.katalon.composer.testcase.keywords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.custom.keyword.KeywordMethod;

public class BuiltinKeywordFolderBrowserTreeEntity extends KeywordBrowserFolderTreeEntity {
    private static final long serialVersionUID = 1L;
    private String className;
    private String simpleName;
    private String label;

    public BuiltinKeywordFolderBrowserTreeEntity(String className, String simpleName, String label,
            IKeywordBrowserTreeEntity parent) {
        super(simpleName, parent);
        this.className = className;
        setSimpleName(simpleName);
        this.label = label;
    }

    @Override
    public boolean hasChildren() {
        try {
            if (KeywordController.getInstance().getBuiltInKeywords(simpleName).size() > 0) {
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Override
    public Object[] getChildren() {
        try {
            return getKeywordByKeywordObject().toArray();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    private List<IKeywordBrowserTreeEntity> getKeywordByKeywordObject() throws Exception {
        List<KeywordMethod> allKeywordMethod = KeywordController.getInstance().getBuiltInKeywords(simpleName);
        Map<String, List<KeywordMethod>> methodObjectMap = new HashMap<String, List<KeywordMethod>>();
        for (KeywordMethod method : allKeywordMethod) {
            Keyword keywordParameter = method.getKeywordAnnotation();
            if (keywordParameter == null) {
                continue;
            }
            List<KeywordMethod> methodList = methodObjectMap.get(keywordParameter.keywordObject());
            if (methodList == null) {
                methodList = new ArrayList<KeywordMethod>();
                methodObjectMap.put(keywordParameter.keywordObject(), methodList);
            }
            methodList.add(method);
        }
        List<IKeywordBrowserTreeEntity> childTreeEntityList = new ArrayList<IKeywordBrowserTreeEntity>();
        Iterator<Entry<String, List<KeywordMethod>>> it = methodObjectMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, List<KeywordMethod>> pair = (Entry<String, List<KeywordMethod>>) it.next();
            KeywordBrowserFolderTreeEntity keywordFolder = new KeywordBrowserFolderTreeEntity(pair.getKey(), this);
            for (KeywordMethod method : pair.getValue()) {
                keywordFolder.children.add(new KeywordBrowserTreeEntity(className, simpleName, method.getName(), false,
                        keywordFolder));
            }
            childTreeEntityList.add(keywordFolder);
        }
        Collections.sort(childTreeEntityList, new Comparator<IKeywordBrowserTreeEntity>() {
            @Override
            public int compare(IKeywordBrowserTreeEntity o1, IKeywordBrowserTreeEntity o2) {
                if (o1 != null && o2 != null) {
                    return o1.getName().compareTo(o2.getName());
                }
                return 0;
            }
        });
        return childTreeEntityList;
    }

    @Override
    public String getName() {
        return label;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

}
