package com.kms.katalon.composer.mobile.installer.model;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.installer.exception.RunInstallationStepException;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;

public class InstallationCommandStep extends InstallationStep {

    private String command;

    private String workingDirectory;

    public InstallationCommandStep(String title, File logFile, File errorLogFile, String command, String workingDirectory) {
        super(title, logFile, errorLogFile);
        this.command = command;
        this.workingDirectory = workingDirectory;
    }

    public InstallationCommandStep(String title, File logFile, String command, String workingDirectory) {
        this(title, logFile, null, command, workingDirectory);
    }

    public InstallationCommandStep(String title, File logFile, File errorLogFile, String command) {
        this(title, logFile, errorLogFile, command, StringUtils.EMPTY);
    }

    public InstallationCommandStep(String title, File logFile, String command) {
        this(title, logFile, null, command, StringUtils.EMPTY);
    }

    public InstallationCommandStep(String title, String command, String workingDirectory) {
        this(title, null, null, command, workingDirectory);
    }

    public InstallationCommandStep(String title, String command) {
        this(title, null, null, command, StringUtils.EMPTY);
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            Map<String, String> envs = IosDeviceInfo.getIosAdditionalEnvironmentVariables();
            String[] commands = new String[] { "/bin/sh", "-c", command };
            if (runCommand(commands, envs, workingDirectory, getLogFile(), getErrorLogFile()) != 0) {
                throw new RunInstallationStepException("Failed to run the installation command.", new Throwable(String.format("Command: \"%s\"", command)));
            }
        } catch (IOException error) {
            LoggerSingleton.logError(error);
        }
    }

    private int runCommand(String[] commands, Map<String, String> envs, String workingDirectory, File logFile, File errorLogFile)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        Map<String, String> existingEnvironmentVariables = processBuilder.environment();
        existingEnvironmentVariables.putAll(envs);

        if (StringUtils.isNotEmpty(workingDirectory)) {
            processBuilder.directory(new File(workingDirectory));
        }
        if (logFile != null) {
            processBuilder.redirectOutput(Redirect.appendTo(logFile));
        }
        if (logFile != null) {
            processBuilder.redirectError(Redirect.appendTo(errorLogFile));
        }
//        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        return process.waitFor();
    }
}
