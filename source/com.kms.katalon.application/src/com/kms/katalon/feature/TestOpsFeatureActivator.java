package com.kms.katalon.feature;

import java.util.Set;

import com.kms.katalon.license.models.OrganizationFeature;

public interface TestOpsFeatureActivator {
    Set<String> getFeatures(String serverUrl, String email, String password, long orgId, String ksVersion);

    String connect(String serverUrl, String email, String password) throws Exception;
    
    String getLicense(String serverUrl, String token, String username, String sessionId, String hostname,
            String machineId, OrganizationFeature organizationFeature) throws Exception;
}
