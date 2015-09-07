package com.kms.katalon.composer.integration.qtest.model;

import com.kms.katalon.integration.qtest.entity.QTestProject;

public class TestSuiteRepo {
	private QTestProject qTestProject;
	private String folderId;

	public QTestProject getQTestProject() {
		return qTestProject;
	}

	public void setQTestProject(QTestProject qTestProject) {
		this.qTestProject = qTestProject;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((folderId == null) ? 0 : folderId.hashCode());
		result = prime * result + ((qTestProject == null) ? 0 : qTestProject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TestSuiteRepo other = (TestSuiteRepo) obj;
		if (folderId == null) {
			if (other.folderId != null) return false;
		} else if (!folderId.equals(other.folderId)) return false;
		if (qTestProject == null) {
			if (other.qTestProject != null) return false;
		} else if (!qTestProject.equals(other.qTestProject)) return false;
		return true;
	}

}
