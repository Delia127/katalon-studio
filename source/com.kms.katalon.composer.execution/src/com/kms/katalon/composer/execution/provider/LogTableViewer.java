package com.kms.katalon.composer.execution.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class LogTableViewer extends TableViewer {

    private static final int DEPTH_OF_MAIN_TEST_CASE = 0;

    private List<XmlLogRecord> records;
    private int logDepth;
    private IEventBroker eventBroker;
    private IPreferenceStore store;

    // Represents the latest test case's result record, used to update progress bar of table viewer
    private LogRecord latestResultRecord;

    public LogTableViewer(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);
        this.eventBroker = eventBroker;
        this.setContentProvider(new ArrayContentProvider());
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
        clearAll();
    }

    public void clearAll() {
        records = new ArrayList<XmlLogRecord>();
        super.setInput(records);
        logDepth = DEPTH_OF_MAIN_TEST_CASE;
        latestResultRecord = null;
    }

    @Override
    public void add(Object object) {
        if (object != null && object instanceof XmlLogRecord) {
            XmlLogRecord record = (XmlLogRecord) object;
            records.add(record);
            super.add(record);

            LogLevel logLevel = LogLevel.valueOf(record.getLevel());
            switch (logLevel) {
            case END:
                logDepth--;
                if (StringConstants.LOG_END_TEST_METHOD.equals(record.getSourceMethodName())) {
                    eventBroker.send(EventConstants.CONSOLE_LOG_UPDATE_PROGRESS_BAR, latestResultRecord);
                }
                break;
            case START:
                String startName = record.getSourceMethodName();
                if (!StringConstants.LOG_START_SUITE_METHOD.equals(startName)) {
                    logDepth++;
                }
                break;
            default:
                if (LogLevel.getResultLogs().contains(logLevel) && logDepth == DEPTH_OF_MAIN_TEST_CASE + 1) {
                    latestResultRecord = record;
                }
                break;
            }

            updateTableBackgroundColor();
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        super.setInput(records);
        updateTableBackgroundColor();
    }

    private void updateTableBackgroundColor() {
        Table table = this.getTable();
        for (TableItem item : table.getItems()) {
            XmlLogRecord record = (XmlLogRecord) item.getData();
            LogLevel logLevel = LogLevel.valueOf(record.getLevel());
            if (logLevel == null) {
                continue;
            }
            
            switch (logLevel) {
            case PASSED:
                item.setBackground(ColorUtil.getPassedLogBackgroundColor());
                item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                break;
            case FAILED:
                item.setBackground(ColorUtil.getFailedLogBackgroundColor());
                item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                break;
            case ERROR:
                item.setBackground(ColorUtil.getErrorLogBackgroundColor());
                item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                break;
            case WARNING:
                item.setBackground(ColorUtil.getWarningLogBackgroundColor());
                item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                break;
            default:
                break;
            }
        }

        if (isScrollLogEnable()) {
            int lastItemIndex = table.getItemCount() - 1;
            if (lastItemIndex >= 0) {
                table.showItem(table.getItem(lastItemIndex));
            }
        }
    }

    private boolean isScrollLogEnable() {
        return !store.getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_PIN_LOG);
    }

    public List<XmlLogRecord> getRecords() {
        return records;
    }
}
