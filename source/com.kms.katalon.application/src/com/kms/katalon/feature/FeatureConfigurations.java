package com.kms.katalon.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.logging.LogUtil;

public class FeatureConfigurations implements IFeatureService {

    private Properties coreFeatures;

    private Properties customFeatures;

    public static final String FEATURES_RESOURCE_FILE = "resources/features/features.properties";

    public InputStream getFeaturesResource() {
        return getClass().getClassLoader().getResourceAsStream(FEATURES_RESOURCE_FILE);
    }

    private static final String TRUE_VALUE = "true";

    private static final String FALSE_VALUE = "false";

    public FeatureConfigurations() {
        loadFeatures();
    }

    protected void loadFeatures() {
        loadCoreFeatures();
        loadCustomFeatures();
    }

    protected void loadCoreFeatures() {
        if (coreFeatures == null) {
            coreFeatures = new Properties();
        }
        try {
            coreFeatures.load(getFeaturesResource());
        } catch (IOException error) {
            LogUtil.logError(error);
        }
    }

    protected void loadCustomFeatures() {
        if (customFeatures == null) {
            customFeatures = new Properties();
        }
    }

    public boolean canUse(KSEFeature feature) {
        return canUse(feature.name());
    }

    @Override
    public boolean canUse(String featureKey) {
        boolean isCustomFeature = customFeatures.containsKey(featureKey);
        if (isCustomFeature && getBoolean(customFeatures, featureKey)) {
            return true;
        }

        boolean isValidFeature = coreFeatures.containsKey(featureKey);
        if (!isValidFeature) {
            return false;
        }

        boolean hasKSELicense = LicenseUtil.isNotFreeLicense();
        boolean isKSEFeature = getBoolean(coreFeatures, featureKey);
        boolean isFreeFeature = !isKSEFeature;
        return isFreeFeature || (isKSEFeature && hasKSELicense);
    }

    @Override
    public void enable(String customFeature) {
        customFeatures.setProperty(customFeature, TRUE_VALUE);
    }

    @Override
    public void disable(String customFeature) {
        customFeatures.setProperty(customFeature, FALSE_VALUE);
    }

    @Override
    public void clear() {
        customFeatures.clear();
    }

    private boolean getBoolean(Properties features, String featureKey) {
        return TRUE_VALUE.equalsIgnoreCase(features.getProperty(featureKey));
    }
}
