package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class CheckpointTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = 6128596821855400571L;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name", "description" };

    public static final String KEY_WORD = StringConstants.ENTITY_KW_CHECKPOINT;

    public CheckpointTreeEntity(CheckpointEntity entity, ITreeEntity parentTreeEntity) {
        super(entity, parentTreeEntity);
    }

    @Override
    public CheckpointEntity getObject() {
        return (CheckpointEntity) entity;
    }

    @Override
    public Object[] getChildren() {
        return null;
    }

    @Override
    public Image getImage() {
        return ImageConstants.IMG_16_CHECKPOINT;
    }

    @Override
    public String getTypeName() {
        return StringConstants.CHECKPOINT;
    }

    @Override
    public String getCopyTag() {
        return FolderType.CHECKPOINT.toString();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean isRemoveable() {
        return true;
    }

    @Override
    public boolean isRenamable() {
        return true;
    }

    @Override
    public Transfer getEntityTransfer() {
        return TreeEntityTransfer.getInstance();
    }

    @Override
    public void setObject(Object object) {
        if (object instanceof CheckpointEntity) {
            entity = (CheckpointEntity) object;
        }
    }

    @Override
    public FolderTreeEntity getParent() {
        return (FolderTreeEntity) parentTreeEntity;
    }

    @Override
    public String getText() {
        if (entity == null) {
            return StringUtils.EMPTY;
        }
        return entity.getName();
    }

    @Override
    public String getKeyWord() {
        return KEY_WORD;
    }

    @Override
    public String[] getSearchTags() {
        return SEARCH_TAGS;
    }

    @Override
    public String getPropertyValue(String key) {
        CheckpointEntity checkpoint = getObject();
        if (checkpoint == null) {
            return StringUtils.EMPTY;
        }

        switch (key) {
            case "id":
                return checkpoint.getIdForDisplay();

            case "name":
                return checkpoint.getName();

            case "description":
                return checkpoint.getDescription();

            default:
                return StringUtils.EMPTY;
        }
    }

    @Override
    public Image getEntryImage() {
        return getImage();
    }

    @Override
    public void loadAllDescentdantEntities() {
        // nothing to do
    }

}
