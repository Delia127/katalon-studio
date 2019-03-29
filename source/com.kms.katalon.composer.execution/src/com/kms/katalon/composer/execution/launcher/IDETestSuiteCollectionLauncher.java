package com.kms.katalon.composer.execution.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.ExecutionMode;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.TestSuiteCollectionLauncher;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.result.LauncherStatus;

public class IDETestSuiteCollectionLauncher extends TestSuiteCollectionLauncher implements IDEObservableLauncher,
        IDEObservableParentLauncher, LauncherListener {

    private boolean observed;

    private List<XmlLogRecord> logRecords;

    public IDETestSuiteCollectionLauncher(TestSuiteCollectionExecutedEntity executedEntity,
            LauncherManager parentManager, List<ReportableLauncher> subLaunchers, ExecutionMode executionMode,
            ReportCollectionEntity reportCollection) {
        super(executedEntity, parentManager, subLaunchers, executionMode, reportCollection);
        this.observed = false;

        logRecords = new ArrayList<>();
    }

    private IEventBroker getEventBroker() {
        return EventBrokerSingleton.getInstance().getEventBroker();
    }

    @Override
    protected void preStarting() {
        getEventBroker().send(EventConstants.CONSOLE_LOG_RESET, getId());
    }

    @Override
    protected void postExecution() {
        super.postExecution();
        // update status of "Run" and "Stop" buttons
        getEventBroker().post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
    }

    @Override
    public void setStatus(LauncherStatus status, String message) {
        super.setStatus(status, message);
        getEventBroker().post(EventConstants.JOB_REFRESH, null);
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
    public LaunchMode getMode() {
        return LaunchMode.RUN;
    }

    @Override
    public String getDisplayMessage() {
        return "<" + getStatus().toString() + "> - Test Suite Collection";
    }

    @Override
    public void suspend() {
        // Do nothing
    }

    @Override
    public void resume() {
        // Do nothing
    }

    @Override
    public List<XmlLogRecord> getLogRecords() {
        return logRecords;
    }

    @Override
    public ILaunch getLaunch() {
        return null;
    }

    @Override
    public void handleLauncherEvent(LauncherEvent event, LauncherNotifiedObject object) {
        switch (event) {
            case UPDATE_RECORD:
                logRecords.add((XmlLogRecord) object.getObject());
                notifyLauncherChanged(LauncherEvent.UPDATE_RECORD, getId());
                break;
            default:
                super.handleLauncherEvent(event, object);
        }
    }

    @Override
    protected void onUpdateResult(TestStatusValue testStatusValue) {
        super.onUpdateResult(testStatusValue);
        getEventBroker().post(EventConstants.JOB_REFRESH, null);
    }
    
    @Override
    protected void onNewLauncherAdded() {
        getEventBroker().post(EventConstants.JOB_REFRESH, null);
        getEventBroker().send(EventConstants.CONSOLE_LOG_RESET, getId());
    }

    @Override
    public List<IDEObservableLauncher> getObservableLaunchers() {
        List<IDEObservableLauncher> subIDEObservableLaunchers = new ArrayList<>();
        for (ReportableLauncher subLauncher : subLaunchers) {
            if (subLauncher instanceof IDEObservableLauncher) {
                subIDEObservableLaunchers.add((IDEObservableLauncher) subLauncher);
            }
        }
        return subIDEObservableLaunchers;
    }
}
