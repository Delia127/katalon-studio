package com.kms.katalon.execution.launcher.process;

import com.kms.katalon.execution.logging.IOutputStream;
import com.kms.katalon.execution.logging.LaunchOutputStreamHandler;

public class ConsoleProcess implements ILaunchProcess {

    private Process fSystemProcess;

    private LaunchOutputStreamHandler fOutputStreamHandler;
    private LaunchOutputStreamHandler fErrorStreamHandler;

    public ConsoleProcess(Process systemProcess) {
        fSystemProcess = systemProcess;

        fOutputStreamHandler = new LaunchOutputStreamHandler(systemProcess.getInputStream(), System.out);
        fOutputStreamHandler.start();

        fErrorStreamHandler = new LaunchOutputStreamHandler(systemProcess.getErrorStream(), System.err);
        fErrorStreamHandler.start();
    }

    @Override
    public int getExitValue() {
        return fSystemProcess.exitValue();
    }

    @Override
    public void terminate() {
        fSystemProcess.destroy();
    }

    @Override
    public boolean isTerminated() {
        try {
            getExitValue();
            return true;
        } catch (IllegalThreadStateException ex) {
            return false;
        }
    }

    @Override
    public IOutputStream getOutputStreamHandler() {
        return fOutputStreamHandler;
    }

    @Override
    public IOutputStream getErrorStreamHandler() {
        return fErrorStreamHandler;
    }
}
