package com.kms.katalon.composer.testcase.keywords;

import java.io.Serializable;

public interface IKeywordBrowserTreeEntity extends Serializable {
    public String getName();

    /**
     * Get TreeEntity readable name. This name is for display purpose.
     * 
     * @return (Custom) readable name
     */
    public String getReadableName();

    public String getToolTip();

    public boolean hasChildren();

    public Object[] getChildren();

    public IKeywordBrowserTreeEntity getParent();
}
