package com.kms.katalon.execution.launcher;

import java.util.HashSet;
import java.util.Set;

import com.kms.katalon.execution.launcher.process.ILaunchProcess;

public class LaunchWatchdog implements Runnable {

    private ILaunchProcess process;

    private IWatcher[] subWatchdogs;
    private Thread[] subThreads;

    private boolean stopFlag;
    private int waitingDelayInMillis;

    private Set<IWatchdogListener> listeners;

    public LaunchWatchdog() {
        stopFlag = false;
        setWaitingDelayInMillis(IWatcher.DF_TIME_OUT_IN_MILLIS);
    }

    public LaunchWatchdog(int waitingDelayInMillis) {
        stopFlag = false;
        setWaitingDelayInMillis(waitingDelayInMillis);
    }

    public LaunchWatchdog(IWatcher[] watchers) {
        stopFlag = false;
        setWaitingDelayInMillis(IWatcher.DF_TIME_OUT_IN_MILLIS);
        setSubThreads(watchers);
    }

    public LaunchWatchdog(IWatcher[] watchers, int waitingDelayInMillis) {
        stopFlag = false;
        setWaitingDelayInMillis(waitingDelayInMillis);
        setSubThreads(watchers);
    }

    @Override
    public void run() {
        try {
            startChildWatchDogs();
            
            while (true) {
                boolean isProcessTerminated = isTerminated();

                if (stopFlag && !isProcessTerminated) {
                    terminateProcess();
                    isProcessTerminated = true;
                }

                if (isProcessTerminated) {
                    stopFlag = true;
                    terminateChildWatchdogs();
                    return;
                }

                Thread.sleep(waitingDelayInMillis);
            }
        } catch (InterruptedException e) {
            // Do nothing
        } finally {
            notifyProcessTerminated();
        }
    }
    
    private void startChildWatchDogs() {
        if (subWatchdogs == null) { 
            return;
        }
           
        subThreads = new Thread[subWatchdogs.length];
        for (int i = 0; i < subWatchdogs.length; i++) {
            subThreads[i] = new Thread(subWatchdogs[i]);
            subThreads[i].start();
        }
    }

    private boolean isTerminated() {
        return process != null && process.isTerminated();
    }

    private void terminateProcess() {
        if (process != null) {
            process.terminate();
        }
    }

    public ILaunchProcess getProcess() {
        return process;
    }

    public void setProcess(ILaunchProcess process) {
        this.process = process;
    }

    private void terminateChildWatchdogs() {
        if (subWatchdogs == null) {
            return;
        }

        for (IWatcher watchdog : subWatchdogs) {
            watchdog.stop();
        }
    }

    public void stop() {
        this.stopFlag = true;
    }

    private void setWaitingDelayInMillis(int waitingDelayInMillis) {
        this.waitingDelayInMillis = waitingDelayInMillis;
    }

    private void setSubThreads(IWatcher[] subThreads) {
        this.subWatchdogs = subThreads;
    }

    private Set<IWatchdogListener> getListeners() {
        if (listeners == null) {
            listeners = new HashSet<IWatchdogListener>();
        }

        return listeners;
    }

    public void addListener(IWatchdogListener listener) {
        getListeners().add(listener);
    }

    private void notifyProcessTerminated() {
        for (IWatchdogListener listener : getListeners()) {
            listener.onWatchdogComplete(this);
        }
    }
}