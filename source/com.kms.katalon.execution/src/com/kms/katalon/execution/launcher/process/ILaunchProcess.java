package com.kms.katalon.execution.launcher.process;

import com.kms.katalon.execution.logging.IOutputStream;

public interface ILaunchProcess {
    public IOutputStream getOutputStreamHandler();
    
    public IOutputStream getErrorStreamHandler();
    
    public int getExitValue() throws IllegalThreadStateException;
    
    public void terminate() throws IllegalThreadStateException;
    
    public boolean isTerminated();
}
