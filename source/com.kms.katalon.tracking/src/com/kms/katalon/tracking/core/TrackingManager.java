package com.kms.katalon.tracking.core;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;


import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class TrackingManager {
    
    private static final String TRACKING_TIME_ARGUMENT = "trackingTime";
    
    private static final int DEFAULT_TRACKING_TIME = 30*60; //seconds;
    
    private static TrackingManager instance;
    
    public static TrackingManager getInstance() {
        if (instance == null) {
            instance = new TrackingManager();
        }
        return instance;
    }
    
    // get tracking time from application arguments
    // if not available or parsable, return default value (30 minutes)
    public int getTrackingTime() {
        String trackingTimeValue = getTrackingTimeArgument();
        if (!StringUtils.isBlank(trackingTimeValue)) {
            try {
                return Integer.valueOf(trackingTimeValue);
            } catch (Exception e) {
                return DEFAULT_TRACKING_TIME;
            }
        } else {
            return DEFAULT_TRACKING_TIME;
        }
    }
    
    private String getTrackingTimeArgument() {
        String[] args = Platform.getApplicationArgs();
        
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        
        parser.accepts(TRACKING_TIME_ARGUMENT).withOptionalArg();
        
        OptionSet options = parser.parse(args);
        if (options.has(TRACKING_TIME_ARGUMENT)) {
            return (String) options.valueOf(TRACKING_TIME_ARGUMENT);
        }
        return StringUtils.EMPTY;
    }
}
