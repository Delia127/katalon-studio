package com.kms.katalon.composer.execution.provider;

import java.util.ArrayList;
import java.util.List;

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
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;

public class LogTableViewer extends TableViewer {

	private List<XmlLogRecord> records;
	private int logDepth;
	private IEventBroker eventBroker;
	private IPreferenceStore store;

	public LogTableViewer(Composite parent, int style, IEventBroker eventBroker) {
		super(parent, style);
		this.eventBroker = eventBroker;
		this.setContentProvider(new ArrayContentProvider());
		store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
		clearAll();
	}

	public void clearAll() {
		records = new ArrayList<XmlLogRecord>();
		super.setInput(records);
		logDepth = 0;
	}

	@Override
	public void add(Object object) {
		if (object != null && object instanceof XmlLogRecord) {
			XmlLogRecord record = (XmlLogRecord) object;
			records.add(record);
			super.add(record);

			if (record.getLevel().equals(LogLevel.END)) {
				logDepth--;
				if (record.getSourceMethodName().equals(
						com.kms.katalon.core.constants.StringConstants.LOG_END_TEST_METHOD)) {
					if (logDepth == 0 || logDepth == 1) {
						eventBroker.send(EventConstants.CONSOLE_LOG_UPDATE_PROGRESS_BAR,
								records.get(records.size() - 2));
					}
				}
			} else if (record.getLevel().equals(LogLevel.START)) {
				logDepth++;
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
			if (record.getLevel().equals(LogLevel.PASSED)) {
				item.setBackground(ColorUtil.getPassedLogBackgroundColor());
				item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			} else if (record.getLevel().equals(LogLevel.FAILED)) {
				item.setBackground(ColorUtil.getFailedLogBackgroundColor());
				item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			} else if (record.getLevel().equals(LogLevel.ERROR) || record.getLevel().equals(LogLevel.WARNING)) {
				item.setBackground(ColorUtil.getWarningLogBackgroundColor());
				item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
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
		return !store.getBoolean(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_PIN_LOG);
	}

	public List<XmlLogRecord> getRecords() {
		return records;
	}
}
