package com.kms.katalon.execution.launcher;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ILaunchProcessor {
    public Process execute(File scripFile) throws IOException;

    String[] getClasspath();
    
    Map<String, String> getEnviromentVariables() throws IOException;
}
