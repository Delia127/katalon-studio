package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.entity.file.TestListenerEntity;

public class TestListenerTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = -4172877789258620229L;

    public static final String[] SEARCH_TAGS = new String[] { "id", "name" };

    public TestListenerTreeEntity(TestListenerEntity entity, TestListenerFolderTreeEntity parentTreeEntity) {
        super(entity, parentTreeEntity);
    }

    @Override
    public TestListenerEntity getObject() throws Exception {
        return (TestListenerEntity) super.getObject();
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_KEYWORD;
    }

    @Override
    public String getTypeName() throws Exception {
        return GlobalMessageConstants.TEST_LISTENER;
    }

    @Override
    public String getCopyTag() throws Exception {
        return null;
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
        return "tl";
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return SEARCH_TAGS;
    }

    @Override
    public String getPropertyValue(String key) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }

        switch (key) {
            case "id":
                return entity.getIdForDisplay();
            case "name":
                return entity.getName();
            case "folder":
                return entity.getParentFolder().getIdForDisplay();
                
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Image getEntryImage() throws Exception {
        return ImageConstants.IMG_16_KEYWORD;
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
    }

}
