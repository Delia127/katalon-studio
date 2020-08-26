package com.kms.katalon.composer.components.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DialogUtil {

    public static Point computeCenterLocation(Point initialSize) {
        return computeCenterLocation(initialSize, null);
    }

    public static Point computeCenterLocation(Point initialSize, Shell parent) {
        Rectangle parentBounds = getParentBounds(parent);
        int left = (parentBounds.width - initialSize.x) / 2 + parentBounds.x;
        int top = (parentBounds.height - initialSize.y) / 2 + parentBounds.y;
        return new Point(left, top);
    }

    public static Point computeLeftLocation(Point initialSize) {
        return computeLeftLocation(initialSize, null);
    }

    public static Point computeLeftLocation(Point initialSize, Shell parent) {
        Rectangle parentBounds = getParentBounds(parent);
        int left = parentBounds.x;
        int top = (parentBounds.height - initialSize.y) / 2 + parentBounds.y;
        return new Point(left, top);
    }

    public static Point computeRightLocation(Point initialSize) {
        return computeRightLocation(initialSize, null);
    }

    public static Point computeRightLocation(Point initialSize, Shell parent) {
        Rectangle parentBounds = getParentBounds(parent);
        int left = parentBounds.x + (parentBounds.width - initialSize.x);
        int top = (parentBounds.height - initialSize.y) / 2 + parentBounds.y;
        return new Point(left, top);
    }

    public static Rectangle getParentBounds() {
        return getParentBounds(null);
    }

    public static Rectangle getParentBounds(Shell parentShell) {
        Rectangle parentBounds = parentShell != null
                ? parentShell.getBounds()
                : Display.getCurrent().getBounds();
        return parentBounds;
    }
}
