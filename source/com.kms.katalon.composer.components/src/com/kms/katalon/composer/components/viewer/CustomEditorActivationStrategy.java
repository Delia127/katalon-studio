package com.kms.katalon.composer.components.viewer;

import java.util.EventObject;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

public class CustomEditorActivationStrategy extends ColumnViewerEditorActivationStrategy {
    /**
     * SWT MouseEvent constant for right ( third ) mouse button
     * 
     * @see {@link org.eclipse.swt.events.MouseEvent#button}
     */
    private static final int LEFT_MOUSE = 1;

    private FocusCellOwnerDrawHighlighterForMultiSelection focusCellHighlighter;

    public CustomEditorActivationStrategy(ColumnViewer viewer,
            FocusCellOwnerDrawHighlighterForMultiSelection focusCellHighlighter) {
        super(viewer);
        this.focusCellHighlighter = focusCellHighlighter;
    }

    @Override
    protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
        switch (event.eventType) {
            case ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION:
                Object viewerCell = event.getSource();
                return (viewerCell instanceof ViewerCell && viewerCell.equals(focusCellHighlighter.getMarkedCell()) && isLeftMouseClick(event.sourceEvent));
            case ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION:
                return isLeftMouseClick(event.sourceEvent);
            case ColumnViewerEditorActivationEvent.KEY_PRESSED:
                return event.keyCode == SWT.CR;
            case ColumnViewerEditorActivationEvent.PROGRAMMATIC:
                return true;
        }

        return false;
    }

    protected boolean isLeftMouseClick(EventObject eventObject) {
        return eventObject instanceof MouseEvent && ((MouseEvent) eventObject).button == LEFT_MOUSE;
    }
}
