package com.kms.katalon.controller.entity;

import com.katalon.platform.api.model.Entity;

public class PluginProjectEntity implements Entity {
	private String fileLocation;
	private String folderLocation;
	private String id;
	private String name;

	@Override
	public String getFileLocation() {
		return fileLocation;
	}

	@Override
	public String getFolderLocation() {
		return folderLocation;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public void setFolderLocation(String folderLocation) {
		this.folderLocation = folderLocation;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}