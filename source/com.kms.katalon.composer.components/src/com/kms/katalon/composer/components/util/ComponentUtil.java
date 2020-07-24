package com.kms.katalon.composer.components.util;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.constants.ComponentConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.resources.image.ImageManager;

public class ComponentUtil {

    public interface EventHandler {
        void call(Event event);
    }

    public static Composite createGridLayout(Composite parent) {
        return createGridLayout(parent, 1);
    }

    public static Composite createGridLayout(Composite parent, int numCols) {
        return createGridLayout(parent, numCols, SWT.NONE);
    }

    public static Composite createGridLayout(Composite parent, int numCols, int style) {
        return createGridLayout(parent, numCols, false, style);
    }

    public static Composite createGridLayout(Composite parent, int numCols, boolean equalWidth, int style) {
        Composite grid = new Composite(parent, style);
        appendGridLayout(grid, numCols, equalWidth);
        StyleContext.style(grid);
        return grid;
    }

    public static Composite appendGridLayout(Composite grid) {
        return appendGridLayout(grid, 1, false);
    }

    public static Composite appendGridLayout(Composite grid, int numCols, boolean equalWidth) {
        GridLayout layout = new GridLayout(numCols, equalWidth);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        grid.setLayout(layout);
        return grid;
    }

    public static Composite createRowLayout(Composite parent) {
        return createRowLayout(parent, SWT.NONE, SWT.HORIZONTAL);
    }

    public static Composite createRowLayout(Composite parent, int style) {
        return createRowLayout(parent, style, SWT.HORIZONTAL);
    }

    public static Composite createRowLayout(Composite parent, int style, int type) {
        Composite row = new Composite(parent, style);
        RowLayout layout = new RowLayout(type);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.spacing = 0;
        layout.wrap = true;
        row.setLayout(layout);
        StyleContext.style(row);
        return row;
    }

    public static Label createLabel(Composite parent) {
        return createLabel(parent, StringUtils.EMPTY);
    }

    public static Label createLabel(Composite parent, String text) {
        return createLabel(parent, text, SWT.WRAP);
    }

    public static Label createLabel(Composite parent, int style) {
        return createLabel(parent, StringUtils.EMPTY, style);
    }

    public static Label createLabel(Composite parent, String text, int style) {
        Label label = new Label(parent, style | SWT.WRAP);
        label.setText(text);
        StyleContext.style(label);
        return label;
    }

    public static Text createText(Composite parent) {
        return createText(parent, StringUtils.EMPTY);
    }

    public static Text createText(Composite parent, String text) {
        return createText(parent, text, SWT.WRAP);
    }

    public static Text createText(Composite parent, int style) {
        return createText(parent, StringUtils.EMPTY, style);
    }

    public static Text createText(Composite parent, String text, int style) {
        Text label = new Text(parent, style | SWT.WRAP);
        label.setText(text);
        StyleContext.style(label);
        return label;
    }

    public static Button createButton(Composite parent) {
        return createButton(parent, StringUtils.EMPTY, SWT.NONE);
    }

    public static Button createButton(Composite parent, int style) {
        return createButton(parent, StringUtils.EMPTY, style);
    }

