package com.kms.katalon.composer.components.impl.tree;

import com.kms.katalon.entity.folder.FolderEntity;

public class IncludeTreeRootEntity extends FolderTreeEntity {
    private static final long serialVersionUID = 9122449535591295663L;

    public IncludeTreeRootEntity(FolderEntity folder) {
        super(folder, null);
    }

    @Override
    public Object[] getChildren() throws Exception {
        return super.getChildren();
    }
    
    @Override
    public boolean isRemoveable() throws Exception {
        return false;
    }

    @Override
    public boolean isRenamable() throws Exception {
        return false;
    }
}
