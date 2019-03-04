package com.kms.katalon.entity;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.kms.katalon.entity.file.ClonableObject;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public abstract class Entity extends ClonableObject implements IEntity {
    protected static final long serialVersionUID = 1L;

    protected String id;

    protected Date dateCreated;

    protected Date dateModified;

    protected String name;

    protected String tag;

    protected String description;

    protected FolderEntity parentFolder;

    protected ProjectEntity project;

    protected Entity() {
        description = "";
        tag = "";
        id = "";
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getDateCreated() {
        return this.dateCreated;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public Date getDateModified() {
        return this.dateModified;
    }

    @Override
    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public FolderEntity getParentFolder() {
        return parentFolder;
    }

    @Override
    public void setParentFolder(FolderEntity parentFolder) {
        this.parentFolder = parentFolder;
    }

    @Override
    public ProjectEntity getProject() {
        return project;
    }

    @Override
    public void setProject(ProjectEntity project) {
        this.project = project;
    };

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Entity clone() {
        return (Entity) super.clone();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Entity)) {
            return false;
        }
        Entity that = (Entity) object;
        return new EqualsBuilder().append(this.getId(), that.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31).append(this.getId()).toHashCode();
    }
}
