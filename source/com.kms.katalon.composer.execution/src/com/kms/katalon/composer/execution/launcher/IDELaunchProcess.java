package com.kms.katalon.composer.execution.launcher;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.logging.IOutputStream;

public class IDELaunchProcess implements ILaunchProcess {

    private ILaunch fLaunch;
    public IDELaunchProcess(ILaunch launch) {
        fLaunch = launch;
    }

    @Override
    public int getExitValue() {
        try {
            return getProcess().getExitValue();
        } catch (DebugException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void terminate() {
        try {
            IProcess process = getProcess();
            if (process != null && process.canTerminate()) {
                getProcess().terminate();
            }
        } catch (DebugException e) {
            throw new IllegalThreadStateException(e.getMessage());
        }
    }

    @Override
    public boolean isTerminated() {
        return getProcess() != null && getProcess().isTerminated();
    }

    private IProcess getProcess() {
        if (fLaunch != null && fLaunch.getProcesses() != null && fLaunch.getProcesses().length > 0) {
            return fLaunch.getProcesses()[0];
        }

        return null;
    }

    @Override
    public IOutputStream getOutputStreamHandler() {        
        return null;
    }

    @Override
    public IOutputStream getErrorStreamHandler() {
        return null;
    }
}
