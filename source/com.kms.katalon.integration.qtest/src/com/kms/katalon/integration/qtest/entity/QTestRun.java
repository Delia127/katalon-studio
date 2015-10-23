package com.kms.katalon.integration.qtest.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class QTestRun extends QTestEntity implements Serializable {

	private static final long serialVersionUID = -7907449918497976310L;
	private String href;
	private String testCaseLink;
	private String testLogsLink;
	private String executionStatusesLink;
	private boolean executed;
	
	private long qTestCaseId;
	private long testCaseVersionId;
	private long order;
	private long statusId;
	private String pid;
	
	public QTestRun(long id, String name) {
		super(id, name);
	}
	
	public QTestRun() {}
	
	public String getHref() {
		return href;
	}


	public void setHref(String href) {
		this.href = href;
	}


	public String getTestCaseLink() {
		return testCaseLink;
	}


	public void setTestCaseLink(String testCaseLink) {
		this.testCaseLink = testCaseLink;
	}


	public String getTestLogsLink() {
		return testLogsLink;
	}


	public void setTestLogsLink(String testLogsLink) {
		this.testLogsLink = testLogsLink;
	}


	public String getExecutionStatusesLink() {
		return executionStatusesLink;
	}


	public void setExecutionStatusesLink(String executionStatusesLink) {
		this.executionStatusesLink = executionStatusesLink;
	}


	public boolean isExecuted() {
		return executed;
	}


	public void setExecuted(boolean executed) {
		this.executed = executed;
	}


	public long getTestCaseVersionId() {
		return testCaseVersionId;
	}


	public void setTestCaseVersionId(long testCaseVersionId) {
		this.testCaseVersionId = testCaseVersionId;
	}


	public long getOrder() {
		return order;
	}


	public void setOrder(long order) {
		this.order = order;
	}


	public long getStatusId() {
		return statusId;
	}


	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}

	public long getQTestCaseId(){
		return qTestCaseId;
	}
	
	public void setQTestCaseId(long id){
		this.qTestCaseId = id;
	}
	
	public Map<String, Object> getMapProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(QTestEntity.ID_FIELD, id);
		properties.put(QTestEntity.NAME_FIELD, name);
		properties.put("qTestCaseId", qTestCaseId);
		properties.put("pid", getPid());
		
		return properties;
	}
	
	public static int getType() {
		return 3;
	}

    public String getPid() {
        if (pid == null) {
            pid = "";
        }
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
