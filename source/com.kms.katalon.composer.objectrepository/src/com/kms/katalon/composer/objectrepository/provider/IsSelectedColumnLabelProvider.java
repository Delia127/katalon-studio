package com.kms.katalon.composer.objectrepository.provider;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.objectrepository.constant.ImageConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class IsSelectedColumnLabelProvider extends OwnerDrawLabelProvider {
    @Override
    protected void paint(Event event, Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            Image checkboxImage;
            if (((WebElementPropertyEntity) element).getIsSelected()) {
                checkboxImage = ImageConstants.IMG_16_CHECKBOX_CHECKED;
            } else {
                checkboxImage = ImageConstants.IMG_16_CHECKBOX_UNCHECKED;
            }
            event.gc.drawImage(checkboxImage, event.getBounds().x + 5, event.getBounds().y);
        }

    }

    @Override
    protected void measure(Event event, Object element) {
        // TODO Auto-generated method stub
    }
}
