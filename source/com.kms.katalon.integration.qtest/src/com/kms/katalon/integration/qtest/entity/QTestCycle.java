package com.kms.katalon.integration.qtest.entity;

import java.util.List;

public class QTestCycle extends QTestSuiteParent {
	private QTestRelease parent;
	
	public QTestCycle() {
		
	}
	
	public QTestCycle(QTestRelease release) {
		parent = release;
	}

	@Override
	public int getType() {
		return QTestSuiteParent.CYCLE_TYPE;
	}

	@Override
	public List<QTestSuiteParent> getChildren() {
		return null;
	}

	@Override
	public QTestSuiteParent getParent() {
		return parent;
	}

	@Override
	public String getTypeName() {
		return "Cycle";
	}
}
