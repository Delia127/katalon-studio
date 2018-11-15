package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class SystemFileTreeEntity extends AbstractTreeEntity {
    private static final long serialVersionUID = -6134975225093034560L;
    
    public static final String[] SEARCH_TAGS = new String[] { "id", "name" };

    public SystemFileTreeEntity(SystemFileEntity entity, FolderTreeEntity parentTreeEntity) {
        super(entity, parentTreeEntity);
    }
    
    @Override
    public SystemFileEntity getObject() throws Exception {
        return (SystemFileEntity) super.getObject();
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        if (".feature".equals(getObject().getFileExtension())) {
            return ImageConstants.IMG_16_FEATURE;
        } else if (".properties".equals(getObject().getFileExtension())) {
            return ImageConstants.IMG_16_CONFIG;
        }
        return null;
    }

    @Override
    public String getTypeName() throws Exception {
        return FolderType.INCLUDE.toString();
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.INCLUDE.toString();
    }

    @Override
    public boolean hasChildren() throws Exception {
        return false;
    }

    @Override
    public boolean isRemoveable() throws Exception {
        return true;
    }

    @Override
    public boolean isRenamable() throws Exception {
        return true;
    }

    @Override
    public Transfer getEntityTransfer() throws Exception {
        return null;
    }

    @Override
    public String getKeyWord() throws Exception {
        return "file";
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return new String[0];
    }

    @Override
    public String getPropertyValue(String key) {
        switch (key) {
            case "id":
                return entity.getId();
            case "name":
                return entity.getName();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
        
    }
}
