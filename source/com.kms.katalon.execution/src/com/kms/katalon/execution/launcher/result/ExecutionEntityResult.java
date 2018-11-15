package com.kms.katalon.execution.launcher.result;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.execution.entity.IExecutedEntity;

public class ExecutionEntityResult {

	private String name;
	
	private String description;
	
	private TestStatusValue testStatusValue;
	
	private IExecutedEntity executedEntity;
	
	private Object event;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TestStatusValue getTestStatusValue() {
		return testStatusValue;
	}

	public void setTestStatusValue(TestStatusValue testStatusValue) {
		this.testStatusValue = testStatusValue;
	}

	public IExecutedEntity getExecutedEntity() {
		return executedEntity;
	}

	public void setExecutedEntity(IExecutedEntity executedEntity) {
		this.executedEntity = executedEntity;
	}

	public Object getEvent() {
		return event;
	}

	public void setEvent(Object event) {
		this.event = event;
	}

	
	
}
