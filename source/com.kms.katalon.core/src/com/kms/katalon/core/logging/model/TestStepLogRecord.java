package com.kms.katalon.core.logging.model;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.model.FailureHandling;

public class TestStepLogRecord extends AbstractLogRecord {
	private List<String> arguments;
	private String attachment;
	private FailureHandling flowControl;
	private int index;
	
    public TestStepLogRecord(String name) {
		super(name);
	}

	public List<String> getArguments() {
		if (arguments == null) {
			arguments = new ArrayList<String>();
		}
		return arguments;
	}

	public void setArguments(List<String> args) {
		this.arguments = args;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public TestStatus getStatus() {
		TestStatus testStatus = new TestStatus();
		testStatus.setStatusValue(TestStatusValue.PASSED);
		for (ILogRecord logRecord : getChildRecords()) {
			if (logRecord instanceof TestStepLogRecord) {
				setAttachment(((TestStepLogRecord) logRecord).getAttachment());
			} else if (logRecord instanceof MessageLogRecord) {
				setAttachment(((MessageLogRecord) logRecord).getAttachment());
			}
			if (!(logRecord instanceof TestCaseLogRecord && ((TestCaseLogRecord) logRecord).isOptional())
					&& (logRecord.getStatus().getStatusValue() == TestStatusValue.ERROR || logRecord.getStatus()
							.getStatusValue() == TestStatusValue.FAILED)) {
				if (logRecord.getStatus().getStatusValue() == TestStatusValue.ERROR) {
					testStatus.setStatusValue(TestStatusValue.ERROR);
				} else if (logRecord
							.getStatus().getStatusValue() == TestStatusValue.FAILED) {
					testStatus.setStatusValue(TestStatusValue.FAILED);
				}
				setMessage(logRecord.getMessage());
				break;
			}
		}
		return testStatus;
	}

	public FailureHandling getFlowControl() {
		return flowControl;
	}

	public void setFlowControl(FailureHandling flowControl) {
		this.flowControl = flowControl;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getIndexString() {
	    int stepIndex = index;
	    if (stepIndex == -1 && parentLogRecord != null) {
	        for (int i = 0; i < parentLogRecord.getChildRecords().length; i++) {
	            if (parentLogRecord.getChildRecords()[i] == this) {
	                stepIndex = i + 1;
	                break;
	            }
	        }
	    }
	    if (parentLogRecord == null || !(parentLogRecord instanceof TestStepLogRecord)) {
	        return String.valueOf(stepIndex);
	    } else {
	        return ((TestStepLogRecord) parentLogRecord).getIndexString() + "." + stepIndex;
	    }
	}

}
