package com.kms.katalon.entity;

import java.util.Date;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public interface IEntity {
    public Date getDateCreated();

    public void setDateCreated(Date dateCreated);

    public Date getDateModified();

    public void setDateModified(Date dateModified);

    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    public String getTag();

    public void setTag(String tag);

    public String getDescription();

    public void setDescription(String description);

    public FolderEntity getParentFolder();

    public void setParentFolder(FolderEntity folder);

    public ProjectEntity getProject();

    public void setProject(ProjectEntity project);
}
