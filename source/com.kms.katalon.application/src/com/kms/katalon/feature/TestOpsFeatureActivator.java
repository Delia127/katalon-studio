package com.kms.katalon.feature;

import java.util.Set;

public interface TestOpsFeatureActivator {
    Set<String> getFeatures(String serverUrl, String email, String password, long orgId, String ksVersion);
}
