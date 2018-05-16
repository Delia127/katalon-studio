package com.kms.katalon.composer.components.impl.tree;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.TestListenerController;
import com.kms.katalon.entity.folder.FolderEntity;

public class TestListenerFolderTreeEntity extends FolderTreeEntity {

    private static final long serialVersionUID = 1701272959941191597L;

    public TestListenerFolderTreeEntity(FolderEntity folder, ITreeEntity parentTreeEntity) {
        super(folder, parentTreeEntity);
    }

    @Override
    public Object[] getChildren() throws Exception {
        return TestListenerController.getInstance()
                .getTestListeners(getObject())
                .stream()
                .map(listener -> new TestListenerTreeEntity(listener, this))
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
        return "tl";
    }
    
    @Override
    public Image getImage() throws Exception {
        return ImageConstants.IMG_16_FOLDER;
    }

    @Override
    public FolderEntity getObject() throws Exception {
        return super.getObject();
    }

    @Override
    public Image getEntryImage() throws Exception {
        return getImage();
    }
}
