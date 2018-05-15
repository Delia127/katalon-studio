package com.kms.katalon.composer.intro;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class FunctionsIntroductionDialogSizeCalculator {
    private Shell currentShell;

    private double ratio;

    private Point expectedSize;

    public FunctionsIntroductionDialogSizeCalculator(Shell currentShell, Point expectedSize, double ratio) {
        this.currentShell = currentShell;
        this.ratio = ratio;
        this.expectedSize = expectedSize;
    }

    private Monitor getCurrentMonitor() {
        Point location = currentShell.getLocation();
        Monitor[] monitors = currentShell.getDisplay().getMonitors();
        for (Monitor monitor : monitors) {
            if (monitor.getClientArea().contains(location)) {
                return monitor;
            }
        }
        return monitors.length > 0 ? monitors[0] : null;
    }

    public Point getBestSize() {
        Monitor currentMonitor = getCurrentMonitor();
        if (currentMonitor == null) {
            return null;
        }
        Rectangle monitorClientArea = currentMonitor.getClientArea();
        return new Point(Math.min(expectedSize.x, (int) (monitorClientArea.width * ratio)), Math.min(expectedSize.y,
                (int) (monitorClientArea.height * ratio)));
    }

    public void computeDialogSize(Composite fixedComposite) {
        Point shellSize = currentShell.getSize();
        Point fixedCompositeOldSize = fixedComposite.getSize();
        int remainX = shellSize.x - fixedCompositeOldSize.x;
        int remainY = shellSize.y - fixedCompositeOldSize.y;
        fixedComposite.setSize(getBestSize());

        Point fixedCompositeNewize = fixedComposite.getSize();
        currentShell.setSize(remainX + fixedCompositeNewize.x, fixedCompositeNewize.y + remainY);
    }
}
