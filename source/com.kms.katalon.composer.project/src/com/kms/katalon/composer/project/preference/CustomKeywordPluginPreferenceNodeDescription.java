package com.kms.katalon.composer.project.preference;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.jface.preference.PreferencePage;

import com.kms.katalon.custom.keyword.KeywordsManifest;
import com.kms.katalon.preferences.PreferenceNodeDescription;
import com.kms.katalon.preferences.PreferencePageBuilder;

@SuppressWarnings("restriction")
public class CustomKeywordPluginPreferenceNodeDescription implements PreferenceNodeDescription {

    private final String bundleId;

    private final String nodeId;

    private final String nodeName;

    private final String parentNodeId;

    private final PreferencePageBuilderImpl builder;

    private final KeywordsManifest keywordManifest;

    public CustomKeywordPluginPreferenceNodeDescription(String bundleId, String nodeId, String nodeName,
            String parentNodeId, KeywordsManifest keywordManifest) {
        this.bundleId = bundleId;
        this.nodeId = nodeId;
        this.parentNodeId = parentNodeId;
        this.nodeName = nodeName;
        this.keywordManifest = keywordManifest;
        this.builder = new PreferencePageBuilderImpl();
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
    public boolean hasPage() {
        return keywordManifest != null 
                && keywordManifest.getConfiguration() != null
                && keywordManifest.getConfiguration().getSettingPage() != null
                && keywordManifest.getConfiguration().getSettingId() != null;
    }

    @Override
    public PreferencePageBuilder getBuilder() {
        return builder;
    }

    public class PreferencePageBuilderImpl implements PreferencePageBuilder {

        @Override
        public PreferencePage build(IContributionFactory contributionFactory, IEclipseContext context) {
            return new CustomKeywordPluginPreferencePage(keywordManifest);
        }

    }
}
