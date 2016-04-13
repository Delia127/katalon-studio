package com.kms.katalon.composer.components.util;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;

import com.kms.katalon.composer.components.viewer.CustomEditorActivationStrategy;
import com.kms.katalon.composer.components.viewer.FocusCellOwnerDrawHighlighterForMultiSelection;

public class ColumnViewerUtil {
    private static final int ACTIVATION_BIT_MASK = ColumnViewerEditor.TABBING_HORIZONTAL
            | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.KEYBOARD_ACTIVATION;


    /**
     * Set the activation strategy for the table cells to be double click or enter key
     * 
     * @param tableViewer
     */
    public static void setTableActivation(TableViewer tableViewer) {
        FocusCellOwnerDrawHighlighterForMultiSelection focusCellHighlighter = getFocusCellHighlighter(tableViewer);
        TableViewerEditor.create(tableViewer, new TableViewerFocusCellManager(tableViewer, focusCellHighlighter),
                getColumnViewerActivationStrategy(tableViewer, focusCellHighlighter), ACTIVATION_BIT_MASK);
    }

    /**
     * Set the activation strategy for the tree table cells to be double click or enter key
     * 
     * @param treeViewer
     */
    public static void setTreeTableActivation(TreeViewer treeViewer) {
        FocusCellOwnerDrawHighlighterForMultiSelection focusCellHighlighter = getFocusCellHighlighter(treeViewer);
        TreeViewerEditor.create(treeViewer, new TreeViewerFocusCellManager(treeViewer,
                focusCellHighlighter),
                getColumnViewerActivationStrategy(treeViewer, focusCellHighlighter), ACTIVATION_BIT_MASK);
    }

    private static ColumnViewerEditorActivationStrategy getColumnViewerActivationStrategy(
            ColumnViewer columnViewer, FocusCellOwnerDrawHighlighterForMultiSelection focusCellHighlighter) {
        return new CustomEditorActivationStrategy(columnViewer, focusCellHighlighter);
    }
    
    private static FocusCellOwnerDrawHighlighterForMultiSelection getFocusCellHighlighter(ColumnViewer columnViewer) {
        return new FocusCellOwnerDrawHighlighterForMultiSelection(columnViewer);
    }
}
