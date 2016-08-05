package com.kms.katalon.composer.components.impl.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

public class ProgressMonitorDialogWithThread extends ProgressMonitorDialog {
    public ProgressMonitorDialogWithThread(Shell parent) {
        super(parent);
    }

    private Thread thread;

    private void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    protected void cancelPressed() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }

        super.cancelPressed();
    }

    private void startThreadAndWait() {
        if (thread == null) {
            return;
        }

        thread.run();

        while (thread.isAlive()) {
            // wait for thread complete or interrupted
        }
    }

    public <V> V runAndWait(final Callable<V> callable) throws InterruptedException, InvocationTargetException {
        FutureTask<V> futureTask = new FutureTask<V>(callable);
        setThread(new Thread(futureTask));

        try {
            startThreadAndWait();
            return futureTask.get();
        } catch (ExecutionException e) {
            throw new InvocationTargetException(e);
        }
    }
}
