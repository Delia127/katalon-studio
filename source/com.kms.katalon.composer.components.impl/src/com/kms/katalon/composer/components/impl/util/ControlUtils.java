package com.kms.katalon.composer.components.impl.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class ControlUtils {
    private ControlUtils() {
        // Disable default constructor.
    }

    public static final int DF_CONTROL_HEIGHT = 18;
    public static final int DF_VERTICAL_SPACING = 10;
    public static final int DF_HORIZONTAL_SPACING = 10;

    public static void recursiveSetEnabled(Control ctrl, boolean enabled) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                recursiveSetEnabled(c, enabled);
            }
        } else {
            ctrl.setEnabled(enabled);
        }
    }

    public static void setFontToBeBold(Control ctrl) {
        ctrl.setFont(JFaceResources.getFontRegistry().getBold(""));
    }

    public static void setFontSize(Control ctrl, int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Font's size must be a positive number");
        }
        FontData[] fD = ctrl.getFont().getFontData();
        fD[0].setHeight(height);
        ctrl.setFont(new Font(ctrl.getDisplay(), fD));
    }

    public static Listener getAutoHideScrollbarListener = new Listener() {
        @Override
        public void handleEvent(final Event event) {
            final  Text t = (Text) event.widget;
            final Rectangle r1 = t.getClientArea();
            final Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
            final Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            t.getDisplay().timerExec(50, new Runnable() {
                @Override
                public void run() {
                    t.getHorizontalBar().setVisible(r2.width <= p.x);
                    t.getVerticalBar().setVisible(r2.height <= p.y);
                    if (event.type == SWT.Modify) {
                        t.getParent().layout(true);
                        t.showSelection();
                    }
                }
            });
        }
    };
    
    public static Listener getAutoHideStyledTextScrollbarListener = new Listener() {
        @Override
        public void handleEvent(final Event event) {
            final StyledText t = (StyledText) event.widget;
            final Rectangle r1 = t.getClientArea();
            final Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
            final Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            t.getDisplay().timerExec(50, new Runnable() {
                @Override
                public void run() {
                    t.getHorizontalBar().setVisible(r2.width <= p.x);
                    t.getVerticalBar().setVisible(r2.height <= p.y);
                    if (event.type == SWT.Modify) {
                        t.getParent().layout(true);
                        t.showSelection();
                    }
                }
            });
        }
    };
}
