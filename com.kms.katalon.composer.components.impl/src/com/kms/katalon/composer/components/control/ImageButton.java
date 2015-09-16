package com.kms.katalon.composer.components.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ImageButton extends Composite {
    private Color textColor;
    private Image image;
    private String text;
    private int width;
    private int height;

    public ImageButton(Composite parent, int style) {
        super(parent, style);
        Display display = parent.getDisplay();
        textColor = display.getSystemColor(SWT.COLOR_WHITE);
        text = "";
        /* Add dispose listener for the image */
        addListener(SWT.Dispose, new Listener() {
            @Override
            public void handleEvent(Event arg0) {
                if (image != null)
                    image.dispose();
            }
        });

        /* Add custom paint listener that paints the stars */
        addListener(SWT.Paint, new Listener() {
            @Override
            public void handleEvent(Event e) {
                paintControl(e);
            }
        });
    }

    private void paintControl(Event event) {
        GC gc = event.gc;

        if (image != null) {
            gc.drawImage(image, 1, 1);
            Point textSize = gc.textExtent(text);
            gc.setForeground(textColor);
            gc.drawText(text, (width - textSize.x) / 2 + 1, (height - textSize.y) / 2 + 1, true);
        }
    }

    public void setImage(Image image) {
        this.image = new Image(Display.getDefault(), image, SWT.IMAGE_COPY);
        width = image.getBounds().width;
        height = image.getBounds().height;
        redraw();
    }

    public void setText(String text) {
        this.text = text;
        redraw();
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        int overallWidth = width;
        int overallHeight = height;

        /* Consider hints */
        if (wHint != SWT.DEFAULT && wHint < overallWidth)
            overallWidth = wHint;

        if (hHint != SWT.DEFAULT && hHint < overallHeight)
            overallHeight = hHint;

        /* Return computed dimensions plus border */
        return new Point(overallWidth + 2, overallHeight + 2);
    }
}