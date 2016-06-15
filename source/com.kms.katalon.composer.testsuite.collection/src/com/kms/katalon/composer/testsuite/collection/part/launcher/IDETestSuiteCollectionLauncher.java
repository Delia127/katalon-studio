package com.kms.katalon.composer.testsuite.collection.part.launcher;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.composer.execution.launcher.IDELauncherEvent;
import com.kms.katalon.composer.execution.launcher.IDELauncherListener;
import com.kms.katalon.composer.execution.launcher.ObservableLauncher;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.launcher.TestSuiteCollectionLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class IDETestSuiteCollectionLauncher extends TestSuiteCollectionLauncher implements ObservableLauncher, IDELauncherListener {

    private boolean observed;

    private Set<IDELauncherListener> listeners;

    private List<XmlLogRecord> logRecords;

    public IDETestSuiteCollectionLauncher(TestSuiteCollectionExecutedEntity executedEntity,
            LauncherManager parentManager, List<SubIDELauncher> subLaunchers) {
        super(executedEntity, parentManager, subLaunchers);
        this.observed = false;
        listeners = new LinkedHashSet<>();
        for (IDELauncher subLauncher : subLaunchers) {
            subLauncher.addListener(this);
        }
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
    protected void onUpdateResult() {
        onUpdateStatus();
    }

    protected void onUpdateStatus() {
        notifyLauncherChanged(IDELauncherEvent.UPDATE_STATUS, this.getId());
        getEventBroker().post(EventConstants.JOB_REFRESH, null);
    }

    private void notifyLauncherChanged(IDELauncherEvent event, String id) {
        for (IDELauncherListener l : listeners) {
            l.handleLauncherEvent(event, id);
        }
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
    public void addListener(IDELauncherListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListener(IDELauncherListener l) {
        listeners.remove(l);
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
    public void handleLauncherEvent(IDELauncherEvent event, Object object) {
        if (event == IDELauncherEvent.UPDATE_RECORD && object instanceof XmlLogRecord) {
            logRecords.add((XmlLogRecord) object);
            notifyLauncherChanged(IDELauncherEvent.UPDATE_RECORD, getId());
        }
    }

}
