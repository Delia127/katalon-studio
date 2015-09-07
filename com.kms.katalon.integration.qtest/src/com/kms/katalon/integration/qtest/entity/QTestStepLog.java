package com.kms.katalon.integration.qtest.entity;

import java.util.ArrayList;
import java.util.List;

public class QTestStepLog extends QTestEntity {

	private long qTestStepId;
	private long status;
	private String actualResult;
	private List<Long> defectIds;  
	private String selfLink;
	
	public List<Long> getDefectIds() {
		if(defectIds == null){
			defectIds = new ArrayList<>();
		}
		return defectIds;
	}

	public void setDefectIds(List<Long> defectIds) {
		this.defectIds = defectIds;
	}

	public QTestStepLog(long id, String name) {
		super(id, name);
	}
	
	public QTestStepLog() {
		super();
	}	
	
	public long getqTestStepId() {
		return qTestStepId;
	}
	public void setqTestStepId(long qTestStepId) {
		this.qTestStepId = qTestStepId;
	}
	public long getStatus() {
		return status;
	}
	public void setStatus(long status) {
		this.status = status;
	}
	public String getActualResult() {
		return actualResult;
	}
	public void setActualResult(String actualResult) {
		this.actualResult = actualResult;
	}

	public String getSelfLink() {
		return selfLink;
	}

	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}
}
