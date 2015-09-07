package com.kms.katalon.integration.qtest.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QTestSuite extends QTestEntity {

	private String pid;
	private QTestSuiteParent parent;
	private List<QTestRun> testRuns;
	private boolean isSelected;
	
	public QTestSuite(long id, String name, String pid, QTestSuiteParent parent) {
		super(id, name);
		setPid(pid);
		setParent(parent);
	}
	
	public QTestSuite() {
	}
	
	@Override
	public String getName() {
		if (name == null) name = "";
		return name;
	}

	public String getPid() {
		if (pid == null) pid = "";
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public static int getType() {
		return 2;
	}

	public QTestSuiteParent getParent() {
		return parent;
	}

	public void setParent(QTestSuiteParent parent) {
		this.parent = parent;
	}

	public List<QTestRun> getTestRuns() {
		if (testRuns == null) {
			testRuns = new ArrayList<QTestRun>();
		}
		return testRuns;
	}

	public void setTestRuns(List<QTestRun> testRuns) {
		this.testRuns = testRuns;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put(QTestEntity.ID_FIELD, getId());
		properties.put(QTestEntity.NAME_FIELD, getName());
		properties.put(QTestEntity.PID_FIELD, getPid());
		properties.put("default", isSelected());
		
		List<Map<String, Object>> testRunMap = new ArrayList<Map<String,Object>>();
		for (QTestRun testRun : getTestRuns()) {
			testRunMap.add(testRun.getMapProperties());
		}
		properties.put("testRuns", testRunMap);
		properties.put("parent", getParent().getPropertyMap());
		
		return properties;
		
	}
}
