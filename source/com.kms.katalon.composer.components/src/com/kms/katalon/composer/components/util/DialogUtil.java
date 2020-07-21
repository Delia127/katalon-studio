package com.kms.katalon.composer.components.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DialogUtil {

    public static Point computeCenterLocation(Point initialSize) {
        Shell parentShell = Display.getCurrent().getActiveShell();
        Rectangle parentSize = parentShell != null
                ? parentShell.getBounds()
                : Display.getCurrent().getBounds();
        int centerX = (parentSize.width - initialSize.x) / 2 + parentSize.x;
        int centerY = (parentSize.height - initialSize.y) / 2 + parentSize.y;
        return new Point(centerX, centerY);
    }
}
