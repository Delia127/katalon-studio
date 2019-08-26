package com.kms.katalon.execution.handler;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.util.OrganizationId;

public class OrganizationHandler {
	
	public static String getOrganizationId() {
		if (true) {
			return OrganizationId.get();
		} else {
			OrganizationId org = new OrganizationId();
			String jsonObject = ApplicationInfo.getAppProperty(ApplicationStringConstants.KA_ORGANIZATION);
			if (StringUtils.isNotBlank(jsonObject)) {
	            try {
	                 org = JsonUtil.fromJson(jsonObject, OrganizationId.class);
	            } catch (IllegalArgumentException e) {
	                // do nothing
	            }
	        }
	        return org.get();
		}
	}
	
}
