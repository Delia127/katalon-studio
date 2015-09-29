package com.kms.katalon.composer.integration.qtest.model;

import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;

public class TestCaseRepo {
    private QTestProject qTestProject;
    private QTestModule qTestModule;
    private String folderId;

    public TestCaseRepo() {

    }

    public QTestProject getQTestProject() {
        return qTestProject;
    }

    public void setQTestProject(QTestProject qTestProject) {
        this.qTestProject = qTestProject;
    }

    public QTestModule getQTestModule() {
        return qTestModule;
    }

    public void setQTestModule(QTestModule qTestModule) {
        this.qTestModule = qTestModule;
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
        result = prime * result + ((qTestModule == null) ? 0 : qTestModule.hashCode());
        result = prime * result + ((qTestProject == null) ? 0 : qTestProject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TestCaseRepo other = (TestCaseRepo) obj;
        if (folderId == null) {
            if (other.folderId != null) return false;
        } else if (!folderId.equals(other.folderId)) return false;
        if (qTestModule == null) {
            if (other.qTestModule != null) return false;
        } else if (!qTestModule.equals(other.qTestModule)) return false;
        if (qTestProject == null) {
            if (other.qTestProject != null) return false;
        } else if (!qTestProject.equals(other.qTestProject)) return false;
        return true;
    }
}
