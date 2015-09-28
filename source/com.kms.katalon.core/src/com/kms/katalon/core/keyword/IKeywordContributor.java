package com.kms.katalon.core.keyword;

import com.kms.katalon.core.driver.IDriverCleaner;


public interface IKeywordContributor {
    public Class<?> getKeywordClass();
    public String getLabelName();
    public Class<? extends IDriverCleaner> getDriverCleaner();
}
