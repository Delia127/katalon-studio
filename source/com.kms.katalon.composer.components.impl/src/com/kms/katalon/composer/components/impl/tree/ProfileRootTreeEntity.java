package com.kms.katalon.composer.components.impl.tree;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;

public class ProfileRootTreeEntity extends FolderTreeEntity {

    public static String SEARCH_TAG = "gl";

    private static final long serialVersionUID = 4270815411681851205L;

    public ProfileRootTreeEntity(FolderEntity folder, ITreeEntity parentTreeEntity) {
        super(folder, parentTreeEntity);
    }

    @Override
    public Object[] getChildren() throws Exception {
        return GlobalVariableController.getInstance()
                .getAllGlobalVariableCollections(ProjectController.getInstance().getCurrentProject()).stream()
                .map(p -> new ProfileTreeEntity(p, this))
                .toArray();
    }

    @Override
    public String getText() throws Exception {
        return getObject().getName();
    }

    @Override
    public boolean hasChildren() throws Exception {
        return true;
    }

    @Override
    public boolean isRemoveable() throws Exception {
        return false;
    }

    @Override
    public boolean isRenamable() throws Exception {
        return false;
    }
    
    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_PROFILE_FOLDER_ENTITY;
    }
    
    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }
}
