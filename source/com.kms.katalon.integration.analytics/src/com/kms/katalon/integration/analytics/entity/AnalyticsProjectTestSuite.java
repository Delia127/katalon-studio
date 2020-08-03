package com.kms.katalon.integration.analytics.entity;

public class AnalyticsProjectTestSuite {
	private long id;
	private String name;
	private String path;
	private AnalyticsProject project;
	private String alias;
	private String urlId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public AnalyticsProject getProject() {
		return project;
	}

	public void setProject(AnalyticsProject project) {
		this.project = project;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUrlId() {
		return urlId;
	}

	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}

	public AnalyticsProjectTestSuite(long id, String name, String path, AnalyticsProject project, String alias,
			String urlId) {
		super();
		this.id = id;
		this.name = name;
		this.path = path;
		this.project = project;
		this.alias = alias;
		this.urlId = urlId;
	}

}
