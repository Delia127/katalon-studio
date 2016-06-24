package com.kms.katalon.composer.execution.launcher;

import java.util.List;

import org.eclipse.debug.core.ILaunch;

import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.launcher.ObservableLauncher;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public interface IDEObservableLauncher extends ObservableLauncher {
    
    void setObserved(boolean observed);
    
    boolean isObserved();

    LaunchMode getMode();

    String getDisplayMessage();

    void suspend();

    void resume();

    List<XmlLogRecord> getLogRecords();
    
    ILaunch getLaunch();
}
