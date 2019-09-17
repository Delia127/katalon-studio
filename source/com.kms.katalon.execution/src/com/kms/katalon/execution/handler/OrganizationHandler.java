package com.kms.katalon.execution.handler;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.util.AnalyticsOrganization;
import com.kms.katalon.execution.util.Organization;

import com.kms.katalon.logging.LogUtil;

public class OrganizationHandler {

    public static Long getOrganizationId() {
        try {
            RunningMode mode = ApplicationRunningMode.get();
            if (mode == RunningMode.CONSOLE) {
                return Long.parseLong(Organization.getId());
            } else {
                AnalyticsOrganization org = new AnalyticsOrganization();
                String jsonObject = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ORGANIZATION);
                if (StringUtils.isNotBlank(jsonObject)) {
                    org = JsonUtil.fromJson(jsonObject, AnalyticsOrganization.class);
                }
                return org.getId();
            }
        } catch (Exception ex) {
            return null;
        }
    }
	
	public static void setOrgnizationIdToProject(String id) {
        Organization.setId(id);
    }

}
