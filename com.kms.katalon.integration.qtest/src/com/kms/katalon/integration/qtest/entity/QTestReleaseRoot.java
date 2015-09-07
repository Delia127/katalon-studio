package com.kms.katalon.integration.qtest.entity;

import java.util.List;

public class QTestReleaseRoot extends QTestSuiteParent {
	private List<QTestSuiteParent> children;

	@Override
	public List<QTestSuiteParent> getChildren() {
		return children;
	}

	@Override
	public QTestSuiteParent getParent() {
		return null;
	}

	@Override
	public int getType() {
		return QTestSuiteParent.RELEASE_ROOT_TYPE;
	}

	public void setChildren(List<QTestSuiteParent> children) {
		this.children = children;
	}

	@Override
	public String getTypeName() {
		return "Project";
	}
	
}
