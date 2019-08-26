package com.kms.katalon.feature;

/**
 * Interface for feature service used to handle
 * authentications and other actions relating to features.
 */
public interface IFeatureService {
    /**
     * Verify if the feature is available for this user
     * 
     * @param featureKey Feature's key
     * @return true if the feature is available, false otherwise
     */
    boolean canUse(String featureKey);
}
