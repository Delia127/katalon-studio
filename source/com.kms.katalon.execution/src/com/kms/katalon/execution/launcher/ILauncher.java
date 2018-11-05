package com.kms.katalon.execution.launcher;

import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;

public interface ILauncher extends Executable {
    /**
     * @return Id of the current launcher with Date Time format: YYYYMMDD_HHMMSS
     * </br>
     */
    String getId();
    
    /**
     * Used for displaying.
     */
    String getName();
    
    /**
     * Represents what status the current is doing.
     */
    LauncherStatus getStatus();
    
    void setStatus(LauncherStatus status);
    
    /**
     * Provides an instance that describes the results of test cases of the current launcher.
     */
    ILauncherResult getResult();
    
    String getExecutionUUID();
}
