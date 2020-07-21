package com.kms.katalon.composer.quickstart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.ComponentDataUtil;
import com.kms.katalon.composer.components.util.ComponentUtil;
import com.kms.katalon.composer.components.util.StyleContext;
import com.kms.katalon.composer.components.util.StyleUtil;
import com.kms.katalon.composer.components.util.ComponentUtil.EventHandler;

public class LinkBox extends Composite {

    public LinkBox(Composite parent, String text, String link, String imageKey) {
        super(parent, SWT.NONE);
        createLinkBox(this, text, link, imageKey);
    }

    private void createLinkBox(Composite box, String text, String link, String imageKey) {
        StyleContext.setBackground(null);
        box.setBackgroundMode(SWT.INHERIT_FORCE);

        EventHandler clickHandler = (event) -> {
            ComponentUtil.triggerLink(link);
        };

        ComponentBuilder.fromGrid(box)
                .size(120)
                .onClick(clickHandler)
                .background(ColorUtil.GRAY_LIGHT_COLOR)
                .hoverBackground(ColorUtil.GRAY_COLOR)
                .activeBackground(ColorUtil.GRAY_DARK_COLOR)
                .borderRadius(8)
                .build();
        box.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
        MouseTrackListener mouseTracker = ComponentDataUtil.getMouseTrackListener(box);

        Composite boxInner = ComponentBuilder.gridContainer(box)
                .gridMargin(10)
                .center()
                .middle()
                .onClick(clickHandler)
                .mouseTrack(mouseTracker)
                .build();

        ComponentBuilder.image(boxInner, imageKey).center().onClick(clickHandler).mouseTrack(mouseTracker).build();

        ComponentBuilder.label(boxInner).text(text).center().onClick(clickHandler).mouseTrack(mouseTracker).build();

        StyleUtil.updateChildrenBackground(box, ColorUtil.GRAY_LIGHT_COLOR);
        StyleUtil.applyMoueseListenerToChildren(box);

        StyleContext.prevBackground();
    }
}
