package com.kms.katalon.composer.execution.provider;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kms.katalon.composer.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class LogTableViewerFilter extends ViewerFilter {

    public static final int ALL = 1 << 0;

    public static final int INFO = 1 << 1;

    public static final int PASSED = 1 << 2;

    public static final int FAILED = 1 << 3;

    public static final int ERROR = 1 << 4;

    public static final int WARNING = 1 << 5;
    
    public static final int NOT_RUN = 1 << 6;

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        XmlLogRecord record = (XmlLogRecord) element;
        return ((evaluteLog(record) & getPreferenceShowedValue()) != 0);
    }

    private int getPreferenceShowedValue() {
        ScopedPreferenceStore store = getPreferenceStore(LogTableViewerFilter.class);
        int showAllLogs = store.getBoolean(ExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS) ? ALL : 0;
        int showInfoLogs = store.getBoolean(ExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS) ? INFO : 0;
        int showPassedLogs = store.getBoolean(ExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS) ? PASSED : 0;
        int showFailedLogs = store.getBoolean(ExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS) ? FAILED : 0;
        int showIncompleteLogs = store.getBoolean(ExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS) ? ERROR : 0;
        int showWarningLogs = store.getBoolean(ExecutionPreferenceConstants.EXECUTION_SHOW_WARNING_LOGS) ? WARNING : 0;
        int showNotRunLogs = store.getBoolean(ExecutionPreferenceConstants.EXECUTION_SHOW_NOT_RUN_LOGS) ? NOT_RUN : 0;

        return (showAllLogs & ALL) | (showInfoLogs & INFO) | (showPassedLogs & PASSED) | (showFailedLogs & FAILED)
                | (showIncompleteLogs & ERROR) | (showWarningLogs & WARNING) | (showNotRunLogs & NOT_RUN);
    }

    private int evaluteLog(XmlLogRecord record) {
        LogLevel level = LogLevel.valueOf(record.getLevel().getName());
        int value = ALL;
        if (level == LogLevel.INFO) {
            value |= INFO;
        } else if (level == LogLevel.PASSED) {
            value |= PASSED;
        } else if (level == LogLevel.FAILED) {
            value |= FAILED;
        } else if (level == LogLevel.ERROR) {
            value |= ERROR;
        } else if (level == LogLevel.WARNING) {
            value |= WARNING;
        }  else if (level == LogLevel.NOT_RUN) {
            // TODO: Re-factor for return immediately without else-if
            value |= NOT_RUN;
        }
        return value;
    }

}
