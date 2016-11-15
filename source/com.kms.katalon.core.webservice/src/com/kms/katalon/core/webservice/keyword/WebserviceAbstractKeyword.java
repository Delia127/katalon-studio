package com.kms.katalon.core.webservice.keyword;

import com.kms.katalon.core.keyword.AbstractKeyword;
import com.kms.katalon.core.keyword.SupportLevel;

public abstract class WebserviceAbstractKeyword extends AbstractKeyword {
    
	@Override
	public SupportLevel getSupportLevel(Object ...params) {
		return SupportLevel.NOT_SUPPORT;
	}
}