    public static Button createButton(Composite parent, String text, int style) {
        Button button = new Button(parent, style | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
        button.setText(text);
        StyleContext.style(button);
        return button;
    }

    public static Label createGridLabel(Composite parent, int colSpan) {
        return createGridLabel(parent, StringUtils.EMPTY, colSpan, 1);
    }

    public static Label createGridLabel(Composite parent, int colSpan, int rowSpan) {
        return createGridLabel(parent, StringUtils.EMPTY, colSpan, rowSpan);
    }

    public static Label createGridLabel(Composite parent, String text, int colSpan) {
        return createGridLabel(parent, text, colSpan, 1);
    }

    public static Label createGridLabel(Composite parent, String text, int colSpan, int rowSpan) {
        Label label = createLabel(parent, text);
        setGridLayoutData(label, colSpan, rowSpan);
        return label;
    }

    public static Link createLink(Composite parent) {
        return createLink(parent, StringUtils.EMPTY);
    }

    public static Link createLink(Composite parent, String text) {
        return createLink(parent, text, SWT.NONE);
    }

    public static Link createLink(Composite parent, int style) {
        return createLink(parent, StringUtils.EMPTY, style);
    }

    public static Link createLink(Composite parent, String text, int style) {
        Link link = new Link(parent, style | SWT.WRAP);
        link.setText(text);
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String linkTextContent = link.getText();
                Pattern pattern = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");
                Matcher matcher = pattern.matcher(linkTextContent);
                if (matcher.find()) {
                    String url = matcher.toMatchResult().group(2);
                    triggerLink(url);
                }
            }
        });
        StyleContext.style(link);
        return link;
    }

    public static void triggerLink(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }

        if (url.startsWith("mailto")) {
            try {
                Desktop.getDesktop().mail(URI.create(url));
            } catch (IOException exception) {
                LoggerSingleton.logError(exception);
            }
            return;
        }
        Program.launch(url);
    }

    public static Link createGridLink(Composite parent, int colSpan) {
        return createGridLink(parent, StringUtils.EMPTY, colSpan, 1);
    }

    public static Link createGridLink(Composite parent, int colSpan, int rowSpan) {
        return createGridLink(parent, StringUtils.EMPTY, colSpan, rowSpan);
    }

    public static Link createGridLink(Composite parent, String text, int colSpan) {
        return createGridLink(parent, text, colSpan, 1);
    }

    public static Link createGridLink(Composite parent, String text, int colSpan, int rowSpan) {
        Link link = createLink(parent, text);
        setGridLayoutData(link, colSpan, rowSpan);
        return link;
    }

    public static String wrapLink(String link) {
        return MessageFormat.format("<a href=\"{0}\">{0}</a>", link);
    }

    public static String wrapLink(String label, String link) {
        return MessageFormat.format("<a href=\"{0}\">{1}</a>", link, label);
    }

    public static Canvas createCanvasImage(Composite parent, String imageKey) {
        Image image = ImageManager.getImage(imageKey);
        return createCanvas(parent, image);
    }

    public static Canvas createCanvasImage(Composite parent, String imageKey, int maxHeight) {
        Image image = ImageManager.getImage(imageKey);
        int height = Math.min(maxHeight, image.getBounds().height);
        int width = (int) (((float) image.getBounds().width) * ((float) height) / ((float) image.getBounds().height));
        return createCanvas(parent, image, width, height);
    }

    public static Canvas createCanvasImage(Composite parent, String imageKey, int width, int height) {
        Image image = ImageManager.getImage(imageKey);
        return ComponentUtil.createCanvas(parent, image, width, height);
    }

    public static Canvas createCanvas(Composite parent, Image image) {
        return createCanvas(parent, image, image.getBounds().width, image.getBounds().height);
    }

    public static Canvas createCanvas(Composite parent, Image image, int width, int height) {
        Canvas canvas = new Canvas(parent, SWT.TRANSPARENT);
        canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                GC gc = event.gc;
                gc.setAntialias(SWT.ON);
                gc.setAdvanced(true);
                gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
            }
        });
        setSize(canvas, width, height);
        return canvas;
    }

    public static void setGridLayoutData(Control control, int colSpan, int rowSpan) {
        GridData gridData = new GridData();
        gridData.horizontalSpan = colSpan;
        gridData.verticalSpan = rowSpan;
        control.setLayoutData(gridData);
    }

    public static void setGridColSpan(Control control, int colSpan) {
        GridData gridData = new GridData();
        gridData.horizontalSpan = colSpan;
        control.setLayoutData(gridData);
    }

    public static void setGridRowSpan(Control control, int rowSpan) {
        GridData gridData = new GridData();
        gridData.verticalSpan = rowSpan;
        control.setLayoutData(gridData);
    }

    public static void setGridMarginTop(Composite container, int margin) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.marginTop = margin;
        container.setLayout(gridLayout);
    }

    public static void setGridMarginRight(Composite container, int margin) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.marginRight = margin;
        container.setLayout(gridLayout);
    }

    public static void setGridMarginBottom(Composite container, int margin) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.marginBottom = margin;
        container.setLayout(gridLayout);
    }

    public static void setGridMarginLeft(Composite container, int margin) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.marginLeft = margin;
        container.setLayout(gridLayout);
    }

    public static void setGridMarginX(Composite container, int width) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.marginWidth = width;
        container.setLayout(gridLayout);
    }

    public static void setGridMarginY(Composite container, int height) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.marginHeight = height;
        container.setLayout(gridLayout);
    }

    public static void setGridMargin(Composite container, int margin) {
        setGridMargin(container, margin, margin);
    }

    public static void setGridMargin(Composite container, int width, int height) {
        setGridMargin(container, height, width, height);
    }

    public static void setGridMargin(Composite container, int top, int width, int bottom) {
        setGridMargin(container, top, width, bottom, width);
    }

    public static void setGridMargin(Composite container, int top, int right, int bottom, int left) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.marginTop = top;
        gridLayout.marginRight = right;
        gridLayout.marginBottom = bottom;
        gridLayout.marginLeft = left;
        container.setLayout(gridLayout);
    }

    public static void setGridVerticalSpacing(Composite container, int verticalSpacing) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.verticalSpacing = verticalSpacing;
        container.setLayout(gridLayout);
    }

    public static void setGridHorizontalSpacing(Composite container, int horizontalSpacing) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.horizontalSpacing = horizontalSpacing;
        container.setLayout(gridLayout);
    }

    public static void setGridSpacing(Composite container, int spacing) {
        setGridSpacing(container, spacing, spacing);
    }

    public static void setGridSpacing(Composite container, int verticalSpacing, int horizontalSpacing) {
        GridLayout gridLayout = getGridLayout(container);
        gridLayout.verticalSpacing = verticalSpacing;
        gridLayout.horizontalSpacing = horizontalSpacing;
        container.setLayout(gridLayout);
    }

    public static void gridAlignCenter(Control control) {
        gridAlign(control, SWT.CENTER);
    }

    public static void gridAlignLeft(Control control) {
        gridAlign(control, SWT.LEFT);
    }

    public static void gridAlignRight(Control control) {
        gridAlign(control, SWT.RIGHT);
    }

    public static void gridAlign(Control control, int align) {
        GridData gridData = getGridData(control);
        gridData.horizontalAlignment = SWT.CENTER;
        gridData.grabExcessHorizontalSpace = true;
        control.setLayoutData(gridData);

        try {
            Method setAlignment = control.getClass().getMethod("setAlignment", int.class);
            if (setAlignment != null) {
                setAlignment.invoke(control, align);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
    }

    public static void gridAlignMiddle(Control control) {
        gridVerticalAlign(control, SWT.CENTER);
    }

    public static void gridVerticalAlign(Control control, int align) {
        Object layoutData = getLayoutData(control);
        if (layoutData instanceof GridData) {
            ((GridData) layoutData).verticalAlignment = align;
            ((GridData) layoutData).grabExcessVerticalSpace = true;
        }
        control.setLayoutData(layoutData);
    }

    public static void gridFill(Control control) {
        GridData gridData = getGridData(control);
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        control.setLayoutData(gridData);
    }

    public static void setWidth(Control control, int width) {
        Object layoutData = getLayoutData(control);
        if (layoutData instanceof GridData) {
            ((GridData) layoutData).widthHint = width;
        }
        if (layoutData instanceof RowData) {
            ((RowData) layoutData).width = width;
        }
        control.setLayoutData(layoutData);
    }

    public static void setHeight(Control control, int height) {
        Object layoutData = getLayoutData(control);
        if (layoutData instanceof GridData) {
            ((GridData) layoutData).heightHint = height;
        }
        if (layoutData instanceof RowData) {
            ((RowData) layoutData).height = height;
        }
        control.setLayoutData(layoutData);
    }

    public static void setSize(Control control, int size) {
        setSize(control, size, size);
    }

    public static void setSize(Control control, int width, int height) {
        setWidth(control, width);
        setHeight(control, height);
    }

    public static void setRowSpacing(Composite container, int spacing) {
        RowLayout rowLayout = getRowLayout(container);
        rowLayout.spacing = spacing;
        container.setLayout(rowLayout);
    }

    public static void setRowJustify(Composite container, boolean justify) {
        RowLayout rowLayout = getRowLayout(container);
        rowLayout.justify = justify;
        container.setLayout(rowLayout);
    }

    public static void fontSize(Control control, int fontSize) {
        Font currentFont = control.getFont() != null
                ? control.getFont()
                : StyleContext.getFont();
        control.setFont(FontUtil.size(currentFont, fontSize));
    }

    public static void fontStyle(Control control, int fontStyle) {
        Font currentFont = control.getFont() != null
                ? control.getFont()
                : StyleContext.getFont();
        control.setFont(FontUtil.style(currentFont, fontStyle));
    }

    public static void fontFamily(Control control, String fontFamily) {
        Font currentFont = control.getFont() != null
                ? control.getFont()
                : StyleContext.getFont();
        control.setFont(FontUtil.family(currentFont, fontFamily));
    }

    public static void setText(Control control, String text) {
        try {
            Method setText = control.getClass().getMethod("setText", String.class);
            if (setText != null) {
                setText.invoke(control, text);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
    }

    public static void setMessage(Control control, String message) {
        try {
            Method setMessage = control.getClass().getMethod("setMessage", String.class);
            if (setMessage != null) {
                setMessage.invoke(control, message);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
    }

    public static void setImage(Control control, Image image) {
        setImage(control, image, SWT.LEFT);
    }

    public static void setImage(Control control, Image image, int align) {
        try {
            Method setImage = control.getClass().getMethod("setImage", Image.class);
            if (setImage != null) {
                setImage.invoke(control, image);
            }
            control.setData(ComponentConstants.CONTROL_IMAGE_ALIGN, align);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
    }

    public static void setColor(Control control, Color color) {
        control.setForeground(color);
    }

    public static void setBackground(Control control, Color color) {
        control.setBackground(color);
    }

    public static void setHoverColor(Control control, Color color) {
        setData(control, ComponentConstants.CONTROL_HOVER_COLOR, color);
    }

    public static void setHoverBackground(Control control, Color color) {
        setData(control, ComponentConstants.CONTROL_HOVER_BACKGROUND, color);
    }

    public static void setActiveColor(Control control, Color color) {
        setData(control, ComponentConstants.CONTROL_ACTIVE_COLOR, color);
    }

    public static void setActiveBackground(Control control, Color color) {
        setData(control, ComponentConstants.CONTROL_ACTIVE_BACKGROUND, color);
    }

    public static void setBorder(Control control) {
        setBorder(control, ComponentConstants.DEFAULT_BORDER_WIDTH);
    }

    public static void setBorder(Control control, int borderWidth) {
        setBorderWidth(control, borderWidth);
    }

    public static void setBorder(Control control, Color color) {
        setBorderColor(control, color);
    }

    public static void setBorder(Control control, int borderWidth, Color color) {
        setBorderWidth(control, borderWidth);
        setBorderColor(control, color);
    }

    public static void setBorderWidth(Control control, int borderWidth) {
        setData(control, ComponentConstants.CONTROL_BORDER_WIDTH, borderWidth);
    }

    public static void setBorderColor(Control control, Color color) {
        setData(control, ComponentConstants.CONTROL_BORDER_COLOR, color);
    }

    public static void setBorderRadius(Control control) {
        setBorderRadius(control, ComponentConstants.DEFAULT_BORDER_RADIUS);
    }

    public static void setBorderRadius(Control control, int borderRadius) {
        setData(control, ComponentConstants.CONTROL_BORDER_RADIUS, borderRadius);
    }

    public static void setCursorPointer(Control control) {
        setCursor(control, SWT.CURSOR_HAND);
    }

    public static void setCursorDefault(Control control) {
        setCursor(control, SWT.DEFAULT);
    }

    public static void setCursor(Control control, int cursorType) {
        boolean isDefaultCursor = cursorType == SWT.DEFAULT || cursorType == SWT.NONE;
        control.setCursor(isDefaultCursor
                ? null
                : new Cursor(Display.getCurrent(), cursorType));
    }

    public static void setData(Control control, Object data) {
        control.setData(data);
    }

    public static void setData(Control control, String key, Object value) {
        control.setData(key, value);
    }

    public static void applyPrimaryButtonStyle(Control control) {
        setCursorPointer(control);
        setBorderRadius(control);
        setColor(control, ColorUtil.getTextWhiteColor());
        setBackground(control, ColorUtil.PRIMARY_COLOR);
        setHoverColor(control, ColorUtil.getTextWhiteColor());
        setHoverBackground(control, ColorUtil.PRIMARY_HOVER_COLOR);
        setActiveColor(control, ColorUtil.getTextWhiteColor());
        setActiveBackground(control, ColorUtil.PRIMARY_ACTIVE_COLOR);
    }

    public static void applyPrimaryBadgeStyle(Control control) {
        setBorderRadius(control);
        setColor(control, ColorUtil.getTextWhiteColor());
        setBackground(control, ColorUtil.PRIMARY_COLOR);
    }

    public static void applyGrayBadgeStyle(Control control) {
        setBorderRadius(control);
        setColor(control, ColorUtil.GRAY_BADGE_COLOR);
        setBackground(control, ColorUtil.GRAY_BADGE_BACKGROUND);
    }

    public static void appendGridChild(Composite container, Control child) {
        child.setParent(container);
        getGridLayout(container).numColumns += 1;
    }

    public static void onClick(Control control, EventHandler onClick) {
        if (control instanceof Button || control instanceof Link) {
            control.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    onClick.call(event);
                }
            });
            return;
        }
        control.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent event) {
                Rectangle bounds = control.getBounds();
                bounds.x = 0;
                bounds.y = 0;
                if (bounds.intersects(event.x, event.y, 1, 1)) {
                    onClick.call(null);
                }
            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
    }

    public static void onChange(Control control, EventHandler onChange) {
        control.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                onChange.call(event);
            }
        });
    }

    public static GridLayout getGridLayout(Composite container) {
        GridLayout gridLayout = (GridLayout) container.getLayout();
        return gridLayout != null
                ? gridLayout
                : new GridLayout();
    }

    public static GridData getGridData(Control control) {
        GridData gridData = (GridData) control.getLayoutData();
        return gridData != null
                ? gridData
                : new GridData(SWT.NONE);
    }

    public static Object getLayoutData(Control control) {
        Object layoutData = control.getLayoutData();
        if (layoutData == null) {
            Layout layout = control.getParent().getLayout();
            layoutData = layout instanceof GridLayout
                    ? new GridData(SWT.NONE)
                    : new RowData();
        }
        return layoutData;
    }

    public static boolean hasGridData(Control control) {
        return control.getLayoutData() instanceof GridData;
    }

    public static RowLayout getRowLayout(Composite container) {
        RowLayout rowLayout = (RowLayout) container.getLayout();
        return rowLayout != null
                ? rowLayout
                : new RowLayout(SWT.HORIZONTAL);
    }

    public static RowData getRowData(Control control) {
        RowData gridData = (RowData) control.getLayoutData();
        return gridData != null
                ? gridData
                : new RowData();
    }

    public static boolean hasRowData(Control control) {
        return control.getLayoutData() instanceof RowData;
    }

    public static boolean isDisposed(Control control) {
        return control == null || control.isDisposed();
    }
}
