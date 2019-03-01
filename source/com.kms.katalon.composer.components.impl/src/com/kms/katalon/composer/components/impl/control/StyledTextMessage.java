package com.kms.katalon.composer.components.impl.control;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class StyledTextMessage {

    private static final int MARGIN = 2;

    private static int[] INVALIDATE_EVENTS = { SWT.Activate, SWT.Deactivate, SWT.Show, SWT.Hide };

    private final StyledText styledText;

    private final Listener invalidateListener;

    private final Color textColor;

    private String message;

    private int verticalOffsetLines;

    public StyledTextMessage(StyledText styledText) {
        this.styledText = Objects.requireNonNull(styledText);
        this.invalidateListener = this::handleInvalidatedEvent;
        this.textColor = getTextColor();
        this.message = "";
        initialize();
    }

    public void setMessage(String message) {
        this.message = Objects.requireNonNull(message);
        this.styledText.redraw();
    }

    public boolean isMessageShowing() {
        return !message.isEmpty() && styledText.getContent().getCharCount() == 0;
    }

    private void initialize() {
        styledText.addListener(SWT.Paint, this::handlePaintEvent);
        styledText.addListener(SWT.Resize, event -> styledText.redraw());
        styledText.addListener(SWT.Dispose, this::handleDispose);
        for (int eventType : INVALIDATE_EVENTS) {
            styledText.getDisplay().addFilter(eventType, invalidateListener);
        }
    }

    private Color getTextColor() {
        return styledText.getDisplay().getSystemColor(SWT.COLOR_GRAY);
    }

    private void handlePaintEvent(Event event) {
        if (isMessageShowing()) {
            drawHint(event.gc, event.x, event.y);
        }
    }

    private void handleDispose(Event event) {
        for (int eventType : INVALIDATE_EVENTS) {
            styledText.getDisplay().removeFilter(eventType, invalidateListener);
        }
    }

    private void handleInvalidatedEvent(Event event) {
        styledText.redraw();
    }

    private void drawHint(GC gc, int x, int y) {
        int verticalOffset = verticalOffsetLines * gc.getFontMetrics().getHeight();
        gc.setForeground(textColor);
        gc.drawText(message, x + MARGIN, y - verticalOffset, SWT.DRAW_DELIMITER | SWT.DRAW_TRANSPARENT);
    }

}
