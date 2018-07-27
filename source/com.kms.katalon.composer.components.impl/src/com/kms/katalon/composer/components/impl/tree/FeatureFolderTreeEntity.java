package com.kms.katalon.composer.components.impl.tree;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.FeatureController;
import com.kms.katalon.entity.folder.FolderEntity;

public class FeatureFolderTreeEntity extends FolderTreeEntity {

    private static final long serialVersionUID = -758813767294617085L;

    public FeatureFolderTreeEntity(FolderEntity folder, ITreeEntity parentTreeEntity) {
        super(folder, parentTreeEntity);
    }
    
    @Override
    public Object[] getChildren() throws Exception {
        return FeatureController.getInstance()
                .getFeatures(getObject())
                .stream()
                .map(feature -> new FeatureTreeEntity(feature, this))
                .toArray();
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
    public void loadAllDescentdantEntities() throws Exception {
        // Do nothing
    }
    
    @Override
    public String getKeyWord() throws Exception {
        return "ft";
    }
    
    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_FOLDER;
    }

    @Override
    public FolderEntity getObject() throws Exception {
        return super.getObject();
    }

}
