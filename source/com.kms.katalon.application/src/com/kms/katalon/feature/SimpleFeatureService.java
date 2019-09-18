package com.kms.katalon.feature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Mock service
 */
public class SimpleFeatureService implements IFeatureService {

    private Set<String> _features = new HashSet<>();
    
    private Set<String> features = Collections.synchronizedSet(_features);
    
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

    @Override
    public void clear() {
        features.clear();
    }

}
