package com.kms.katalon.composer.testcase.keywords;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class KeywordBrowserTreeEntity implements IKeywordBrowserTreeEntity {
    private static final long serialVersionUID = 1L;

    private String simpleClassName;

    private boolean isCustom;

    private String keywordName;

    private IKeywordBrowserTreeEntity parent;
    
    private Class<?>[] parameterTypes;

    public KeywordBrowserTreeEntity(String fullClassName, String simpleClassName, String keywordName, boolean isCustom,
            IKeywordBrowserTreeEntity parent) {
        this(fullClassName, simpleClassName, keywordName, null, isCustom, parent);
    }
    
    public KeywordBrowserTreeEntity(String fullClassName, String simpleClassName, String keywordName,
            Class<?>[] parameterTypes, boolean isCustom, IKeywordBrowserTreeEntity parent) {
        this.simpleClassName = simpleClassName;
        this.keywordName = keywordName;
        this.isCustom = isCustom;
        this.parent = parent;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public String getName() {
        return keywordName;
    }

    @Override
    public String getToolTip() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if (isCustom || project == null) {
            return getName();
        }
        String keywordJavaDoc = TestCaseEntityUtil.getBuiltinKeywordJavadocText(simpleClassName, keywordName);
        if (keywordJavaDoc.isEmpty()) {
            return TreeEntityUtil.getReadableKeywordName(getName());
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

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
}
