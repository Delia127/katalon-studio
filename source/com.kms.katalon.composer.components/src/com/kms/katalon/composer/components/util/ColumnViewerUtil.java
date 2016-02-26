package com.kms.katalon.composer.components.util;

import java.util.EventObject;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import com.kms.katalon.composer.components.viewer.FocusCellOwnerDrawHighlighterForMultiSelection;

public class ColumnViewerUtil {
    /**
     * Set the activation strategy for the table cells to be double click or enter key
     * @param tableViewer
     */
    public static void setTableActivation(TableViewer tableViewer) {
        TableViewerEditor.create(tableViewer, new TableViewerFocusCellManager(tableViewer,
                new FocusCellOwnerDrawHighlighterForMultiSelection(tableViewer)), getColumViewerActivationStrategy(tableViewer),
                ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | ColumnViewerEditor.KEYBOARD_ACTIVATION);
    }

    private static ColumnViewerEditorActivationStrategy getColumViewerActivationStrategy(ColumnViewer columnViewer) {
        ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(columnViewer) {
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
                    EventObject source = event.sourceEvent;
                    if (source instanceof MouseEvent && ((MouseEvent) source).button == 3)
                        return false;

                    return true;
                } else if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR) {
                    return true;
                }
                return false;
            }
        };
        return activationSupport;
    }
    
    /**
     * Set the activation strategy for the tree table cells to be double click or enter key
     * @param treeTable
     */
    public static void setTreeTableActivation(TreeViewer treeTable) {
        TreeViewerEditor.create(treeTable, new TreeViewerFocusCellManager(treeTable,
                new FocusCellOwnerDrawHighlighterForMultiSelection(treeTable)), getColumViewerActivationStrategy(treeTable),
                ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | ColumnViewerEditor.KEYBOARD_ACTIVATION);
    }
}
