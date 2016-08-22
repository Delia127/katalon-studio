package com.kms.katalon.composer.components.impl.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ResizableBackgroundImageComposite extends FocusableComposite implements Listener {

    private Image originalImage;

    public ResizableBackgroundImageComposite(Composite parent, int style, Image originalImage) {
        super(parent, style);
        this.originalImage = originalImage;
        this.addListener(SWT.Resize, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.type == SWT.Resize) {
            changeImage();
        }
    }

    private void changeImage() {
        disposeCurrentImage();
        if (originalImage != null) {
            Rectangle rect = getClientArea();
            setBackgroundImage(resize(originalImage, rect.width, rect.height));
        }
    }

    private Image resize(Image image, int width, int height) {
        Image scaled = new Image(getDisplay(), width, height);
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
        gc.dispose();
        return scaled;
    }

    @Override
    public void dispose() {
        disposeCurrentImage();
        super.dispose();
    }

    private void disposeCurrentImage() {
        Image oldImage = getBackgroundImage();
        if (oldImage != null) {
            oldImage.dispose();
        }
    }
}
