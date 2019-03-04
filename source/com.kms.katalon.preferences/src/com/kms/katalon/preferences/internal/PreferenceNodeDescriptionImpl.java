package com.kms.katalon.preferences.internal;

import com.kms.katalon.preferences.PreferenceNodeDescription;

public class PreferenceNodeDescriptionImpl implements PreferenceNodeDescription {
    private final String bundleId;

    private final String nodeId;

    private final String nodeName;

    private final String parentNodeId;

    private final String preferencePageClassName;

    public PreferenceNodeDescriptionImpl(String bundleId, String nodeId, String nodeName, String parentNodeId,
            String preferencePageClassName) {
        this.bundleId = bundleId;
        this.nodeId = nodeId;
        this.parentNodeId = parentNodeId;
        this.nodeName = nodeName;
        this.preferencePageClassName = preferencePageClassName;
    }

    @Override
    public String getBundleId() {
        return bundleId;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getParentNodeId() {
        return parentNodeId;
    }

    @Override
    public String getPreferencePageClassName() {
        return preferencePageClassName;
    }
}
