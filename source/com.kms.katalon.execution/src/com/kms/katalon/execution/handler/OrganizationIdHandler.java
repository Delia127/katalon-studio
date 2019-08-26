package com.kms.katalon.execution.handler;

import com.kms.katalon.execution.util.OrganizationId;

public class OrganizationIdHandler {
    public static void setOrgnizationIdToProject(String id) {
        OrganizationId.set(id);
    }
}
