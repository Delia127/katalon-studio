package com.kms.katalon.composer.integration.qtest.model;

import org.apache.commons.lang.builder.EqualsBuilder;

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
        if (!(obj instanceof TestSuiteRepo)) {
            return false;
        }
        TestSuiteRepo that = (TestSuiteRepo) obj;
        return new EqualsBuilder().append(this.getFolderId(), that.getFolderId())
                .append(this.getQTestProject(), that.getQTestProject()).isEquals();
    }

}
