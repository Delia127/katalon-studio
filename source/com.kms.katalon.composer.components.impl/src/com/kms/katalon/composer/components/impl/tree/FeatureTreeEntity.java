package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.entity.file.FeatureEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class FeatureTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = -5786885226138264998L;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name" };

    public FeatureTreeEntity(FeatureEntity entity, FeatureFolderTreeEntity parentTreeEntity) {
        super(entity, parentTreeEntity);
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_FEATURE;
    }

    @Override
    public String getTypeName() throws Exception {
        return getCopyTag();
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.FEATURE.toString();
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
        return TreeEntityTransfer.getInstance();
    }

    @Override
    public String getKeyWord() throws Exception {
        return "ft";
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return SEARCH_TAGS;
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
        return ImageConstants.IMG_16_FEATURE;
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {}
    
    @Override
    public FeatureEntity getObject() throws Exception {
        return (FeatureEntity) super.getObject();
    }

}
