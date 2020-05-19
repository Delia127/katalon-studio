package com.kms.katalon.composer.testcase.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractKeywordNodeTooltip {

    private int preferedWidth = 600;

    private int preferedHeight = 200;

    protected Shell tip;

    protected Control control;

    private boolean showBelow = true;

    protected Point location;

    private static AbstractKeywordNodeTooltip currentTooltip = null;

    public Shell getShell() {
        return tip;
    }

    protected abstract void initComponents(Composite parent);

    private void createTooltip() {
        tip = new Shell(control.getShell(), SWT.ON_TOP | SWT.TOOL | SWT.RESIZE);
        tip.setLayout(new FillLayout());
        initComponents(tip);
    }

    public boolean isShowBelowPoint() {
        return showBelow;
    }

    public void setPreferedSize(int w, int h) {
        if (w > 0) {
            preferedWidth = w;
        }
        if (h > 0) {
            preferedHeight = h;
        }
    }

    public void show(Point p) {
        hide();
        location = p;
        createTooltip();
        tip.setLocation(p);

        Point tipSize = getBestSizeForKeywordDescriptionPopup();
        tip.setSize(tipSize);

        if (currentTooltip != null && currentTooltip != this) {
            currentTooltip.hide();
        }
        currentTooltip = this;
        tip.setVisible(true);
    }

    protected Point getBestSizeForKeywordDescriptionPopup() {
        Monitor currentMonitor = null;
        for (Monitor monitor : Display.getCurrent().getMonitors()) {
            if (monitor.getClientArea().contains(location)) {
                currentMonitor = monitor;
                break;
            }
        }
        Rectangle displayRect = currentMonitor.getClientArea();
        int width = preferedWidth;
        if (location.x + width > displayRect.x + displayRect.width) {
            width = displayRect.x + displayRect.width - location.x;
        }
        return new Point(width, preferedHeight);
    }
    
    public synchronized void hide() {    
        if (tip != null && !tip.isDisposed()) {
                tip.dispose();
            currentTooltip = null;
        }
    }

    public boolean isVisible() {
        return tip != null && !tip.isDisposed() && tip.isVisible();
    }

    public Rectangle getBounds() {
        return tip.getBounds();
    }
}
