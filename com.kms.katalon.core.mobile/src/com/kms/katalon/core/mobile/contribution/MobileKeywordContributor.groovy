package com.kms.katalon.core.mobile.contribution;


import groovy.transform.CompileStatic;

import com.kms.katalon.core.driver.IDriverCleaner;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords;

@CompileStatic
public class MobileKeywordContributor implements IKeywordContributor {
    @Override
    public Class<?> getKeywordClass() {
        return MobileBuiltInKeywords.class;
    }

    @Override
    public String getLabelName() {
        return StringConstants.CONTR_LBL_MOBILE_KEYWORD;
    }

	@Override
	public Class<? extends IDriverCleaner> getDriverCleaner() {
		return null;
	}
}
