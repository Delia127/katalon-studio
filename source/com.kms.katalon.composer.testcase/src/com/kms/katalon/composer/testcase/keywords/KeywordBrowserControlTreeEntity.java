package com.kms.katalon.composer.testcase.keywords;

public class KeywordBrowserControlTreeEntity implements IKeywordBrowserTreeEntity {
    private static final long serialVersionUID = 2322349837906808313L;
    private int controlStatementId;
    private String displayName;
    private KeywordBrowserFolderTreeEntity parentTreeEntity;

    public KeywordBrowserControlTreeEntity(int controlStatementId, String displayName,
            KeywordBrowserFolderTreeEntity parentTreeEntity) {
        this.setControlStatementId(controlStatementId);
        this.displayName = displayName;
        this.parentTreeEntity = parentTreeEntity;
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public String getToolTip() {
        return getName();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public Object[] getChildren() {
        return null;
    }

    @Override
    public IKeywordBrowserTreeEntity getParent() {
        return parentTreeEntity;
    }

    public int getControlStatementId() {
        return controlStatementId;
    }

    public void setControlStatementId(int controlStatementId) {
        this.controlStatementId = controlStatementId;
    }

}
