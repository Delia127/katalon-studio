package com.kms.katalon.execution.launcher;

import java.util.LinkedList;
import java.util.List;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.result.LauncherStatus;

/* package */ abstract class BasicLauncher implements ObservableLauncher {
    private LauncherStatus status;

    private List<LauncherListener> listeners;

    protected BasicLauncher() {
        status = LauncherStatus.WAITING;

        listeners = new LinkedList<LauncherListener>();
    }

    protected void notifyLauncherChanged(LauncherEvent event, Object object) {
        for (LauncherListener l : listeners) {
            l.handleLauncherEvent(event, new LauncherNotifiedObject(getId(), object));
        }
    }

    public void addListener(LauncherListener l) {
        listeners.add(l);
    }

    public void removeListener(LauncherListener l) {
        listeners.remove(l);
    }

    @Override
    public LauncherStatus getStatus() {
        return status;
    }
    
    @Override
    public void setStatus(LauncherStatus status) {
        this.status = status;
        notifyLauncherChanged(LauncherEvent.UPDATE_STATUS, status);
    }
    
    protected void onUpdateResult(TestStatusValue testStatusValue) {
        notifyLauncherChanged(LauncherEvent.UPDATE_RESULT, testStatusValue);
    }
}
