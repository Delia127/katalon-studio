package com.kms.katalon.execution.launcher;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.execution.logging.IOutputStream;
import com.kms.katalon.logging.LogUtil;

public abstract class Launcher implements ILauncher, IWatchdogListener {
    protected IRunConfiguration runConfig;

    private LauncherStatus status;

    private ILauncherResult result;

    private IExecutedEntity executedEntity;

    protected Set<IWatcher> watchers;

    protected LaunchWatchdog watchdog;

    protected ILaunchProcess process;

    private LauncherManager manager;

    private Launcher() {
        status = LauncherStatus.WAITING;
        watchers = new HashSet<IWatcher>();
    }

    protected Launcher(IRunConfiguration runConfig) {
        this();
        setRunConfig(runConfig);
    }

    protected Launcher(LauncherManager manager, IRunConfiguration runConfig) {
        this(runConfig);
        this.manager = manager;
    }

    protected LauncherManager getManager() {
        return manager;
    }

    protected void setManager(LauncherManager manager) {
        this.manager = manager;
    }

    public final String getId() {
        return getRunConfig().getExecutionSetting().getName();
    }

    public String getName() {
        return executedEntity.getSourceId() + " - " + getRunConfig().getName() + " - " + getId();
    }

    public IRunConfiguration getRunConfig() {
        return runConfig;
    }

    @Override
    public LauncherStatus getStatus() {
        return status;
    }

    @Override
    public ILauncherResult getResult() {
        return result;
    }

    /**
     * Launches execution process and starts <code>watchdog</code> to monitor
     * that process and its watchers.
     */
    @Override
    public final void start() {
        onStartExecution();

        watchdog = new LaunchWatchdog(watchers.toArray(new IWatcher[watchers.size()]));
        watchdog.addListener(this);

        Thread mainThread = new Thread(watchdog);
        mainThread.start();

        try {
            process = launch();

            if (process != null) {
                watchdog.setProcess(process);

                onStartExecutionComplete();
            } else {
                stop();
            }

        } catch (ExecutionException e) {
            stop();
        }
    }

    protected synchronized void writeLine(String line) {
        try {
            IOutputStream os = process.getOutputStreamHandler();
            if (os != null) {
                os.println(line);
            }
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    protected synchronized void writeError(String line) {
        try {
            IOutputStream es = process.getErrorStreamHandler();
            if (es != null) {
                es.println(line);
            }
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    protected abstract ILaunchProcess launch() throws ExecutionException;

    /**
     * Children may override this
     */
    protected void onStartExecution() {
        // For children
    }

    /**
     * Children may override this
     */
    protected void onStartExecutionComplete() {
        // For children
    }

    @Override
    public void stop() {
        setStatus(LauncherStatus.TERMINATED);
        if (watchdog != null) {
            watchdog.stop();
        }
    }

    protected void setRunConfig(IRunConfiguration runConfig) {
        this.runConfig = runConfig;
        executedEntity = runConfig.getExecutionSetting().getExecutedEntity();
        result = new LauncherResult(executedEntity.getTotalTestCases());
    }

    /**
     * When <code>watchdog</code> completed that means the <code>process</code> is done or terminated. </p> This method
     * is used for handling the phases
     * that are after {@link LauncherStatus#RUNNING}. </br> Example: Preparing
     * report, sending email,... </p>
     */
    @Override
    public final void onWatchdogComplete(LaunchWatchdog watchdog) {
        if (this.watchdog != watchdog) {
            return;
        }

        if (getStatus() == LauncherStatus.RUNNING) {
            preExecutionComplete();

            setStatus(LauncherStatus.DONE);
        } else {
            setStatus(LauncherStatus.TERMINATED);
        }

        schedule();

        postExecutionComplete();
    }

    protected void postExecutionComplete() {
        try {
            if (process.getOutputStreamHandler() != null) {
                process.getOutputStreamHandler().close();
            }

            if (process.getErrorStreamHandler() != null) {
                process.getErrorStreamHandler().close();
            }
        } catch (IOException e) {
            // Nothing to report here
        }
    }

    protected void schedule() {
        try {
            manager.stopRunningAndSchedule(this);
        } catch (InterruptedException e) {
            LogUtil.logError(e);
        }
    }

    /**
     * Children may override this
     */
    protected void preExecutionComplete() {
        // For children
    }

    @Override
    public void setStatus(LauncherStatus status) {
        this.status = status;
    }

    /**
     * Cleans the prepared script file only.
     */
    @Override
    public void clean() {
        File scriptFile = getRunConfig().getExecutionSetting().getScriptFile();
        FileUtils.deleteQuietly(scriptFile);
    }
}
