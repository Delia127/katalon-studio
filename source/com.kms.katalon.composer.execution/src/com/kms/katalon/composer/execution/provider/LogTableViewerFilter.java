package com.kms.katalon.composer.execution.provider;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;

public class LogTableViewerFilter extends ViewerFilter {
    
    public static final int ALL        = 1 << 0;
    public static final int INFO       = 1 << 1;
    public static final int PASSED     = 1 << 2;
    public static final int FAILED     = 1 << 3;
    public static final int ERROR      = 1 << 4;

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        XmlLogRecord record = (XmlLogRecord) element;
        return ((evaluteLog(record) & getPreferenceShowedValue()) != 0);
    }
    
    private int getPreferenceShowedValue() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
        int showAllLogs = store.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS) ? ALL : 0;
        int showInfoLogs = store.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS) ? INFO : 0;
        int showPassedLogs = store.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS) ? PASSED : 0;
        int showFailedLogs = store.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS) ? FAILED : 0;
        int showIncompleteLogs = store.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS) ? ERROR : 0;
        
        return    (showAllLogs & ALL) 
                | (showInfoLogs & INFO) 
                | (showPassedLogs & PASSED) 
                | (showFailedLogs & FAILED)
                | (showIncompleteLogs & ERROR);
    }
    
    private int evaluteLog(XmlLogRecord record) {
        LogLevel level = (LogLevel) LogLevel.parse(record.getLevel().getName());
        int value = ALL;
        if (level == LogLevel.INFO) {
            value |= INFO;
        } else if (level == LogLevel.PASSED) {
            value |= PASSED;
        } else if (level == LogLevel.FAILED) {
            value |= FAILED;
        } else if (level == LogLevel.ERROR) {
            value |= ERROR;
        }
        return value;
    }

}
