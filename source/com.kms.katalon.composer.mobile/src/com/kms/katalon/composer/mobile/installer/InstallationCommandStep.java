package com.kms.katalon.composer.mobile.installer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;

public class InstallationCommandStep extends InstallationStep {

    private String command;

    private String workingDirectory;

    public InstallationCommandStep(String title, File logFile, String command, String workingDirectory) {
        super(title, logFile);
        this.command = command;
        this.workingDirectory = workingDirectory;
    }

    public InstallationCommandStep(String title, File logFile, String command) {
        this(title, logFile, command, StringUtils.EMPTY);
    }

    public InstallationCommandStep(String title, String command, String workingDirectory) {
        this(title, null, command, workingDirectory);
    }

    public InstallationCommandStep(String title, String command) {
        this(title, null, command, StringUtils.EMPTY);
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            Map<String, String> envs = IosDeviceInfo.getIosAdditionalEnvironmentVariables();
            String[] commands = new String[] { "/bin/sh", "-c", command };
            runCommand(commands, envs, workingDirectory, getLogFile());
        } catch (IOException error) {
            LoggerSingleton.logError(error);
        }
    }

    private void runCommand(String[] commands, Map<String, String> envs, String workingDirectory, File logFile)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        Map<String, String> existingEnvironmentVariables = processBuilder.environment();
        existingEnvironmentVariables.putAll(envs);

        if (StringUtils.isNotEmpty(workingDirectory)) {
            processBuilder.directory(new File(workingDirectory));
        }
        if (logFile != null) {
            processBuilder.redirectOutput(getLogFile());
        }
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        process.waitFor();
    }
}
