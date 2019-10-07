package com.kms.katalon.feature;

import java.util.Set;

public interface TestOpsFeatureActivator {
    Set<String> getFeatures(String serverUrl, String email, String password, long orgId, String ksVersion);

    String connect(String serverUrl, String email, String password) throws Exception;
    
    String getLicense(String serverUrl, String token, String username, String sessionId, String hostname,
            String machineId) throws Exception;
    
    void releaseLicense(String serverUrl, String machineId, String ksVersion, String sessionId,
            long orgId, String token) throws Exception;
    
    String getOrganization(String serverUrl, String token, long orgId) throws Exception;
}
