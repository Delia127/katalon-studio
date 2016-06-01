package com.kms.katalon.composer.execution.launcher;

import java.util.List;

import org.eclipse.debug.core.ILaunch;

import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public interface ObservableLauncher extends ILauncher {
    
    void setObserved(boolean observed);
    
    boolean isObserved();

    LaunchMode getMode();

    String getDisplayMessage();

    void suspend();

    void resume();
    
    void addListener(IDELauncherListener l);

    void removeListener(IDELauncherListener l);

    List<XmlLogRecord> getLogRecords();
    
    ILaunch getLaunch();
}
