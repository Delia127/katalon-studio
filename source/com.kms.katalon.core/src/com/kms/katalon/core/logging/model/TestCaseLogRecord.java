package com.kms.katalon.core.logging.model;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

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

    @Override
    public TestStatus getStatus() {
        if (getChildRecords().length == 0) {
            TestStatus testStatus = new TestStatus();
            testStatus.setStatusValue(TestStatusValue.PASSED);
            if (childRecords.size() > 0) {
                ILogRecord logRecord = childRecords.get(childRecords.size() - 1);
                setMessage(logRecord.getMessage());
                return logRecord.getStatus();
            }

            return testStatus;
        } else {
            return super.getStatus();
        }
    }
}
