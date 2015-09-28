package com.kms.katalon.composer.objectrepository.util;

public class QatWebElement {
	private String id;
	private String name;
	private boolean isPage;
	private String parentId;
	private String pageTitle;
	private String pageUrl;
	private String path;
	private String uIControl;
	private String value;
	private String variableStyle;

	public String getId() {
		return id;
	}

	public void setId(String elementId) {
		this.id = elementId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUIControl() {
		return uIControl;
	}

	public void setUIControl(String uIControl) {
		this.uIControl = uIControl;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getVariableStyle() {
		return variableStyle;
	}

	public void setVariableStyle(String variableStyle) {
		this.variableStyle = variableStyle;
	}

	public boolean isPage() {
		return isPage;
	}

	public void setIsPage(String isPage) {
		this.isPage = Boolean.valueOf(isPage);
	}

}