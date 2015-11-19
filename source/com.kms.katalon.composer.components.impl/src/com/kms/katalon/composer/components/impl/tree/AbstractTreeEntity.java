package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.entity.Entity;

public abstract class AbstractTreeEntity implements ITreeEntity {
	protected static final long serialVersionUID = 1L;
	protected Entity entity;
	protected ITreeEntity parentTreeEntity;

	protected AbstractTreeEntity(Entity entity, ITreeEntity parentTreeEntity) {
		this.entity = entity;
		this.parentTreeEntity = parentTreeEntity;
	}

	@Override
	public Object getObject() throws Exception {
		return entity;
	}

	@Override
	public ITreeEntity getParent() throws Exception {
		return parentTreeEntity;
	}

	@Override
	public void setObject(Object object) throws Exception {
		if (object instanceof Entity) {
			entity = (Entity) object;
		}
	}

	@Override
	public String getText() throws Exception {
		return entity.getName();
	}

	@Override
	public boolean equals(Object object) {
		try {
			if (!(object instanceof AbstractTreeEntity)) {
				return false;
			}
			AbstractTreeEntity anotherTreeEntity = (AbstractTreeEntity) object;
			if (!(anotherTreeEntity.getObject() instanceof Entity)) {
				return false;
			}
			Entity anotherEntity = (Entity) anotherTreeEntity.getObject();
			return entity.equals(anotherEntity);
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).append(entity.hashCode()).toHashCode();
	}

}
