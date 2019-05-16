package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class UserFileTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = 4245614492827430989L;
    
    public static final String[] SEARCH_TAGS = new String[] { "id", "name" };
    
    public UserFileTreeEntity(UserFileEntity entity, ITreeEntity parentTreeEntity) {
        super(entity, parentTreeEntity);
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        switch (entity.getFileExtension()) {
            case ".png":
            case ".jpg":
            case ".bmp":
            case ".jpeg":
                return ImageConstants.IMG_16_IMG_TEST_OBJECT;
            case ".feature":
                return ImageConstants.IMG_16_FEATURE;
            case ".git":
            case ".gitignore":
                return ImageConstants.IMG_16_GIT_FILE;
            case ".txt":
                return ImageConstants.IMG_16_TXT_TEST_OBJECT;
            case ".jar":
                return ImageConstants.IMG_16_JAVA;
        }
        return ImageConstants.IMG_16_CONFIG;
    }

    @Override
    public String getTypeName() throws Exception {
        return "File";
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.USER.toString();
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
        return "uf";
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
            default:
                return StringUtils.EMPTY;
        }
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
    }

}
