package com.kms.katalon.platform;

import org.eclipse.e4.core.contexts.IEclipseContext;

public class ApplicationServiceImpl {
    
    private static IEclipseContext eclipseContext;
    
    private static final EventService eventService = new EventServiceImpl();
    
    public static void lookupEclipseContext(IEclipseContext context) {
        eclipseContext = context;
    }
    
    public static <T> T get(Class<T> clazz) {
        return eclipseContext.get(clazz);
    }
    
    public static EventService getEventService() {
        return eventService;
    }
}
