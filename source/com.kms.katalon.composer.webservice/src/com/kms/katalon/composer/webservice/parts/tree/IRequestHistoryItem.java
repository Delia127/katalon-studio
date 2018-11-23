package com.kms.katalon.composer.webservice.parts.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;

public interface IRequestHistoryItem {
    String getName();

    Image getImage();

    List<IRequestHistoryItem> getChildren();

    boolean hasChildren();
    
    default IRequestHistoryItem getParent() {
        return null;
    }
}
