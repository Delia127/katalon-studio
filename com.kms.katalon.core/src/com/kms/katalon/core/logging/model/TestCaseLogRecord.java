package com.kms.katalon.core.logging.model;

import java.util.ArrayList;
import java.util.List;

public class TestCaseLogRecord extends AbstractLogRecord {
	private boolean isOptional;

	public TestCaseLogRecord(String name) {
		super(name);
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	@Override
	public ILogRecord[] getChildRecords() {
		List<ILogRecord> resultRecords = new ArrayList<ILogRecord>();
		for (ILogRecord logRecord : childRecords) {
			if (logRecord instanceof TestStepLogRecord) {
				resultRecords.add(logRecord);
			}
		}
		return resultRecords.toArray(new ILogRecord[resultRecords.size()]);
	}
}
