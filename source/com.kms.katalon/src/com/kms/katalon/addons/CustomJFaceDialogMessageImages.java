package com.kms.katalon.addons;

import javax.annotation.PostConstruct;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;

public class CustomJFaceDialogMessageImages {
    ImageRegistry imageRegistry = JFaceResources.getImageRegistry();

    @PostConstruct
    public void postConstruct() {
        replaceImage(Dialog.DLG_IMG_MESSAGE_INFO, ImageConstants.IMG_20_INFO_MSG);
        replaceImage(Dialog.DLG_IMG_MESSAGE_WARNING, ImageConstants.IMG_20_WARNING_MSG);
        replaceImage(Dialog.DLG_IMG_MESSAGE_ERROR, ImageConstants.IMG_20_ERROR_MSG);
    }

    private void replaceImage(String key, Image replacement) {
        imageRegistry.remove(key);
        imageRegistry.put(key, replacement);
    }
}
