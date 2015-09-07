package com.kms.katalon.composer.testcase.keywords;


public class KeywordBrowserTreeEntity implements IKeywordBrowserTreeEntity {
	private static final long serialVersionUID = 1L;
	private String className;
	private boolean isCustom;
	private String keywordName;
	private IKeywordBrowserTreeEntity parent;
	
	public KeywordBrowserTreeEntity(String className, String keywordName, boolean isCustom, IKeywordBrowserTreeEntity parent) {
		setClassName(className);
		this.keywordName = keywordName;
		setCustom(isCustom);
		this.parent = parent;
	}
	
	@Override
	public String getName() {
		return keywordName;
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isCustom() {
		return isCustom;
	}

	public void setCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}

	@Override
	public IKeywordBrowserTreeEntity getParent() {
		return parent;
	}
	
}
