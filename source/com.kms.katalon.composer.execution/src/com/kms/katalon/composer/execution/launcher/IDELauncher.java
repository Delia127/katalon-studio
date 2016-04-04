package com.kms.katalon.composer.execution.launcher;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.model.LauncherStatus;
import com.kms.katalon.execution.launcher.process.ILaunchProcess;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class IDELauncher extends ReportableLauncher implements ILaunchListener {

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private List<IDELauncherListener> listeners;

    private boolean observed;

    private ILaunch launch;

    private LaunchMode mode;

    private boolean launchRemoved;

    public ILaunch getLaunch() {
        return launch;
    }

    public IDELauncher(IRunConfiguration runConfig, LaunchMode mode) {
        super(runConfig);

        this.mode = mode;
        listeners = new LinkedList<IDELauncherListener>();
        observed = false;
    }

    @Override
    public ReportableLauncher clone(IRunConfiguration newConfig) {
        return new IDELauncher(newConfig, mode);
    }

    @Override
    protected ILaunchProcess launch() throws ExecutionException {
        try {
            DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);

            SafeRunner.run(new ISafeRunnable() {
                @Override
                public void run() throws Exception {
                    IFile scriptFile = GroovyUtil.getTempScriptIFile(getRunConfig().getExecutionSetting()
                            .getScriptFile(), ProjectController.getInstance().getCurrentProject());

                    if (scriptFile == null) {
                        return;
                    }

                    launchRemoved = false;

                    ILaunch expectedLaunch = new IDELaunchShorcut().launch(scriptFile, mode);

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
        eventBroker.send(EventConstants.CONSOLE_LOG_RESET, getId());
    }

    public void addListener(IDELauncherListener l) {
        listeners.add(l);
    }

    public void removeListener(IDELauncherListener l) {
        listeners.remove(l);
    }

    private void notifyLauncherChanged(IDELaucherEvent event, Object object) {
        for (IDELauncherListener l : listeners) {
            l.handleLauncherEvent(event, object);
        }
    }

    @Override
    public void setStatus(LauncherStatus status) {
        super.setStatus(status);
        onUpdateStatus();
    }

    @Override
    protected void onUpdateStatus() {
        notifyLauncherChanged(IDELaucherEvent.UPDATE_STATUS, this.getId());
        eventBroker.post(EventConstants.JOB_REFRESH, null);
    }

    @Override
    protected void onUpdateRecord(XmlLogRecord record) {
        notifyLauncherChanged(IDELaucherEvent.UPDATE_RECORD, record);
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
    }

    public boolean isObserved() {
        return observed;
    }

    protected void postExecutionComplete() {
        eventBroker.post(EventConstants.JOB_REFRESH, null);

        // update status of "Run" and "Stop" buttons
        eventBroker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);

        if (getTestSuite() == null || getStatus() != LauncherStatus.DONE) {
            return;
        }
        try {
            ReportEntity report = ReportController.getInstance().getReportEntity(getTestSuite(), getId());

            // refresh report item on tree explorer
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, null);

            // Open report by setting
            ScopedPreferenceStore store = getPreferenceStore(IDELauncher.class);
            if (store.getBoolean(ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING)) {
                eventBroker.post(EventConstants.REPORT_OPEN, report);
            }
        } catch (Exception e) {
            logError(e);
        }
    }

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
                eventBroker.send(EventConstants.CONSOLE_LOG_RESET, null);
            }
        } catch (Exception e) {
            logError(e);
        }
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

    public LaunchMode getMode() {
        return mode;
    }

    /**
     * Handles the case that user cancels the launch progress. </br> Called in the phase that our launch is not returned
     * at {@link #launch()}.
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
}
