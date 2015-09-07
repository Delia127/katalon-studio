package com.kms.katalon.execution.entity;

import com.kms.katalon.entity.link.TestDataCombinationType;

public class TestDataExecutedEntity {
	private String testDataLinkId;
	private String testDataId;	
	private int[] rowIndexes;
	private int multiplier;
	private TestDataCombinationType type;
	
	public TestDataExecutedEntity(String dataLinkId, String testDataId) {
		this.testDataLinkId = dataLinkId;
		this.testDataId = testDataId;
		this.multiplier = 1;
	}

	public String getTestDataLinkId() {
		return testDataLinkId;
	}

	public void setTestDataLinkId(String testDataLinkId) {
		this.testDataLinkId = testDataLinkId;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public TestDataCombinationType getType() {
		return type;
	}

	public void setType(TestDataCombinationType type) {
		this.type = type;
	}

	public int[] getRowIndexes() {
		return rowIndexes;
	}

	public void setRowIndexes(int[] rowIndexes) {
		this.rowIndexes = rowIndexes;
	}

	public String getTestDataId() {
		return testDataId;
	}

	public void setTestDataId(String testDataId) {
		this.testDataId = testDataId;
	}
}
