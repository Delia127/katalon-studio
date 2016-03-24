package com.kms.katalon.core.logging;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;


public enum LogLevel {
    START(1002),
    END(1003),
    PASSED(1000),
    INFO(1001),
    WARNING(1004),
    FAILED(1005),
    ERROR(1006),
    ABORTED(1009),
    INCOMPLETE(1010),
    RUN_DATA(2000);
    
    private final int value;
    private Level level;
    
    private LogLevel(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public Level getLevel() {
        if (level == null) {
            level = new InternalLogLevel(name(), getValue());;
        }
        return level;
    }
    
    public static LogLevel valueOf(Level level) {
        for (LogLevel logLevel : values()) {
            if (logLevel.getValue() == level.intValue()) {
                return logLevel;
            }
        }
        return null;        
    }
    
	public static Set<LogLevel> getResultLogs() {
	    Set<LogLevel> resultLogs = new LinkedHashSet<LogLevel>();
	    resultLogs.add(PASSED);
	    resultLogs.add(FAILED);
	    resultLogs.add(ERROR);
	    resultLogs.add(INCOMPLETE);
	    
	    return resultLogs;
	}
	
	private class InternalLogLevel extends Level {
        private static final long serialVersionUID = 7111238540539667071L;

        protected InternalLogLevel(String arg0, int arg1) {
            super(arg0, arg1);
        }
	}
}
