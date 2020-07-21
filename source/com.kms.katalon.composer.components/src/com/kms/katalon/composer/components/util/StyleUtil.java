package com.kms.katalon.composer.components.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.constants.ComponentConstants;

public class StyleUtil {

    private static final int TEXT_IMAGE_OFFSET = 3;

    public static void customRender(Control control) {
        ComponentDataUtil.set(control, ComponentConstants.CONTROL_PROP_IMAGE, ComponentDataUtil.getImage(control));
        ComponentDataUtil.set(control, ComponentConstants.CONTROL_PROP_BACKGROUND, control.getBackground());
        ComponentDataUtil.set(control, ComponentConstants.CONTROL_PROP_COLOR, control.getForeground());

        ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_BACKGROUND, control.getBackground());
        ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_COLOR, control.getForeground());

        control.setBackground(null);
        ComponentDataUtil.setImage(control, null);

        control.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent event) {
                GC gc = event.gc;
                gc.setAntialias(SWT.ON);
                gc.setAdvanced(true);
                gc.setFont(FontUtil.size(control.getFont(), control.getFont().getFontData()[0].getHeight() - 1));
                Color background = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_STATE_BACKGROUND);
                if (background != null) {
                    gc.setBackground(background);
                }
                Color color = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_STATE_COLOR);
                if (color != null) {
                    gc.setForeground(color);
                }

                // ---

                Rectangle bounds = control.getBounds();

                int borderRadius = ComponentDataUtil.getInt(control, ComponentConstants.CONTROL_BORDER_RADIUS);
                Image image = ComponentDataUtil.getImage(control, ComponentConstants.CONTROL_PROP_IMAGE);
                String textContent = ComponentDataUtil.getText(control);

                Point textSize = gc.textExtent(StringUtils.defaultString(textContent));
                Point contentSize = new Point(textSize.x, textSize.y);

                Rectangle imageBounds = new Rectangle(0, 0, 0, 0);
                if (image != null) {
                    imageBounds = image.getBounds();
                    contentSize.x += imageBounds.width + TEXT_IMAGE_OFFSET;
                    contentSize.y = Math.max(contentSize.y, imageBounds.height);
                }

                if (bounds.width < contentSize.x) {
                    control.setSize(contentSize.x + 5, bounds.height);
                    bounds = control.getBounds();
                }

                int width = bounds.width;
                int height = bounds.height;

                // ---

                // Draw rounded background
                gc.fillRoundRectangle(0, 0, width, height, borderRadius, borderRadius);

                // Draw text & image

                Point anchor = new Point((width - contentSize.x) / 2, (height - contentSize.y) / 2);
                Point textAnchor = new Point(anchor.x, anchor.y + Math.max((imageBounds.height - textSize.y) / 2, 0));
                if (image != null) {
                    Integer imageAlign = (Integer) control.getData(ComponentConstants.CONTROL_IMAGE_ALIGN);
                    Point imgAnchor = new Point(anchor.x,
                            anchor.y + Math.max((textSize.y - imageBounds.height) / 2, 0));
                    if (imageAlign == SWT.RIGHT) {
                        imgAnchor.x += textSize.x + TEXT_IMAGE_OFFSET;
                    }

                    gc.drawImage(image, 0, 0, imageBounds.width, imageBounds.height, imgAnchor.x, imgAnchor.y,
                            imageBounds.width, imageBounds.height);

                    if (imageAlign != SWT.RIGHT) {
                        textAnchor.x += imageBounds.width + TEXT_IMAGE_OFFSET;
                    }
                }

                if (StringUtils.isNotBlank(textContent)) {
                    gc.drawText(textContent, textAnchor.x, textAnchor.y, true);
                }

                // Draw border
                if (Display.getCurrent().getCursorControl() != control) {
                    if (ComponentDataUtil.has(control, ComponentConstants.CONTROL_BORDER_WIDTH)
                            || ComponentDataUtil.has(control, ComponentConstants.CONTROL_BORDER_COLOR)) {
                        Color borderColor = ComponentDataUtil.getColor(control,
                                ComponentConstants.CONTROL_BORDER_COLOR);
                        if (borderColor != null) {
                            gc.setForeground(borderColor);
                        } else {
                            gc.setForeground(ColorUtil.getColor(ComponentConstants.DEFAULT_BORDER_COLOR));
                        }
                        int borderWidth = ComponentDataUtil.get(control, ComponentConstants.CONTROL_BORDER_WIDTH, 1);
                        gc.setLineWidth(borderWidth);
                        gc.drawRoundRectangle(0, 0, width - borderWidth, height - borderWidth, borderRadius,
                                borderRadius);
                    }
                }
            }
        });

        if (ComponentDataUtil.shouldTrackMouseMove(control)) {
            MouseTrackListener mouseTrackListener = new MouseTrackListener() {

                @Override
                public void mouseHover(MouseEvent e) {
                }

                @Override
                public void mouseExit(MouseEvent e) {
                    setStyleFromProp(control);
                }

                @Override
                public void mouseEnter(MouseEvent e) {
                    setStyleFromHover(control);
                }
            };
            control.addMouseTrackListener(mouseTrackListener);
        }

        if (ComponentDataUtil.shouldTrackMouseAction(control)) {
            MouseListener mouseListener = new MouseListener() {

                @Override
                public void mouseUp(MouseEvent e) {
                    setStyleFromProp(control);
                }

                @Override
                public void mouseDown(MouseEvent e) {
                    setStyleFromActive(control);
                }

                @Override
                public void mouseDoubleClick(MouseEvent e) {
                }
            };
            control.addMouseListener(mouseListener);
            control.setData("mouseListener", mouseListener);
        }
    }

    private static void setStyleFromProp(Control control) {
        Color background = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_PROP_BACKGROUND);
        Color color = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_PROP_COLOR);
        ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_BACKGROUND, background);
        ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_COLOR, color);
        updateChildrenBackground(control, background);
        updateChildrenColor(control, color);
        control.redraw();
    }

    private static void setStyleFromHover(Control control) {
        if (ComponentDataUtil.has(control, ComponentConstants.CONTROL_HOVER_BACKGROUND)) {
            Color background = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_HOVER_BACKGROUND);
            ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_BACKGROUND, background);
            updateChildrenBackground(control, background);
        }
        if (ComponentDataUtil.has(control, ComponentConstants.CONTROL_HOVER_COLOR)) {
            Color color = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_HOVER_COLOR);
            ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_COLOR, color);
            updateChildrenColor(control, color);
        }
        control.redraw();
    }

    private static void setStyleFromActive(Control control) {
        if (ComponentDataUtil.has(control, ComponentConstants.CONTROL_ACTIVE_BACKGROUND)) {
            Color background = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_ACTIVE_BACKGROUND);
            ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_BACKGROUND, background);
            updateChildrenBackground(control, background);
        }
        if (ComponentDataUtil.has(control, ComponentConstants.CONTROL_ACTIVE_COLOR)) {
            Color color = ComponentDataUtil.getColor(control, ComponentConstants.CONTROL_ACTIVE_COLOR);
            ComponentDataUtil.set(control, ComponentConstants.CONTROL_STATE_COLOR, color);
            updateChildrenColor(control, color);
        }
        control.redraw();
    }

    public static void updateChildrenBackground(Control control, Color background) {
        if (control instanceof Composite) {
            Control[] children = ((Composite) control).getChildren();
            for (Control child : children) {
                child.setBackground(background);
            }
        }
    }

    private static void updateChildrenColor(Control control, Color color) {
        if (control instanceof Composite) {
            Control[] children = ((Composite) control).getChildren();
            for (Control child : children) {
                child.setForeground(color);
            }
        }
    }

    public static void applyMoueseListenerToChildren(Control control) {
        applyMoueseListenerToChildren(control, null);
    }

    private static void applyMoueseListenerToChildren(Control control, MouseListener mouseListener) {
        if (mouseListener == null) {
            mouseListener = (MouseListener) control.getData("mouseListener");
            if (mouseListener == null) {
                return;
            }
        }
        if (!(control instanceof Composite)) {
            return;
        }

        Control[] children = ((Composite) control).getChildren();
        for (Control child : children) {
            child.addMouseListener(mouseListener);
            applyMoueseListenerToChildren(child, mouseListener);
        }
    }
}
