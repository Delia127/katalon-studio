package com.kms.katalon.execution.launcher;

public interface IWatcher extends Runnable {
    public static final int DF_TIME_OUT_IN_MILLIS = 10;
    
    void stop();
}
