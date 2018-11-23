package com.kms.katalon.execution.launcher;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.execution.entity.ExecutedEntity;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.execution.integration.ReportIntegrationFactory;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.result.ExecutionEntityResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;
import com.kms.katalon.logging.LogUtil;

/* package */abstract class BasicLauncher implements ObservableLauncher {
    private LauncherStatus status;

    private List<LauncherListener> listeners;

    private String message;

    private String executionUUID;
    
    protected BasicLauncher() {
        status = LauncherStatus.WAITING;

        executionUUID = UUID.randomUUID().toString();
        listeners = new LinkedList<LauncherListener>();
        setMessage(StringUtils.EMPTY);
    }

    protected void notifyLauncherChanged(LauncherEvent event, Object object) {
        for (LauncherListener l : listeners) {
            l.handleLauncherEvent(event, new LauncherNotifiedObject(getId(), object));
        }
    }
    
    protected void notifyProccess(Object event, IExecutedEntity executedEntity, ExecutionEntityResult result) {
    	result.setExecutedEntity(executedEntity);
    	result.setEvent(event);
    	for (Entry<String, ReportIntegrationContribution> reportContributorEntry : ReportIntegrationFactory
                .getInstance().getIntegrationContributorMap().entrySet()) {
            ReportIntegrationContribution contribution = reportContributorEntry.getValue();
            try {
                reportContributorEntry.getValue().notifyProccess(event, result);

            } catch (Exception e) {
                LogUtil.logError(e);
            }
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
        setStatus(status, StringUtils.EMPTY);
    }

    public void setStatus(LauncherStatus status, String message) {
        this.setMessage(message);
        this.status = status;
        notifyLauncherChanged(LauncherEvent.UPDATE_STATUS, status);
    }

    protected void onUpdateResult(TestStatusValue testStatusValue) {
        notifyLauncherChanged(LauncherEvent.UPDATE_RESULT, testStatusValue);
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }
    
    public String getExecutionUUID() {
    	return this.executionUUID;
    }
    
    public void setExecutionUUID(String executionUUID) {
    	this.executionUUID = executionUUID;
    }
}
