package com.kms.katalon.composer.testcase.keywords;

import java.io.Serializable;


public interface IKeywordBrowserTreeEntity extends Serializable {
	public String getName();
	
	public String getToolTip();
	
	public boolean hasChildren();
	
	public Object[] getChildren();
	
	public IKeywordBrowserTreeEntity getParent();
}
