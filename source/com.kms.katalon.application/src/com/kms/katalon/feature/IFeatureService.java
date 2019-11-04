package com.kms.katalon.feature;

/**
 * Interface for feature service used to handle
 * authentications and other actions relating to features.
 */
public interface IFeatureService {
    
    /**
     * Enable the feature for this user
     * 
     * @param featureKey Feature's key
     */
    void enable(String featureKey);
    
    /**
     * Disable the feature for this user
     * 
     * @param featureKey Feature's key
     */
    void disable(String featureKey);
    
    /**
     * Verify if the feature is available for this user
     * 
     * @param featureKey Feature's key
     * @return true if the feature is available, false otherwise
     */
    boolean canUse(String featureKey);

    
    /**
     * Clear all available features
     */
    void clear();
}
