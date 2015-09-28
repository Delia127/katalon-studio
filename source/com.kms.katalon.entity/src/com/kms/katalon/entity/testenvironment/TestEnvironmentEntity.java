package com.kms.katalon.entity.testenvironment;

import com.kms.katalon.entity.file.FileEntity;

public class TestEnvironmentEntity extends FileEntity {

	private static final long serialVersionUID = 1L;

	private String operationSystem;

	private String hostName;

	private String tesEnvironmentGuid;

	public String getOperationSystem() {
		return this.operationSystem;
	}

	public void setOperationSystem(String operationSystem) {
		this.operationSystem = operationSystem;
	}

	public String getHostName() {
		return this.hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getTesEnvironmentGuid() {
		return this.tesEnvironmentGuid;
	}

	public void setTesEnvironmentGuid(String tesEnvironmentGuid) {
		this.tesEnvironmentGuid = tesEnvironmentGuid;
	}
	
	public static String getTestEnvironmentFileExtension() {
		return ".env";
	}

	@Override
	public String getFileExtension() {
		return getTestEnvironmentFileExtension();
	}

	@Override
	public String getLocation() {
		return tesEnvironmentGuid;
	}
}
