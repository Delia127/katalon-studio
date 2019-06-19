package com.kms.katalon.composer.execution.launcher;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;

import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.ExistingRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.execution.session.ExecutionSessionSocketServer;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.service.Trackings;

public class IDELauncher extends ReportableLauncher implements ILaunchListener, IDEObservableLauncher {

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private boolean observed;

    private ILaunch launch;

    private LaunchMode mode;

    private boolean launchRemoved;

    @Override
    public ILaunch getLaunch() {
        return launch;
    }

    public IDELauncher(LauncherManager manager, IRunConfiguration runConfig, LaunchMode mode) {
        super(manager, runConfig);

        this.mode = mode;
        observed = false;
    }

    @Override
    public ReportableLauncher clone(IRunConfiguration newConfig) {
        return new IDELauncher(getManager(), newConfig, mode);
    }

    @Override
    protected ILaunchProcess launch() throws ExecutionException {
        try {
            DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);

            SafeRunner.run(new ISafeRunnable() {
                @Override
                public void run() throws Exception {
                    IFile scriptFile = GroovyUtil.getTempScriptIFile(
                            getRunConfig().getExecutionSetting().getScriptFile(),
                            ProjectController.getInstance().getCurrentProject());

                    if (scriptFile == null) {
                        return;
                    }

                    launchRemoved = false;

                    ILaunch expectedLaunch = new IDELaunchShorcut().launch(scriptFile, mode, runConfig);

                    launch = (launchRemoved) ? null : expectedLaunch;
                }

                @Override
                public void handleException(Throwable exception) {
                    logError(exception);
                }
            });

            return (launch != null) ? new IDELaunchProcess(launch) : null;
        } finally {
            DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
        }
    }

    @Override
    protected void onStartExecutionComplete() {
        super.onStartExecutionComplete();
        sendUpdateLogViewerEvent(getId());
        if (runConfig instanceof ExistingRunConfiguration) {
            pauseExecutionSession((ExistingRunConfiguration) runConfig);
        }
    }

    private void pauseExecutionSession(ExistingRunConfiguration runConfig) {
        ExecutionSession executionSession = ExecutionSessionSocketServer.getInstance()
                .getExecutionSessionBySessionAndRemoteURL(runConfig.getSessionId(), runConfig.getRemoteUrl());
        if (executionSession != null) {
            executionSession.pause();
        }
    }

    @Override
    public void setStatus(LauncherStatus status, String message) {
        super.setStatus(status, message);
        sendUpdateJobViewerEvent();
    }

    @Override
    protected void onUpdateResult(TestStatusValue statusValue) {
        super.onUpdateResult(statusValue);
        sendUpdateJobViewerEvent();
    }

    @Override
    public void setObserved(boolean observed) {
        this.observed = observed;
    }

    @Override
    public boolean isObserved() {
        return observed;
    }

    @Override
    protected void postExecutionComplete() {
        super.postExecutionComplete();
        sendUpdateJobViewerEvent();

        // update status of "Run" and "Stop" buttons
        eventBroker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);

        updateReport();

        if (runConfig instanceof ExistingRunConfiguration) {
            resumeExecutionSession((ExistingRunConfiguration) runConfig);
        } else {
            resumeExecutionSession(runConfig);
        }
        

        if (getStatus() != LauncherStatus.TERMINATED) {
            if (getExecutedEntity() instanceof TestCaseExecutedEntity) {
                String resultTestcase = getExecutionResult();
                Trackings.trackExecuteTestCase(mode.toString(), runConfig.getName(), resultTestcase,
                        getEndTime().getTime() - getStartTime().getTime());
            } else if (getExecutedEntity() instanceof TestSuiteExecutedEntity) {
                String resultTestSuite = getExecutionResult();
                Trackings.trackExecuteTestSuiteInGuiMode(mode.toString(), runConfig.getName(), resultTestSuite,
                        getEndTime().getTime() - getStartTime().getTime());
            }
        }
    }

    protected String getExecutionResult() {
        String resultExecution = null;
        if (getResult().getNumFailures() > 0) {
            resultExecution = TestStatusValue.FAILED.toString();
        } else if (getResult().getNumErrors() > 0) {
            resultExecution = TestStatusValue.ERROR.toString();
        } else {
            resultExecution = TestStatusValue.PASSED.toString();
        }
        return resultExecution;
    }

    private void resumeExecutionSession(IRunConfiguration runConfig) {
        ExecutionSession executionSession = ExecutionSessionSocketServer.getInstance()
                .getExecutionSessionByLogFolderPath(runConfig.getExecutionSetting().getFolderPath());
        if (executionSession != null) {
            executionSession.resume();
        }
    }

    private void resumeExecutionSession(ExistingRunConfiguration runConfig) {
        ExecutionSession executionSession = ExecutionSessionSocketServer.getInstance()
                .getExecutionSessionBySessionAndRemoteURL(runConfig.getSessionId(), runConfig.getRemoteUrl());
        if (executionSession != null) {
            executionSession.resume();
        }
    }

    protected void updateReport() {
        try {
            if (getTestSuite() == null || getStatus() != LauncherStatus.DONE) {
                return;
            }

            ReportEntity report = ReportController.getInstance().getReportEntity(getTestSuite(), getId());

            // refresh report item on tree explorer
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, null);

            // Open report by setting
            if (ExecutionDefaultSettingStore.getStore().isPostExecOpenReport()) {
                eventBroker.post(EventConstants.REPORT_OPEN, report);
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    protected void updateLastRun(Date startTime) throws Exception {
        super.updateLastRun(startTime);

        TestSuiteEntity testSuite = getTestSuite();
        eventBroker.send(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
    }

    @Override
    public void clean() {
        try {
            IDELaunchShorcut.cleanConfiguration(getLaunch());

            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            IFile scriptFile = GroovyUtil.getTempScriptIFile(getRunConfig().getExecutionSetting().getScriptFile(),
                    currentProject);
            if (scriptFile == null) {
                return;
            }
            scriptFile.delete(true, null);

            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(currentProject);
            libFolder.refreshLocal(IResource.DEPTH_ONE, null);

            if (isObserved()) {
                sendUpdateLogViewerEvent(null);
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    protected void sendUpdateLogViewerEvent(String message) {
        eventBroker.send(EventConstants.CONSOLE_LOG_RESET, message);
    }

    protected void sendUpdateJobViewerEvent() {
        eventBroker.post(EventConstants.JOB_REFRESH, null);
    }

    @Override
    protected synchronized void writeError(String line) {
        LogUtil.logErrorMessage(line);
    }

    private boolean isExpectedLaunch(ILaunch launch) {
        String expectedName = FilenameUtils.getBaseName(getRunConfig().getExecutionSetting().getScriptFile().getName());
        ILaunchConfiguration launchConfig = launch.getLaunchConfiguration();
        return launchConfig != null && expectedName.equals(launchConfig.getName());
    }

    @Override
    public LaunchMode getMode() {
        return mode;
    }

    /**
     * Handles the case that user cancels the launch progress. </br>
     * Called in
     * the phase that our launch is not returned at {@link #launch()}.
     */
    @Override
    public void launchRemoved(ILaunch launch) {
        if (isExpectedLaunch(launch)) {
            launchRemoved = true;
        }
    }

    @Override
    public void launchAdded(ILaunch launch) {
    }

    @Override
    public void launchChanged(ILaunch launch) {
    }

    @Override
    public void suspend() {
        try {
            getLaunch().getDebugTarget().suspend();
            onSuspended();
        } catch (DebugException e) {
            LoggerSingleton.logError(e);
        }
    }

    public void onSuspended() {
        setStatus(LauncherStatus.SUSPENDED);
        sendUpdateJobViewerEvent();
    }

    @Override
    public void resume() {
        try {
            getLaunch().getDebugTarget().resume();
            setStatus(LauncherStatus.RUNNING);
            sendUpdateJobViewerEvent();
        } catch (DebugException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public String getDisplayMessage() {
        String currentStatusMessage = getMessage();
        String displayMessage = StringUtils.isNotEmpty(currentStatusMessage) ? currentStatusMessage
                : getStatus().toString();
        return "<" + displayMessage + ">" + " - " + getRunConfig().getName();
    }

    @Override
    protected void onStartExecution() {
        super.onStartExecution();
    }
}
