package com.kms.katalon.activation;

public class ActivationServiceConsumer {
    private static ActivationService serviceInstance;
    
    public synchronized void setActivationService(ActivationService service) {
        serviceInstance = service;
    }

    public static ActivationService getServiceInstance() {
        return serviceInstance;
    }
}
