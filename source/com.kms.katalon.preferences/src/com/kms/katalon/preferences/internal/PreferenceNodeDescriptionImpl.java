package com.kms.katalon.preferences.internal;

import java.text.MessageFormat;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.jface.preference.PreferencePage;

import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.PreferenceNodeDescription;
import com.kms.katalon.preferences.PreferencePageBuilder;
import com.kms.katalon.preferences.constants.StringConstants;

@SuppressWarnings("restriction")
public class PreferenceNodeDescriptionImpl implements PreferenceNodeDescription {
    private final String bundleId;

    private final String nodeId;

    private final String nodeName;

    private final String parentNodeId;

    private final String preferencePageClassName;

    private final PreferencePageBuilderImpl builder;

    public PreferenceNodeDescriptionImpl(String bundleId, String nodeId, String nodeName, String parentNodeId,
            String preferencePageClassName) {
        this.bundleId = bundleId;
        this.nodeId = nodeId;
        this.parentNodeId = parentNodeId;
        this.nodeName = nodeName;
        this.preferencePageClassName = preferencePageClassName;
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
    public PreferencePageBuilder getBuilder() {
        return builder;
    }

    @Override
    public boolean hasPage() {
        return preferencePageClassName != null && !preferencePageClassName.isEmpty();
    }

    public class PreferencePageBuilderImpl implements PreferencePageBuilder {

        @Override
        public PreferencePage build(IContributionFactory factory, IEclipseContext context) {
            try {
                String prefPageURI = getClassURI(PreferenceNodeDescriptionImpl.this.bundleId,
                        PreferenceNodeDescriptionImpl.this.preferencePageClassName);
                Object object = factory.create(prefPageURI, context);
                if (object instanceof PreferencePage) {
                    return (PreferencePage) object;
                }
                LogUtil.logErrorMessage(
                        MessageFormat.format(StringConstants.INL_LOG_ERROR_EXPECTED_INSTANCE_OF_PREF_PAGE,
                                PreferenceNodeDescriptionImpl.this.preferencePageClassName));
                return null;
            } catch (ClassNotFoundException e) {
                LogUtil.printAndLogError(e);
                return null;
            }
        }

        private String getClassURI(String definingBundleId, String spec) throws ClassNotFoundException {
            if (spec.startsWith("platform:")) {
                return spec;
            } // $NON-NLS-1$
            return "bundleclass://" + definingBundleId + '/' + spec;
        }

    }
}
