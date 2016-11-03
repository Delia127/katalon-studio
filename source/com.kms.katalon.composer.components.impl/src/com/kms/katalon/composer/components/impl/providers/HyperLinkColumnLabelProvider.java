package com.kms.katalon.composer.components.impl.providers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

public abstract class HyperLinkColumnLabelProvider<T> extends TypeCheckedStyleCellLabelProvider<T> {

    private int charWidth;

    public HyperLinkColumnLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    public void initialize(ColumnViewer viewer, ViewerColumn column) {
        super.initialize(viewer, column);
        registerHyperLinkClickListener(viewer);
    }

    private void registerHyperLinkClickListener(ColumnViewer viewer) {
        Control table = getTable(viewer);

        final HyperLinkClickedListener mouseListener = new HyperLinkClickedListener();
        table.addMouseListener(mouseListener);

        final MoveMouseOnHyperLinkListener mouseMoveListener = new MoveMouseOnHyperLinkListener();
        table.addMouseMoveListener(mouseMoveListener);

        table.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Control control = (Control) e.getSource();
                control.removeMouseListener(mouseListener);
                control.removeMouseMoveListener(mouseMoveListener);
            }
        });
    }

    @Override
    protected void paint(Event event, Object element) {
        super.paint(event, element);
        charWidth = Math.max(1, event.gc.getFontMetrics().getAverageCharWidth());
    }

    @Override
    public void update(ViewerCell cell) {
        super.update(cell);

        cell.setStyleRanges(new StyleRange[] { getHyperLinkStyleRange(cell) });
    }

    private StyleRange getHyperLinkStyleRange(ViewerCell cell) {
        StyleRange hyperLinkStyle = new StyleRange();
        hyperLinkStyle.foreground = cell.getItem().getDisplay().getSystemColor(SWT.COLOR_BLUE);
        hyperLinkStyle.underline = true;
        hyperLinkStyle.start = 0;
        hyperLinkStyle.length = cell.getText().length();
        return hyperLinkStyle;
    }

    private final class HyperLinkClickedListener extends MouseAdapter {

        @Override
        public void mouseDown(MouseEvent e) {
            Point point = new Point(e.x, e.y);
            ViewerCell cell = getViewer().getCell(point);
            if (isPlacedHyperLinkCell(cell)) {
                Rectangle rect = cell.getTextBounds();
                rect.width = cell.getText().length() * charWidth;
                if (rect.contains(point)) {
                    handleMouseDown(e, cell);
                }
            }
        }

    }

    protected abstract void handleMouseDown(MouseEvent e, ViewerCell cell);

    private final class MoveMouseOnHyperLinkListener implements MouseMoveListener {
        @Override
        public void mouseMove(MouseEvent e) {
            ColumnViewer viewer = getViewer();
            ViewerCell cell = viewer.getCell(new Point(e.x, e.y));
            Control table = getTable(viewer);
            if (!isPlacedHyperLinkCell(cell)) {
                table.setCursor(null);
                return;
            }

            if (table.getCursor() == null) {
                table.setCursor(newCursor());
            }
        }

        private Cursor newCursor() {
            return new Cursor(getTable(getViewer()).getDisplay(), SWT.CURSOR_HAND);
        }
    }

    private Control getTable(ColumnViewer viewer) {
        return viewer.getControl();
    }

    private boolean isPlacedHyperLinkCell(ViewerCell cell) {
        return cell != null && cell.getColumnIndex() == columnIndex && StringUtils.isNotEmpty(cell.getText());
    }
}
