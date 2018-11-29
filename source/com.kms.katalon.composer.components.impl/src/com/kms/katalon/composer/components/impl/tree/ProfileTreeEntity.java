package com.kms.katalon.composer.components.impl.tree;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ProfileTreeEntity extends AbstractTreeEntity {

    private static final long serialVersionUID = -117775481186850234L;
    
    private static final String[] SEARCH_TAGS = new String[] { "name", "folder" };

    private ExecutionProfileEntity variableCollection;
    
    public ProfileTreeEntity(ExecutionProfileEntity variableCollection, ITreeEntity parent) {
        super(variableCollection, parent);
        this.variableCollection = variableCollection;
    }
    
    @Override
    public ExecutionProfileEntity getObject() throws Exception {
        return variableCollection;
    }

    @Override
    public Object[] getChildren() throws Exception {
        return null;
    }

    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_PROFILE_ENTITY;
    }

    @Override
    public String getTypeName() throws Exception {
        return GlobalMessageConstants.FILE_NAME_GLOBAL_VARIABLE;
    }

    @Override
    public String getCopyTag() throws Exception {
        return FolderType.PROFILE.toString();
    }

    @Override
    public boolean hasChildren() throws Exception {
        return false;
    }

    @Override
    public boolean isRemoveable() throws Exception {
        return !getObject().isDefaultProfile();
    }

    @Override
    public boolean isRenamable() throws Exception {
        return !getObject().isDefaultProfile();
    }

    @Override
    public Transfer getEntityTransfer() throws Exception {
        return TreeEntityTransfer.getInstance();
    }

    @Override
    public String getKeyWord() throws Exception {
        return "gl";
    }

    @Override
    public String[] getSearchTags() throws Exception {
        return SEARCH_TAGS;
    }

    @Override
    public String getPropertyValue(String key) {
        if ("name".equals(key)) {
            return variableCollection.getName();
        }
        if ("folder".equals("key")) {
            return variableCollection.getParentFolder().getIdForDisplay();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void loadAllDescentdantEntities() throws Exception {
        // Do-nothing
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }
}
