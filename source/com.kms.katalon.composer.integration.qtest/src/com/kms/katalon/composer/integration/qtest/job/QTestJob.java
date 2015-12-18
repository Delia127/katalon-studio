package com.kms.katalon.composer.integration.qtest.job;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public abstract class QTestJob extends Job {
    private List<IntegratedFileEntity> fileEntities;
    protected String token;
    protected ProjectEntity projectEntity;

    public QTestJob(String name) {
        super(name);
        projectEntity = ProjectController.getInstance().getCurrentProject();
    }

    public List<IntegratedFileEntity> getFileEntities() {
        return fileEntities;
    }

    public void setFileEntities(List<IntegratedFileEntity> fileEntities) {
        this.fileEntities = fileEntities;
    }

    public void doTask() {
        schedule();
    }

    /**
     * Sometimes, test case's name is very long and we need to wrap it smaller.
     */
    protected String getWrappedName(String name) {
        String wrappedName = name;
        if (name.length() > 80) {
            name = name.substring(name.length() - 80, name.length());
        }

        return wrappedName;
    }
    
    protected String getProjectDir() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }
}
