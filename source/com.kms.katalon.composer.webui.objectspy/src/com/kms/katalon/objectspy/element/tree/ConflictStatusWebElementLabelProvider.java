package com.kms.katalon.objectspy.element.tree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.ConflictWebElementWrapper;

public class ConflictStatusWebElementLabelProvider extends CellLabelProvider {

    private WebElementLabelProvider webElementProvider = new WebElementLabelProvider();

    @Override
    public void update(ViewerCell cell) {
        String name = StringConstants.EMPTY;
        if (cell.getElement() != null) {
            if (cell.getElement() instanceof ConflictWebElementWrapper) {
                name = ((ConflictWebElementWrapper) cell.getElement()).getOriginalWebElement().getName();
            } 
        
        }
        setColor(cell);
        cell.setText(name);
        cell.setImage(getImage(cell.getElement()));
    }

    private void setColor(ViewerCell cell) {
        Object element = cell.getElement();
        if (element == null || !(element instanceof ConflictWebElementWrapper)) {
            return;
        }

        ConflictWebElementWrapper htmlElement = (ConflictWebElementWrapper) element;

        if (htmlElement.isConflicted()) {
            cell.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }
    }

    private Image getImage(Object element) {
        if (!(element instanceof ConflictWebElementWrapper)) {
            return null;
        }
        ConflictWebElementWrapper htmlElement = (ConflictWebElementWrapper) element;

        Image baseImage = webElementProvider.getImage(htmlElement.getOriginalWebElement());
        Image overlayImage = htmlElement.isConflicted() ? ImageConstants.IMG_16_CONFLICT_ELEMENT_STATUS
                : ImageConstants.IMG_16_NEW_ELEMENT_STATUS;
        return combineTwoImages(baseImage, overlayImage);
    }

    private Image combineTwoImages(Image baseImage, Image overlayImage) {

        DecorationOverlayIcon decoratedImage = new DecorationOverlayIcon(overlayImage,
                new ImageDescriptor[] { null, null, null, null, ImageDescriptor.createFromImage(baseImage) },
                new Point(baseImage.getBounds().width, baseImage.getBounds().height));

        return decoratedImage.createImage();
    }

}
