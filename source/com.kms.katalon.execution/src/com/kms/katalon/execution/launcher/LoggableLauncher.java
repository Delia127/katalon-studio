package com.kms.katalon.execution.launcher;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.configuration.IHostConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.model.LauncherResult;
import com.kms.katalon.execution.logging.ILogCollection;
import com.kms.katalon.execution.logging.SocketWatcher;

public abstract class LoggableLauncher extends Launcher implements ILogCollection {
    private static final int DF_WATCHER_DELAY_TIME = 1;

    private List<XmlLogRecord> logRecords;

    /**
     * Returns the level of the current {@link XmlLogRecord}
     */
    private int logDepth;

    public LoggableLauncher(IRunConfiguration runConfig) {
        super(runConfig);
        logRecords = new ArrayList<XmlLogRecord>();
    }

    @Override
    protected void onStartExecution() {
        logDepth = 0;

        IHostConfiguration hostConfig = getRunConfig().getHostConfiguration();

        watchers.add(new SocketWatcher(hostConfig.getHostPort(), DF_WATCHER_DELAY_TIME, this));
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
                logDepth++;
                break;
            case END:
                logDepth--;

                if (logDepth == 0) {
                    watchdog.stop();
                }
                break;
            default:
                if (LogLevel.getResultLogs().contains(logLevel)
                        && logDepth == getRunConfig().getExecutionSetting().getExecutedEntity().mainTestCaseDepth() + 1) {
                    switch (logLevel) {
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
                    onUpdateStatus();
                }
                break;
            }
        }
    }

    /**
     * Children may override this
     */
    protected void onUpdateStatus() {
        // For children
    }

    /**
     * Children may override this
     */
    protected void onUpdateRecord(XmlLogRecord record) {
        // For children
    }

    public List<XmlLogRecord> getLogRecords() {
        return logRecords;
    }
}
