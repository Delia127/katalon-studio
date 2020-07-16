package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsExecution {
	private AnalyticsExecutionStatus status;
	private Date startTime;
	private long duration;
	private int totalTests;
	private int totalPassedTests;
	private int totalFailedTests;
	private int totalErrorTests;
	private int totalIncompletedTests;
	private int totalDiffTests;
	private int totalDiffPassedTests;
	private int totalDiffFailedTests;
	private int totalDiffErrorTests;
	private int totalDiffIncompletedTests;
	private long id;
	private long projectId;
	private AnalyticsProject project;
	private int order;
	private AnalyticsExecutionStage executionStage;
	private String webUrl;
	private boolean hasComment;
	private AnalyticsUser user;
	private String sessionId;
	private AnalyticsTestSuiteResource[] executionTestSuiteResources;
	
	public AnalyticsExecutionStatus getStatus() {
		return status;
	}
	public void setStatus(AnalyticsExecutionStatus status) {
		this.status = status;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public int getTotalTests() {
		return totalTests;
	}
	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	}
	public int getTotalPassedTests() {
		return totalPassedTests;
	}
	public void setTotalPassedTests(int totalPassedTests) {
		this.totalPassedTests = totalPassedTests;
	}
	public int getTotalFailedTests() {
		return totalFailedTests;
	}
	public void setTotalFailedTests(int totalFailedTests) {
		this.totalFailedTests = totalFailedTests;
	}
	public int getTotalErrorTests() {
		return totalErrorTests;
	}
	public void setTotalErrorTests(int totalErrorTests) {
		this.totalErrorTests = totalErrorTests;
	}
	public int getTotalIncompletedTests() {
		return totalIncompletedTests;
	}
	public void setTotalIncompletedTests(int totalIncompletedTests) {
		this.totalIncompletedTests = totalIncompletedTests;
	}
	public int getTotalDiffTests() {
		return totalDiffTests;
	}
	public void setTotalDiffTests(int totalDiffTests) {
		this.totalDiffTests = totalDiffTests;
	}
	public int getTotalDiffPassedTests() {
		return totalDiffPassedTests;
	}
	public void setTotalDiffPassedTests(int totalDiffPassedTests) {
		this.totalDiffPassedTests = totalDiffPassedTests;
	}
	public int getTotalDiffFailedTests() {
		return totalDiffFailedTests;
	}
	public void setTotalDiffFailedTests(int totalDiffFailedTests) {
		this.totalDiffFailedTests = totalDiffFailedTests;
	}
	public int getTotalDiffErrorTests() {
		return totalDiffErrorTests;
	}
	public void setTotalDiffErrorTests(int totalDiffErrorTests) {
		this.totalDiffErrorTests = totalDiffErrorTests;
	}
	public int getTotalDiffIncompletedTests() {
		return totalDiffIncompletedTests;
	}
	public void setTotalDiffIncompletedTests(int totalDiffIncompletedTests) {
		this.totalDiffIncompletedTests = totalDiffIncompletedTests;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public AnalyticsProject getProject() {
		return project;
	}
	public void setProject(AnalyticsProject project) {
		this.project = project;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public AnalyticsExecutionStage getExecutionStage() {
		return executionStage;
	}
	public void setExecutionStage(AnalyticsExecutionStage executionStage) {
		this.executionStage = executionStage;
	}
	public String getWebUrl() {
		return webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	public boolean isHasComment() {
		return hasComment;
	}
	public void setHasComment(boolean hasComment) {
		this.hasComment = hasComment;
	}
	public AnalyticsUser getUser() {
		return user;
	}
	public void setUser(AnalyticsUser user) {
		this.user = user;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public AnalyticsTestSuiteResource[] getExecutionTestSuiteResources() {
		return executionTestSuiteResources;
	}
	public void setExecutionTestSuiteResources(AnalyticsTestSuiteResource[] executionTestSuiteResources) {
		this.executionTestSuiteResources = executionTestSuiteResources;
	}
	public AnalyticsExecution(AnalyticsExecutionStatus status, Date startTime, long duration, int totalTests, int totalPassedTests,
			int totalFailedTests, int totalErrorTests, int totalIncompletedTests, int totalDiffTests,
			int totalDiffPassedTests, int totalDiffFailedTests, int totalDiffErrorTests, int totalDiffIncompletedTests,
			long id, long projectId, AnalyticsProject project, int order, AnalyticsExecutionStage executionStage, String webUrl,
			boolean hasComment, AnalyticsUser user, String sessionId, AnalyticsTestSuiteResource[] executionTestSuiteResources) {
		super();
		this.status = status;
		this.startTime = startTime;
		this.duration = duration;
		this.totalTests = totalTests;
		this.totalPassedTests = totalPassedTests;
		this.totalFailedTests = totalFailedTests;
		this.totalErrorTests = totalErrorTests;
		this.totalIncompletedTests = totalIncompletedTests;
		this.totalDiffTests = totalDiffTests;
		this.totalDiffPassedTests = totalDiffPassedTests;
		this.totalDiffFailedTests = totalDiffFailedTests;
		this.totalDiffErrorTests = totalDiffErrorTests;
		this.totalDiffIncompletedTests = totalDiffIncompletedTests;
		this.id = id;
		this.projectId = projectId;
		this.project = project;
		this.order = order;
		this.executionStage = executionStage;
		this.webUrl = webUrl;
		this.hasComment = hasComment;
		this.user = user;
		this.sessionId = sessionId;
		this.executionTestSuiteResources = executionTestSuiteResources;
	}
	
	
}
