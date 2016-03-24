package com.kms.katalon.execution.logging;

import com.kms.katalon.execution.launcher.IWatcher;


public abstract class AbstractLogWatcher implements IWatcher {
    protected static final String[] IGNORED_LIST = new String[] { "<log>", "</log>", 
        "<?xml", 
        "<!DOCTYPE log SYSTEM \"logger.dtd\">"};
    
    protected static final String TAG_END_RECORD = "</record>";

    protected static final String LINE_SEPERATOR = System.getProperty("line.separator");
    
    protected String header;
    protected int lineCount;
    protected int delayInMillis;
    
    protected boolean stopSignal;

    public AbstractLogWatcher(int delay) {
        this.delayInMillis = delay;
        this.header = "";
        this.stopSignal = false;
    }

    protected boolean isIgnoredLine(String line) {
        for (String ignoredString : IGNORED_LIST) {
            if (line.equals(ignoredString) || line.startsWith(ignoredString)) {
                return true;
            }
        }
        return false;
    }
    
    protected String prepareString(StringBuilder builder) {
        return builder.insert(0, header + IGNORED_LIST[0]).append(IGNORED_LIST[1]).toString();
    }

    public boolean isStopSignal() {
        return stopSignal;
    }

    public void stop() {
        this.stopSignal = true;
    }
}
