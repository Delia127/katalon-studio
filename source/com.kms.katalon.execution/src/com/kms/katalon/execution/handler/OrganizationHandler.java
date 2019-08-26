package com.kms.katalon.execution.handler;

import com.kms.katalon.execution.util.Organization;

public class OrganizationHandler {
	
	public static void setOrgnizationIdToProject(String id) {
        Organization.setId(id);
    }

}
