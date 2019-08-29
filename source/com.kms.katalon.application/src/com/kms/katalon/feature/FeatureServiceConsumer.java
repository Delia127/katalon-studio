package com.kms.katalon.feature;

public class FeatureServiceConsumer {
    private static IFeatureService serviceInstance;

    public synchronized static void setFeatureService(IFeatureService service) {
        serviceInstance = service;
    }

    /**
     * Clients use this method to obtain a registered {@link IFeatureService} implementation
     * 
     * @return {@link IFeatureService}
     */
    public static IFeatureService getServiceInstance() {
        if (serviceInstance == null) {
            serviceInstance = new SimpleFeatureService();
        }
        return serviceInstance;
    }

}
