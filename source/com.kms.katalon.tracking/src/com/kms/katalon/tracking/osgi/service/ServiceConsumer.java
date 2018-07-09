package com.kms.katalon.tracking.osgi.service;

public class ServiceConsumer {
    private static IProjectStatisticsCollector projectStatisticsCollector;
    
    public static IProjectStatisticsCollector getProjectStatisticsCollector() {
        return projectStatisticsCollector;
    }
    
    public synchronized static void setA(IProjectStatisticsCollector collector) {
        projectStatisticsCollector = collector;
    }
    
    public synchronized static void unsetA(IProjectStatisticsCollector collector) {
        if (projectStatisticsCollector == collector) {
            projectStatisticsCollector = null;
        }
    }
}
