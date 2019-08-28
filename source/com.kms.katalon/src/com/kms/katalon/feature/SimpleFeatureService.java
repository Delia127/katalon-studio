package com.kms.katalon.feature;

/**
 * Mock service
 */
public class SimpleFeatureService implements IFeatureService {

    @Override
    public boolean canUse(String featureKey) {
        return true;
    }

}
