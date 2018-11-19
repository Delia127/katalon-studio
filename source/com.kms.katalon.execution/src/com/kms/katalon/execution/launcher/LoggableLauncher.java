package com.kms.katalon.execution.launcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.execution.configuration.IHostConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.ExecutionEntityResult;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.execution.logging.ILogCollection;
import com.kms.katalon.execution.logging.LogEvaluator;
import com.kms.katalon.execution.logging.SocketWatcher;

public abstract class LoggableLauncher extends ProcessLauncher implements ILogCollection, LogEvaluator {
    private static final int DF_WATCHER_DELAY_TIME = 1;

    private List<XmlLogRecord> logRecords = new ArrayList<XmlLogRecord>();

    private Stack<XmlLogRecord> startRecords = new Stack<>();
    
    /**
     * Returns the level of the current {@link XmlLogRecord}
     */
    private int logDepth;

    private LogLevel currentTestCaseResult;

    public LoggableLauncher(LauncherManager manager, IRunConfiguration runConfig) {
        super(manager, runConfig);
    }

    @Override
    protected void onStartExecution() {
        logDepth = 0;

        IHostConfiguration hostConfig = getRunConfig().getHostConfiguration();

        watchers.add(new SocketWatcher(hostConfig.getHostPort(), DF_WATCHER_DELAY_TIME, this));

        currentTestCaseResult = LogLevel.NOT_RUN;
    }

    @Override
    public synchronized void addLogRecords(List<XmlLogRecord> records) {
        LauncherResult launcherResult = (LauncherResult) getResult();
        for (XmlLogRecord record : records) {
            logRecords.add(record);
            onUpdateRecord(record);

            LogLevel logLevel = LogLevel.valueOf(record.getLevel().getName());
            if (logLevel == null) {
                continue;
            }

            switch (logLevel) {
                case START:
                    startRecords.push(record);
                    logDepth++;
                    break;
                case END:
                    if (isLogUnderTestCaseMainLevel(runConfig, logDepth) && isStartTestCaseLog(startRecords.peek())) {
                        switch (currentTestCaseResult) {
                            case PASSED:
                                launcherResult.increasePasses();
                                break;
                            case ERROR:
                                launcherResult.increaseErrors();
                                break;
                            case FAILED:
                                launcherResult.increaseFailures();
                                break;
                            default:
                                break;
                        }
                        TestStatusValue statusValue = TestStatusValue.valueOf(currentTestCaseResult.name());
                        onUpdateResult(statusValue);
                        
                        XmlLogRecord testCaseRecord = startRecords.peek();
                        String name = testCaseRecord.getProperties().get("name");
                    	ExecutionEntityResult result = new ExecutionEntityResult();
                    	result.setName(name);
                    	result.setTestStatusValue(statusValue);
                    	notifyProccess(LauncherEvent.UPDATE_RECORD, executedEntity, result);
                    	
                    	
                        currentTestCaseResult = LogLevel.NOT_RUN;
                        
                    }
                    logDepth--;
                    startRecords.pop();

                    if (logDepth == 0) {
                        watchdog.stop();
                    }
                    break;
                default:
                    if (LogLevel.getResultLogs().contains(logLevel) && isLogUnderTestCaseMainLevel(runConfig, logDepth)
                            && isStartTestCaseLog(startRecords.peek())) {
                        currentTestCaseResult = logLevel;
                    }
                    break;
            }
        }
    }

    /**
     * Children may override this
     */
    protected void onUpdateRecord(XmlLogRecord record) {
        notifyLauncherChanged(LauncherEvent.UPDATE_RECORD, record);
    }

    @Override
    public List<XmlLogRecord> getLogRecords() {
        return logRecords;
    }

    protected void clearRecords() {
        logRecords.clear();
    }

    @Override
    protected void onStartExecutionComplete() {
        super.onStartExecutionComplete();
    }

    @Override
    protected void postExecutionComplete() {
        super.postExecutionComplete();
    }
}
