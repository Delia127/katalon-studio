package com.kms.katalon.execution.launcher;

import java.io.IOException;
import java.util.List;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.ExecutionMode;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.logging.LogUtil;

public class TestSuiteCollectionLauncher extends BasicLauncher implements LauncherListener {

    protected List<? extends ReportableLauncher> subLaunchers;

    private LauncherResult result;

    protected TestSuiteCollectionLauncherManager subLauncherManager;

    private LauncherManager parentManager;

    private Thread watchDog;

    private TestSuiteCollectionExecutedEntity executedEntity;

    private ExecutionMode executionMode;

    public TestSuiteCollectionLauncher(TestSuiteCollectionExecutedEntity executedEntity, LauncherManager parentManager,
            List<? extends ReportableLauncher> subLaunchers, ExecutionMode executionMode) {
        this.subLauncherManager = new TestSuiteCollectionLauncherManager();
        this.subLaunchers = subLaunchers;
        this.result = new LauncherResult(executedEntity.getTotalTestCases());
        this.parentManager = parentManager;
        this.executedEntity = executedEntity;
        this.executionMode = executionMode;
        addListenerForChildren(subLaunchers);
    }

    private void addListenerForChildren(List<? extends ReportableLauncher> subLaunchers) {
        for (ReportableLauncher childLauncher : subLaunchers) {
            childLauncher.addListener(this);
        }
    }

    @Override
    public void start() throws IOException {
        setStatus(LauncherStatus.RUNNING);

        preStarting();

        scheduleSubLaunchers();

        startWatchDog();
    }

    private void scheduleSubLaunchers() {
        for (ReportableLauncher launcher : subLaunchers) {
            subLauncherManager.addLauncher(launcher);
            launcher.setManager(subLauncherManager);
        }
    }

    protected void preStarting() {
        // Children may override this
    }

    private void startWatchDog() {
        watchDog = new Thread(new Runnable() {
            @Override
            public void run() {
                while (subLauncherManager.isAnyLauncherRunning()) {
                    try {
                        Thread.sleep(IWatcher.DF_TIME_OUT_IN_MILLIS);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                setStatus(LauncherStatus.DONE);
                postExecution();
            }
        });
        watchDog.start();
    }

    protected void postExecution() {
        schedule();
    }

    protected void schedule() {
        try {
            parentManager.stopRunningAndSchedule(this);
        } catch (InterruptedException e) {
            LogUtil.logError(e);
        }
    }

    @Override
    public void stop() {
        if (watchDog.isAlive()) {
            watchDog.interrupt();
        }
        subLauncherManager.stopAllLauncher();

        setStatus(LauncherStatus.TERMINATED);

        postExecution();
    }

    @Override
    public void clean() {
        for (ReportableLauncher launcher : subLaunchers) {
            launcher.clean();
        }
    }

    @Override
    public String getId() {
        return executedEntity.getId();
    }

    @Override
    public String getName() {
        return executedEntity.getSourceId() + " - " + executedEntity.getId();
    }

    @Override
    public ILauncherResult getResult() {
        return result;
    }

    public class TestSuiteCollectionLauncherManager extends LauncherManager {
        protected boolean isLauncherReadyToRun(ILauncher launcher) {
            if (executionMode == ExecutionMode.PARALLEL) {
                return true;
            }
            return getRunningLaunchers().isEmpty();
        }

        @Override
        public String getChildrenLauncherStatus(int consoleWidth) {
            return super.getChildrenLauncherStatus(consoleWidth);
        }

        @Override
        protected void schedule() {
            try {
                Thread.sleep(IWatcher.DF_TIME_OUT_IN_MILLIS);
            } catch (InterruptedException e) {
                LogUtil.logError(e);
            }
            super.schedule();
        }
    }

    @Override
    public void handleLauncherEvent(LauncherEvent event, LauncherNotifiedObject object) {
        if (event == LauncherEvent.UPDATE_RESULT) {
            TestStatusValue statusValue = (TestStatusValue) object.getObject();
            switch (statusValue) {
                case ERROR:
                    result.increaseErrors();
                    break;
                case FAILED:
                    result.increaseFailures();
                    break;
                case PASSED:
                    result.increasePasses();
                    break;
                default:
                    break;
            }
            onUpdateResult(statusValue);
        }
    }
}
