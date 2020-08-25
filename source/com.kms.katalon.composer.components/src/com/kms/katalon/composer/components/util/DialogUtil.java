package com.kms.katalon.composer.components.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DialogUtil {

    public static Point computeCenterLocation(Point initialSize) {
        Rectangle parentBounds = getParentBounds();
        int left = (parentBounds.width - initialSize.x) / 2 + parentBounds.x;
        int top = (parentBounds.height - initialSize.y) / 2 + parentBounds.y;
        return new Point(left, top);
    }

    public static Point computeLeftLocation(Point initialSize) {
        Rectangle parentBounds = getParentBounds();
        int left = parentBounds.x;
        int top = (parentBounds.height - initialSize.y) / 2 + parentBounds.y;
        return new Point(left, top);
    }

    public static Point computeRightLocation(Point initialSize) {
        Rectangle parentBounds = getParentBounds();
        int left = parentBounds.x + (parentBounds.width - initialSize.x);
        int top = (parentBounds.height - initialSize.y) / 2 + parentBounds.y;
        return new Point(left, top);
    }

    public static Rectangle getParentBounds() {
        return getParentBounds(Display.getCurrent().getActiveShell());
    }

    public static Rectangle getParentBounds(Shell parentShell) {
        Rectangle parentBounds = parentShell != null
                ? parentShell.getBounds()
                : Display.getCurrent().getBounds();
        return parentBounds;
    }
}
