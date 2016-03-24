package com.kms.katalon.execution.launcher;

import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.model.LauncherStatus;

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
     * Represents system and users's configuration that is prepared to launch this launcher. 
     */
    IRunConfiguration getRunConfig();
    
    /**
     * Represents what status the current is doing.
     */
    LauncherStatus getStatus();
    
    void setStatus(LauncherStatus status);
    
    /**
     * Provides an instance that describes the results of test cases of the current launcher.
     */
    ILauncherResult getResult();
}
