package com.kms.katalon.composer.testcase.keywords;

import java.util.ArrayList;
import java.util.List;

public class KeywordBrowserFolderTreeEntity implements IKeywordBrowserTreeEntity {
    protected static final long serialVersionUID = 1L;

    protected String name;

    protected IKeywordBrowserTreeEntity parent;

    protected List<IKeywordBrowserTreeEntity> children;

    public KeywordBrowserFolderTreeEntity(String name, IKeywordBrowserTreeEntity parent) {
        this.name = name;
        this.parent = parent;
        this.children = new ArrayList<IKeywordBrowserTreeEntity>();
    }

    public KeywordBrowserFolderTreeEntity(String name, IKeywordBrowserTreeEntity parent,
            List<IKeywordBrowserTreeEntity> children) {
        this.name = name;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getToolTip() {
        return getName();
    }

    @Override
    public IKeywordBrowserTreeEntity getParent() {
        return parent;
    }

    @Override
    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    @Override
    public Object[] getChildren() {
        return children.toArray();
    }

    public void setChildren(List<IKeywordBrowserTreeEntity> children) {
        this.children = children;
    }
    
    public void appendNewChildren(IKeywordBrowserTreeEntity newChildren) {
        this.children.add(newChildren);
    }
}
