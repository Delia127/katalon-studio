package com.kms.katalon.composer.components.viewer;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class CustomTreeViewerFocusCellManager extends TreeViewerFocusCellManager {

    private FocusCellOwnerDrawHighlighterForMultiSelection cellHighlighter;

    private TreeViewer viewer;

    private int maxColumnIndex = 0;

    public CustomTreeViewerFocusCellManager(TreeViewer viewer,
            FocusCellOwnerDrawHighlighterForMultiSelection cellHighlighter) {
        super(viewer, cellHighlighter);
        this.cellHighlighter = cellHighlighter;
        this.viewer = viewer;
        maxColumnIndex = viewer.getTree().getColumnCount() - 1;
    }

    public void focusCell(ViewerCell focusCell) {
        if (focusCell == null) {
            return;
        }

        // focusCell.scrollIntoView(); does not work

        // Workaround
        try {
            Method m = Class.forName("org.eclipse.jface.viewers.SWTFocusCellManager").getDeclaredMethod("setFocusCell",
                    ViewerCell.class);
            boolean access = m.isAccessible();
            if (!access) {
                m.setAccessible(true);
            }
            m.invoke(this, focusCell);
            m.setAccessible(access);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public void focusNextCell() {
        ViewerCell focusCell = cellHighlighter.getFocusCell();
        if (focusCell == null) {
            return;
        }
        int cellIndex = focusCell.getColumnIndex();
        ViewerRow row = focusCell.getViewerRow();
        if (row == null) {
            return;
        }

        boolean isNextRow = cellIndex >= maxColumnIndex;
        int nextCellIndex = isNextRow ? maxColumnIndex - cellIndex : cellIndex + 1;
        if (isNextRow) {
            // get next row
            ViewerRow nextRow = row.getNeighbor(ViewerRow.BELOW, false);
            if (nextRow == null) {
                nextRow = row.getNeighbor(ViewerRow.BELOW, true);
            }
            if (nextRow == null) {
                return;
            }
            row = nextRow;

            // focus to next row
            viewer.getTree().setSelection((TreeItem) row.getItem());
        }

        ViewerCell nextCell = row.getCell(nextCellIndex);
        if (nextCell == null) {
            return;
        }

        focusCell(nextCell);
    }

    public void focusPreviousCell() {
        ViewerCell focusCell = cellHighlighter.getFocusCell();
        if (focusCell == null) {
            return;
        }
        int cellIndex = focusCell.getColumnIndex();
        ViewerRow row = focusCell.getViewerRow();
        if (row == null) {
            return;
        }

        boolean isPreviousRow = cellIndex - 1 < 0;
        int previousCellIndex = isPreviousRow ? maxColumnIndex : cellIndex - 1;
        if (isPreviousRow) {
            // get previous row
            ViewerRow previousRow = row.getNeighbor(ViewerRow.ABOVE, false);
            if (previousRow == null) {
                previousRow = row.getNeighbor(ViewerRow.ABOVE, true);
            }
            if (previousRow == null) {
                return;
            }
            row = previousRow;

            // focus to previous row
            viewer.getTree().setSelection((TreeItem) row.getItem());
        }

        ViewerCell previousCell = row.getCell(previousCellIndex);
        if (previousCell == null) {
            return;
        }

        focusCell(previousCell);
    }

}
