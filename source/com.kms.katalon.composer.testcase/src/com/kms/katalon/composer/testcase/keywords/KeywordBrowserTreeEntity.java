package com.kms.katalon.composer.testcase.keywords;

import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;

public class KeywordBrowserTreeEntity implements IKeywordBrowserTreeEntity {
    private static final long serialVersionUID = 1L;
    private String fullClassName;
    private String simpleClassName;
    private boolean isCustom;
    private String keywordName;
    private IKeywordBrowserTreeEntity parent;

    public KeywordBrowserTreeEntity(String fullClassName, String simpleClassName, String keywordName, boolean isCustom,
            IKeywordBrowserTreeEntity parent) {
        this.fullClassName = fullClassName;
        this.simpleClassName = simpleClassName;
        this.keywordName = keywordName;
        this.isCustom = isCustom;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return keywordName;
    }

    @Override
    public String getToolTip() {
        if (isCustom) {
            return getName();
        }
        String keywordJavaDoc = TestCaseEntityUtil.getKeywordJavaDocText(fullClassName, keywordName, null);
        if (keywordJavaDoc.isEmpty()) {
            return getName();
        } else {
            return keywordJavaDoc;
        }
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public Object[] getChildren() {
        return null;
    }

    public String getClassName() {
        return simpleClassName;
    }
    
    public boolean isCustom() {
        return isCustom;
    }
    
    @Override
    public IKeywordBrowserTreeEntity getParent() {
        return parent;
    }

}
