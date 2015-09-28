package com.kms.katalon.integration.qtest.entity;

import java.util.ArrayList;
import java.util.List;

public class QTestModule extends QTestEntity {
	private String gid;
	private long parentId;
	private List<QTestModule> childModules;
	private List<QTestTestCase> childTestCases;

	public QTestModule(long id, String name, long parentId) {
		super(id, name);
		this.parentId = parentId;
	}

	public String getGid() {
		if (gid == null) {
			gid = "";
		}
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public static int getType() {
		return 0;
	}

	public List<QTestModule> getChildModules() {
		if (childModules == null) {
			childModules = new ArrayList<QTestModule>();
		}
		return childModules;
	}

	public void setChildModules(List<QTestModule> childModules) {
		this.childModules = childModules;
	}

	public List<QTestTestCase> getChildTestCases() {
		if (childTestCases == null) {
			childTestCases = new ArrayList<QTestTestCase>();
		}
		return childTestCases;
	}

	public void setChildTestCases(List<QTestTestCase> childTestCases) {
		this.childTestCases = childTestCases;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (parentId ^ (parentId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		QTestModule other = (QTestModule) obj;
		if (id != other.id) return false;
		if (parentId != other.parentId) return false;
		return true;
	}
	    
}
