package com.kms.katalon.core.logging.model;

import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public class TestCaseLogRecord extends AbstractLogRecord {
    private boolean isOptional;

    public TestCaseLogRecord(String name) {
        super(name);
        setType(ILogRecord.LOG_TYPE_TEST_CASE);
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
    public List<ILogRecord> getChildren() {
        return super.getChildren().stream()
                .filter(item -> item instanceof TestStepLogRecord)
                .collect(Collectors.toList());
    }

    @Override
    public TestStatus getStatus() {
        TestStatus testStatus = super.getStatus();

        if (isInterrupted()) {
            testStatus.setStatusValue(TestStatusValue.INCOMPLETE);
            return testStatus;
        }

        if (getChildRecords().length == 0) {
            testStatus.setStatusValue(TestStatusValue.PASSED);
            if (children.size() > 0) {
                ILogRecord logRecord = children.get(children.size() - 1);
                setMessage(logRecord.getMessage());
                return logRecord.getStatus();
            }
        }

        return testStatus;
    }
}
