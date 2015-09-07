package com.kms.katalon.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public abstract class Entity implements IEntity, Serializable, Cloneable {
	protected static final long serialVersionUID = 1L;
	protected String id;
	protected Date dateCreated;
	protected Date dateModified;
	protected String name;
	protected String description;
	protected FolderEntity parentFolder;
	protected ProjectEntity project;

	protected Entity() {
		description = "";
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

	public void setProject(ProjectEntity project) {
		this.project = project;
	};

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Entity clone() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(this);

			byte[] bytes = bos.toByteArray();
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

			return (Entity) ois.readObject();
		} catch (IOException | ClassNotFoundException exception) {
			return null;
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (!(object instanceof Entity)) {
			return false;
		}

		Entity entity = (Entity) object;

		if (entity.getId().equals(this.getId())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).append(this.getId()).toHashCode();
	}
}
