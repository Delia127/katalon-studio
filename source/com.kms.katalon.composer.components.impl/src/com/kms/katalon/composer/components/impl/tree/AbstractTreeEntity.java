package com.kms.katalon.composer.components.impl.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.tree.TooltipPropertyDescription;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.file.FileEntity;

public abstract class AbstractTreeEntity implements ITreeEntity {
    protected static final long serialVersionUID = 1L;

    protected FileEntity entity;

    protected ITreeEntity parentTreeEntity;

    protected AbstractTreeEntity(FileEntity entity, ITreeEntity parentTreeEntity) {
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
        if (object instanceof FileEntity) {
            entity = (FileEntity) object;
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

    @Override
    public List<TooltipPropertyDescription> getTooltipDescriptions() {
        List<TooltipPropertyDescription> properties = new ArrayList<>();
        properties
                .add(TooltipPropertyDescription.createWithDefaultLength(StringConstants.ID, entity.getIdForDisplay()));
        properties.add(TooltipPropertyDescription.createWithDefaultLength(StringConstants.NAME, entity.getName()));
        properties.add(TooltipPropertyDescription.create(StringConstants.DESCRIPTION, entity.getDescription()));
        return properties;
    }

    @Override
    public com.katalon.platform.api.model.Entity toPlatformEntity() {
        return new com.katalon.platform.api.model.Entity() {

            @Override
            public String getName() {
                return entity.getName();
            }

            @Override
            public String getId() {
                return entity.getIdForDisplay();
            }

            @Override
            public String getFolderLocation() {
                return entity.getParentFolder() != null ? entity.getParentFolder().getLocation() : null;
            }

            @Override
            public String getFileLocation() {
                return entity.getLocation();
            }
        };
    }
}
