package com.kms.katalon.feature;

import java.util.HashSet;
import java.util.Set;

/**
 * Mock service
 */
public class SimpleFeatureService implements IFeatureService {

private Set<String> features = new HashSet<>();
    
    @Override
    public boolean canUse(String featureKey) {
        return features.contains(featureKey);
    }

    @Override
    public void enable(String featureKey) {
        features.add(featureKey);
    }

    @Override
    public void disable(String featureKey) {
        features.remove(featureKey);
    }

}
