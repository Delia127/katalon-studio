package com.kms.katalon.composer.components;

import org.eclipse.e4.ui.workbench.renderers.swt.CTabRendering;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.ImageConstants;

@SuppressWarnings("restriction")
public class CCTabRendering extends CTabRendering {

    static final int BUTTON_BORDER = SWT.COLOR_WIDGET_DARK_SHADOW;

    static final int BUTTON_FILL = SWT.COLOR_LIST_BACKGROUND;

    public CCTabRendering(CTabFolder parent) {
        super(parent);
    }

    @Override
    protected void draw(int part, int state, Rectangle bounds, GC gc) {
//        switch (part) {
//            case PART_MAX_BUTTON:
//                cDrawMaximize(gc, bounds);
//                break;
//            case PART_MIN_BUTTON:
//                cDrawMinimize(gc, bounds);
//                break;
//            default:
//                super.draw(part, state, bounds, gc);
//                break;
//        }
        super.draw(part, state, bounds, gc);

        // Fix the light grey background color at top-right CTabFolder (min max button)
        Control[] children = parent.getChildren();
        if (children.length < 3) {
            return;
        }
        if (children[2] instanceof ToolBar) {
            Color toolBarBackgroundColor = ColorUtil.getToolBarBackgroundColor();
            children[0].setBackground(toolBarBackgroundColor);
            children[1].setBackground(toolBarBackgroundColor);
            children[2].setBackground(toolBarBackgroundColor);
        }
    }

    void cDrawMaximize(GC gc, Rectangle maxRect) {
        if (maxRect.width == 0 || maxRect.height == 0) {
            return;
        }
        gc.setForeground(ColorUtil.getToolBarForegroundColor());
        gc.fillRectangle(maxRect.x, maxRect.y, maxRect.width, maxRect.height);
        if (!parent.getMaximized()) {
            gc.drawImage(ImageConstants.IMG_MAXIMIZE, maxRect.x, maxRect.y);
        } else {
            gc.drawImage(ImageConstants.IMG_RESTORE, maxRect.x, maxRect.y);
        }
    }

    void cDrawMinimize(GC gc, Rectangle minRect) {
        if (minRect.width == 0 || minRect.height == 0) {
            return;
        }
        gc.setBackground(ColorUtil.getToolBarBackgroundColor());
        gc.fillRectangle(minRect.x, minRect.y, minRect.width, minRect.height);
        if (!parent.getMinimized()) {
            gc.drawImage(ImageConstants.IMG_MINIMIZE, minRect.x, minRect.y);
        } else {
            gc.drawImage(ImageConstants.IMG_RESTORE, minRect.x, minRect.y);
        }
    }

}
