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
    private static final int RIGHT_MOUSE = 3;

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
                return (viewerCell instanceof ViewerCell && viewerCell.equals(focusCellHighlighter.getMarkedCell()));
            case ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION:
                EventObject source = event.sourceEvent;
                return (!(source instanceof MouseEvent) || ((MouseEvent) source).button != RIGHT_MOUSE);
            case ColumnViewerEditorActivationEvent.KEY_PRESSED:
                return event.keyCode == SWT.CR;
        }

        return false;
    }
}
