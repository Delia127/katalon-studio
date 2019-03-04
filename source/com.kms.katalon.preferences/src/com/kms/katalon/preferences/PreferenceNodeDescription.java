package com.kms.katalon.preferences;

public interface PreferenceNodeDescription {
    String getBundleId();
    
    String getNodeId();
    
    String getNodeName();

    String getParentNodeId();
    
    String getPreferencePageClassName();
}
