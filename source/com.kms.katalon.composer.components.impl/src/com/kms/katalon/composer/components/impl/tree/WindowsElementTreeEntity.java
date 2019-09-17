package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class WindowsElementTreeEntity extends AbstractTreeEntity {
    private static final long serialVersionUID = -6414687029578671454L;

    public static final String KEY_WORD = StringConstants.TREE_OBJECT_KW;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name" };

    public WindowsElementTreeEntity(WindowsElementEntity entity, FolderTreeEntity parentTreeEntity) {
        super(entity, parentTreeEntity);
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {        
        return ImageConstants.IMG_16_WINDOWS_ENTITY;
    }

    @Override
    public String getTypeName() throws Exception {
        return StringConstants.TREE_OBJECT_TYPE_NAME;
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.WEBELEMENT.toString();
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
        return KEY_WORD;
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return SEARCH_TAGS;
    }

    @Override
    public String getPropertyValue(String key) {
        if (key.equals("name")) {
            return entity.getName();
        } else if (key.equals("id")) {
            return entity.getIdForDisplay();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return null;
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
        
    }

    @Override
    public WindowsElementEntity getObject() throws Exception {
        return (WindowsElementEntity) super.getObject();
    }
}
