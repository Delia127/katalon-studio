package com.kms.katalon.core.logging.model;

public class MessageLogRecord extends AbstractLogRecord {
	private TestStatus testStatus;
	private String attachment;

	public MessageLogRecord() {
		super("");
	}
	
	public void setStatus(TestStatus testStatus) {
		this.testStatus = testStatus;
	}

	@Override
	public TestStatus getStatus() {
		return testStatus;
	}
	
	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
}
