package com.kms.katalon.composer.components.impl.installer;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.exception.RunInstallationStepException;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class InstallationCommandStep extends InstallationStep {

    private String command;

    private String workingDirectory;

    private Map<String, String> envs;

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
            String[] commands = new String[] { "/bin/sh", "-c", command };
            if (runCommand(commands, getEnvs(), workingDirectory, getLogFile(), getErrorLogFile()) != 0) {
                throw new RunInstallationStepException("Failed to run the installation command.", new Throwable(String.format("Command: \"%s\"", command)));
            }
        } catch (IOException error) {
            LoggerSingleton.logError(error);
            throw new RunInstallationStepException("Failed to run the installation command.", error);
        }
    }

    private int runCommand(String[] commands, Map<String, String> envs, String workingDirectory, File logFile, File errorLogFile)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        Map<String, String> existingEnvironmentVariables = processBuilder.environment();
        if (envs != null) {
            existingEnvironmentVariables.putAll(envs);
        }

        if (StringUtils.isNotEmpty(workingDirectory)) {
            processBuilder.directory(new File(workingDirectory));
        }
        if (logFile != null) {
            processBuilder.redirectOutput(Redirect.appendTo(logFile));
        }
        if (errorLogFile != null) {
            processBuilder.redirectError(Redirect.appendTo(errorLogFile));
        }

        Process process = processBuilder.start();
        return process.waitFor();
    }

    public Map<String, String> getEnvs() {
        return envs;
    }

    public void setEnvironments(Map<String, String> envs) {
        this.envs = envs;
    }
}
