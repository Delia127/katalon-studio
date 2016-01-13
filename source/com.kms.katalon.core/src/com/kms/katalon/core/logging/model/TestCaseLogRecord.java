package com.kms.katalon.core.logging.model;

import java.util.ArrayList;
import java.util.List;

public class TestCaseLogRecord extends AbstractLogRecord {
    private boolean isOptional;

    public TestCaseLogRecord(String name) {
        super(name);
    }

    /**
     * Returns if the result of current test case is optional or not.
     * <p>
     * Used when the current test case is called by another test case.
     * 
     * @return true if result of this is optional. Otherwise, false.
     */
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
        TestStatus testStatus = super.getStatus(); 

//        if (isInterrupted()) {
//            testStatus.setStatusValue(TestStatusValue.INCOMPLETE);
//            return testStatus;
//        }
//        
//        if (getChildRecords().length == 0) {
//            testStatus.setStatusValue(TestStatusValue.PASSED);
//            if (childRecords.size() > 0) {
//                ILogRecord logRecord = childRecords.get(childRecords.size() - 1);
//                setMessage(logRecord.getMessage());
//                return logRecord.getStatus();
//            }
//        }
        return testStatus;
    }
}
