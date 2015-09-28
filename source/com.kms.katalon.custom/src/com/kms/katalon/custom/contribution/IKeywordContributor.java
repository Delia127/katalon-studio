package com.kms.katalon.custom.contribution;

import com.kms.katalon.core.driver.IDriverCleaner;


public interface IKeywordContributor {
    public Class<?> getKeywordClass();
    public String getLabelName();
    public Class<? extends IDriverCleaner> getDriverCleaner();
}
