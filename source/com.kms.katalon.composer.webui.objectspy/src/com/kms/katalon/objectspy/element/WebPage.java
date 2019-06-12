package com.kms.katalon.objectspy.element;

import com.kms.katalon.entity.folder.FolderEntity;

public class WebPage extends WebFrame {

    public WebPage(String name) {
        super(name, WebElementType.PAGE);
    }
    
    private FolderEntity folderAlias;

    @Override
    public String getTag() {
        return super.getTag();
    }

    @Override
    public void setTag(String tag) {
        super.setTag(tag);
    }

    @Override
    public boolean hasProperty() {
        return false;
    }

    @Override
    public WebPage softClone() {
        WebPage clone = new WebPage(getName());
        getChildren().stream().forEach(child -> {
            child.softClone().setParent(clone);
        });
        return clone;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebPage)) {
            return false;
        }

        return super.equals(object);
    }

    public FolderEntity getFolderAlias() {
        return folderAlias;
    }

    public void setFolderAlias(FolderEntity folderAlias) {
        this.folderAlias = folderAlias;
    }

    @Override
    public String getScriptId() {
        return folderAlias != null ? folderAlias.getIdForDisplay() : getName();
    }
}
