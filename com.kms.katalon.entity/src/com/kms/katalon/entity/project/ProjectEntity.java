package com.kms.katalon.entity.project;

import java.io.File;

import com.kms.katalon.entity.file.IntegratedFileEntity;

public class ProjectEntity extends IntegratedFileEntity {
	private static final long serialVersionUID = 1L;
	private short pageLoadTimeout;
	private String folderLocation;

	public short getPageLoadTimeout() {
		return this.pageLoadTimeout;
	}

	public void setPageLoadTimeout(short pageLoadTimeout) {
		this.pageLoadTimeout = pageLoadTimeout;
	}

	@Override
	public String getFileExtension() {
		return getProjectFileExtension();
	}

	public static String getProjectFileExtension() {
		return ".prj";
	}

	@Override
	public String getLocation() {
		return getFolderLocation() + File.separator + name + getFileExtension();
	}

	public String getFolderLocation() {
		return folderLocation;
	}

	public void setFolderLocation(String folderLocation) {
		this.folderLocation = folderLocation;
	}
}
