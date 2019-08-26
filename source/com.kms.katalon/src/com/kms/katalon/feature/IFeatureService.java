package com.kms.katalon.feature;

import com.kms.katalon.core.feature.models.Features;

/**
 * Interface for feature service used to handle
 * authentications and other actions relating to features.
 */
public interface IFeatureService {
    /**
     * Verify if the feature is available for this user
     * 
     * @param A {@link Features} enum
     * @return true if the feature is available, false otherwise
     */
    boolean canUse(Features feature);
}
