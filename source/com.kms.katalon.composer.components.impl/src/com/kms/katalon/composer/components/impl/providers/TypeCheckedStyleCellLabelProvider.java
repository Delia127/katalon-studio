package com.kms.katalon.composer.components.impl.providers;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public abstract class TypeCheckedStyleCellLabelProvider<T> extends StyledCellLabelProvider {

    private static final int SPACE = 5;

    protected int columnIndex;

    private TextLayout cachedTextLayout;

    private boolean customPaint;

    private int deltaOfLastMeasure;

    /**
     * Create new instance of {@link TypeCheckedStyleCellLabelProvider} with default <code>customPaint = true</code>
     */
    public TypeCheckedStyleCellLabelProvider(final int columnIndex) {
        this(columnIndex, true);
    }

    /**
     * @param customPaint true if children want to use {@link #paint(Event, Object)}. Otherwise, uses
     * {@link StyledCellLabelProvider#paint(Event, Object)}
     */
    public TypeCheckedStyleCellLabelProvider(final int columnIndex, final boolean customPaint) {
        this.columnIndex = columnIndex;
        this.customPaint = customPaint;
    }

    protected abstract Class<T> getElementType();

    private boolean isElementInstanceOf(Object element) {
        Class<?> clazz = getElementType();
        return clazz != null && clazz.isInstance(element);
    }

    @Override
    protected void paint(Event event, Object element) {
        if (canNotDrawSafely(element)) {
            super.paint(event, element);
            return;
        }

        ViewerCell cell = getOwnedViewerCell(event);

        if (isCellNotExisted(cell)) {
            return;
        }

        
        GC gc = event.gc;
        boolean applyColors = useColors(event);

        Color oldForeground = gc.getForeground();
        Color oldBackground = gc.getBackground();

        if (applyColors) {
            drawCellColor(cell, gc);
        }

        drawCellTextAndImage(event, cell, gc);

        if (canDrawFocus(event)) {
            drawCellFocus(cell, gc);
        }

        if (applyColors) {
            gc.setForeground(oldForeground);
            gc.setBackground(oldBackground);
        }
    }

    protected int getSpace() {
        return SPACE;
    }
    
    protected int getLeftMargin() {
        return SPACE;
    }
    
    protected int getRightMargin() {
        return SPACE;
    }

    protected boolean canNotDrawSafely(Object element) {
        return !customPaint || !isElementInstanceOf(element);
    }

    private void drawCellColor(ViewerCell cell, GC gc) {
        Color foreground = cell.getForeground();
        if (foreground != null) {
            gc.setForeground(foreground);
        }

        Color background = cell.getBackground();
        if (background != null) {
            gc.setBackground(background);
        }
    }

    private void drawCellFocus(ViewerCell cell, GC gc) {
        Rectangle focusBounds = getTextBounds(cell.getViewerRow().getBounds());
        gc.drawFocus(focusBounds.x, focusBounds.y, focusBounds.width + deltaOfLastMeasure + getRightMargin(),
                focusBounds.height);
    }

    private boolean canDrawFocus(Event event) {
        return (event.detail & SWT.FOCUSED) != 0;
    }

    protected void drawCellTextAndImage(Event event, ViewerCell cell, GC gc) {
        Image image = cell.getImage();
        int startX = getLeftMargin();
        if (image != null) {
            gc.drawImage(image, event.getBounds().x + startX, event.getBounds().y);
            startX = getSpace();
        }

        Rectangle textBounds = getTextBounds(cell.getTextBounds());
        if (textBounds != null) {
            TextLayout textLayout = getSharedTextLayout(event.display);

            Rectangle layoutBounds = textLayout.getBounds();
            int y = textBounds.y + Math.max(0, (textBounds.height - layoutBounds.height) / 2);

            Rectangle saveClipping = gc.getClipping();
            gc.setClipping(textBounds);
            textLayout.draw(gc, textBounds.x + startX, y);
            gc.setClipping(saveClipping);
        }
    }

    protected Rectangle getTextBounds(Rectangle originalBounds) {
        return originalBounds;
    }

    protected ViewerCell getOwnedViewerCell(Event event) {
        return getViewer().getCell(new Point(event.x, event.y));
    }

    /**
     * @see StyledCellLabelProvider#mesure(Event, Object)
     */
    @Override
    protected void measure(Event event, Object element) {
        if (canNotDrawSafely(element)) {
            super.measure(event, element);
            return;
        }

        ViewerCell cell = getOwnedViewerCell(event);

        if (isCellNotExisted(cell)) {
            return;
        }

        boolean applyColors = useColors(event);

        TextLayout layout = getSharedTextLayout(event.display);

        int textWidthDelta = deltaOfLastMeasure = updateTextLayout(layout, cell, applyColors);

        event.width += textWidthDelta + getRightMargin();
    }

    private boolean isCellNotExisted(ViewerCell cell) {
        return cell == null || cell.getViewerRow() == null;
    }

    /**
     * @see StyledCellLabelProvider#userColors(Event)
     */
    protected boolean useColors(Event event) {
        return (event.detail & SWT.SELECTED) == 0;
    }

    /**
     * @see StyledCellLabelProvider#updateTextLayout(TextLayout, ViewerCell, boolean)
     */
    private int updateTextLayout(TextLayout layout, ViewerCell cell, boolean applyColors) {
        layout.setStyle(null, 0, Integer.MAX_VALUE); // clear old styles

        layout.setText(cell.getText());
        layout.setFont(cell.getFont()); // set also if null to clear previous usages

        int originalTextWidth = getTextBounds(layout.getBounds()).width; // text width without any styles
        boolean containsOtherFont = false;

        StyleRange[] styleRanges = cell.getStyleRanges();
        if (styleRanges != null) { // user didn't fill styled ranges
            for (int i = 0; i < styleRanges.length; i++) {
                StyleRange curr = prepareStyleRange(styleRanges[i], applyColors);
                layout.setStyle(curr, curr.start, curr.start + curr.length - 1);
                if (curr.font != null) {
                    containsOtherFont = true;
                }
            }
        }
        int textWidthDelta = 0;
        if (containsOtherFont) {
            textWidthDelta = getTextBounds(layout.getBounds()).width - originalTextWidth;
        }
        return textWidthDelta;
    }

    /**
     * @see StyledCellLabelProvider#getSharedTextLayout(Display)
     */
    protected TextLayout getSharedTextLayout(Display display) {
        if (cachedTextLayout == null) {
            int orientation = getViewer().getControl().getStyle() & (SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT);
            cachedTextLayout = new TextLayout(display);
            cachedTextLayout.setOrientation(orientation);
        }
        return cachedTextLayout;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(ViewerCell cell) {
        T element = (T) cell.getElement();
        cell.setText(getText(element));
        cell.setImage(getImage(element));
        cell.setBackground(getBackground(cell.getBackground(), element));
        cell.setForeground(getForeground(cell.getForeground(), element));
        cell.setStyleRanges(getStyleRanges(cell, element));
        super.update(cell);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getToolTipText(Object element) {
        if (isElementInstanceOf(element)) {
            return getElementToolTipText((T) element);
        }
        return super.getToolTipText(element);
    }

    protected String getElementToolTipText(T element) {
        return "";
    }

    protected Color getBackground(Color background, T element) {
        return background;
    }

    protected Color getForeground(Color foreground, T element) {
        return foreground;
    }

    protected abstract Image getImage(T element);

    protected abstract String getText(T element);
    
    protected StyleRange[] getStyleRanges(ViewerCell cell, T element) {
        return null;
    }
}
