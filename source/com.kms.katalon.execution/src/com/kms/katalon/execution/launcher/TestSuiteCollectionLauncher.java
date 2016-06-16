package com.kms.katalon.execution.launcher;

import java.io.IOException;
import java.util.List;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
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

    private List<? extends ReportableLauncher> subLaunchers;

    private LauncherResult result;

    private TestRunLauncherManager testRunManager;

    private LauncherManager parentManager;

    private Thread watchDog;

    private TestSuiteCollectionExecutedEntity executedEntity;

    protected final ReportCollectionEntity reportCollectionEntity;

    public TestSuiteCollectionLauncher(TestSuiteCollectionExecutedEntity executedEntity, LauncherManager parentManager,
            List<? extends ReportableLauncher> subLaunchers) {
        this.testRunManager = new TestRunLauncherManager();
        this.subLaunchers = subLaunchers;
        this.result = new LauncherResult(executedEntity.getTotalTestCases());
        this.parentManager = parentManager;
        this.executedEntity = executedEntity;
        this.reportCollectionEntity = createReportCollectionEntity();

        addListenerForChildren(subLaunchers);
    }

    private void addListenerForChildren(List<? extends ReportableLauncher> subLaunchers) {
        for (ReportableLauncher childLauncher : subLaunchers) {
            childLauncher.addListener(this);
        }
    }

    private final ReportCollectionEntity createReportCollectionEntity() {
        try {
            ReportController reportController = ReportController.getInstance();
            ReportCollectionEntity reportCollection = reportController.newReportCollection(getCurrentProject(),
                    getTestSuiteCollection(), getId());

            for (int i = 0; i < subLaunchers.size(); i++) {
                ReportableLauncher launcher = subLaunchers.get(i);
                RunConfigurationDescription configDescription = executedEntity.getEntity()
                        .getTestSuiteRunConfigurations()
                        .get(i)
                        .getConfiguration();
                reportCollection.getReportItemDescriptions().add(launcher.getReportDescription(configDescription));
            }
            reportController.updateReportCollection(reportCollection);
            return reportCollection;
        } catch (DALException ex) {
            LogUtil.logError(ex);
            return null;
        }
    }

    private ProjectEntity getCurrentProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    private TestSuiteCollectionEntity getTestSuiteCollection() {
        return executedEntity.getEntity();
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
            testRunManager.addLauncher(launcher);
            launcher.setManager(testRunManager);
        }
    }

    protected void preStarting() {
        // Children may override this
    }

    private void startWatchDog() {
        watchDog = new Thread(new Runnable() {
            @Override
            public void run() {
                while (testRunManager.isAnyLauncherRunning()) {
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
        testRunManager.stopAllLauncher();

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

    private class TestRunLauncherManager extends LauncherManager {
        protected boolean isLauncherReadyToRun(ILauncher launcher) {
            return getRunningLaunchers().isEmpty();
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
