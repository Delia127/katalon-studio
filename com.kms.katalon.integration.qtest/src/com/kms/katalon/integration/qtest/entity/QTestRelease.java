package com.kms.katalon.integration.qtest.entity;

import java.util.List;

public class QTestRelease extends QTestSuiteParent {
	private List<QTestSuiteParent> cycles;

	public List<QTestSuiteParent> getCycles() {
		return cycles;
	}

	public void setCycles(List<QTestSuiteParent> cycles) {
		this.cycles = cycles;
	}
	
	@Override
	public int getType() {
		return QTestSuiteParent.RELEASE_TYPE;
	}

	@Override
	public List<QTestSuiteParent> getChildren() {
		return cycles;
	}

	@Override
	public QTestSuiteParent getParent() {		
		return null;
	}

	@Override
	public String getTypeName() {
		return "Release";
	}
}
