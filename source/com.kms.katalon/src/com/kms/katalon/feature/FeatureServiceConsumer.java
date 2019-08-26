package com.kms.katalon.feature;

public class FeatureServiceConsumer {
    private static IFeatureService serviceInstance;

    /**
     * This method is called by this bundle activator's service listener
     * every time a {@link IFeatureService} implementation is registered in Eclipse's service registry.
     * <p>
     * Clients should not call this method explicitly.
     * </p>
     * 
     * @param service
     */
    public synchronized static void setFeatureService(IFeatureService service) {
        serviceInstance = service;
    }

    /**
     * Clients use this method to obtain a registered {@link IFeatureService} implementation
     * 
     * @return {@link IFeatureService} or null if none exists and registered
     */
    public static IFeatureService getServiceInstance() {
        return serviceInstance;
    }

}
