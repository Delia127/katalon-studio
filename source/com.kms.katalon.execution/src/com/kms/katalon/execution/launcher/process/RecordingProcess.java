package com.kms.katalon.execution.launcher.process;

import com.kms.katalon.execution.logging.IOutputStream;
import com.kms.katalon.execution.logging.RecordingOutputStreamHandler;

public class RecordingProcess implements ILaunchProcess {

    private Process fSystemProcess;

    private RecordingOutputStreamHandler fOutputStreamHandler;
    private RecordingOutputStreamHandler fErrorStreamHandler;

    public RecordingProcess(Process systemProcess) {
        fSystemProcess = systemProcess;

        buildStreamHandler(systemProcess);
    }

    private void buildStreamHandler(Process systemProcess) {
        fOutputStreamHandler = RecordingOutputStreamHandler.outputHandlerFrom(systemProcess.getInputStream());
        fOutputStreamHandler.start();

        fErrorStreamHandler = RecordingOutputStreamHandler.errorHandlerFrom(systemProcess.getErrorStream());
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
