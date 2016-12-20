package com.kms.katalon.execution.launcher.process;

import com.kms.katalon.execution.logging.LaunchOutputStreamHandler;

public class NonStreamHandledProcess extends ConsoleProcess {

    public NonStreamHandledProcess(Process systemProcess) {
        super(systemProcess);
    }

    @Override
    protected void buildStreamHandler(Process systemProcess) {
        fOutputStreamHandler = LaunchOutputStreamHandler.outputHandlerFrom(systemProcess.getInputStream(), false);
        fOutputStreamHandler.start();

        fErrorStreamHandler = LaunchOutputStreamHandler.errorHandlerFrom(systemProcess.getErrorStream(), false);
        fErrorStreamHandler.start();
    }

}
