package com.kms.katalon.composer.explorer.integration;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.tree.ITreeEntity;

public interface IntegrationLabelDecorator {
    Image getOverlayImage(ITreeEntity treeEntity);

    int getPreferredOrder();
}
