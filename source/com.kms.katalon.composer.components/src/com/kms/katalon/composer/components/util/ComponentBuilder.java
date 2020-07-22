package com.kms.katalon.composer.components.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ComponentUtil.EventHandler;

public class ComponentBuilder<T extends Control> {

    private Integer marginTop;

    private Integer marginRight;

    private Integer marginBottom;

    private Integer marginLeft;

    private T control;

    public ComponentBuilder(T control) {
        this.control = control;
    }

    public static ComponentBuilder<Label> label(Composite parent) {
        return new ComponentBuilder<Label>(ComponentUtil.createLabel(parent));
    }

    public static ComponentBuilder<Label> label(Composite parent, int style) {
        return new ComponentBuilder<Label>(ComponentUtil.createLabel(parent, style));
    }

    public static ComponentBuilder<Link> link(Composite parent) {
        return new ComponentBuilder<Link>(ComponentUtil.createLink(parent));
    }

    public static ComponentBuilder<Link> link(Composite parent, int style) {
        return new ComponentBuilder<Link>(ComponentUtil.createLink(parent, style));
    }

    public static ComponentBuilder<Text> text(Composite parent) {
        return new ComponentBuilder<Text>(ComponentUtil.createText(parent));
    }

    public static ComponentBuilder<Text> text(Composite parent, int style) {
        return new ComponentBuilder<Text>(ComponentUtil.createText(parent, style));
    }

    public static ComponentBuilder<Button> button(Composite parent) {
        return new ComponentBuilder<Button>(ComponentUtil.createButton(parent));
    }

    public static ComponentBuilder<Button> button(Composite parent, int style) {
        return new ComponentBuilder<Button>(ComponentUtil.createButton(parent, style));
    }

    public static ComponentBuilder<Canvas> image(Composite parent, String imageKey) {
        Canvas canvas = ComponentUtil.createCanvasImage(parent, imageKey);
        return new ComponentBuilder<Canvas>(canvas);
    }

    public static ComponentBuilder<Canvas> image(Composite parent, String imageKey, int maxHeight) {
        Canvas canvas = ComponentUtil.createCanvasImage(parent, imageKey, maxHeight);
        return new ComponentBuilder<Canvas>(canvas);
    }

    public static ComponentBuilder<Canvas> image(Composite parent, String imageKey, int width, int height) {
        Canvas canvas = ComponentUtil.createCanvasImage(parent, imageKey, width, height);
        return new ComponentBuilder<Canvas>(canvas);
    }

    public static ComponentBuilder<Canvas> canvas(Composite parent, Image image) {
        return new ComponentBuilder<Canvas>(ComponentUtil.createCanvas(parent, image));
    }

    public static ComponentBuilder<Canvas> canvas(Composite parent, Image image, int width, int height) {
        return new ComponentBuilder<Canvas>(ComponentUtil.createCanvas(parent, image, width, height));
    }

    public static ComponentBuilder<Composite> gridContainer(Composite parent) {
        return new ComponentBuilder<Composite>(ComponentUtil.createGridLayout(parent));
    }

    public static ComponentBuilder<Composite> gridContainer(Composite parent, int numCols) {
        return new ComponentBuilder<Composite>(ComponentUtil.createGridLayout(parent, numCols));
    }

    public static ComponentBuilder<Composite> gridContainer(Composite parent, int numCols, int style) {
        return new ComponentBuilder<Composite>(ComponentUtil.createGridLayout(parent, numCols, style));
    }

    public static ComponentBuilder<Composite> fromGrid(Composite control) {
        ComponentUtil.appendGridLayout((Composite) control);
        StyleContext.style(control);
        return new ComponentBuilder<Composite>(control);
    }

    public static ComponentBuilder<Composite> rowContainer(Composite parent) {
        return new ComponentBuilder<Composite>(ComponentUtil.createRowLayout(parent));
    }

    public static ComponentBuilder<Composite> rowContainer(Composite parent, int style) {
        return new ComponentBuilder<Composite>(ComponentUtil.createRowLayout(parent, style));
    }

    public static ComponentBuilder<Composite> rowContainer(Composite parent, int style, int type) {
        return new ComponentBuilder<Composite>(ComponentUtil.createRowLayout(parent, style, type));
    }

    public ComponentBuilder<T> gridMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public ComponentBuilder<T> gridMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public ComponentBuilder<T> gridMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public ComponentBuilder<T> gridMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public ComponentBuilder<T> gridMarginX(int marginWidth) {
        this.marginRight = marginWidth;
        this.marginLeft = marginWidth;
        return this;
    }

    public ComponentBuilder<T> gridMarginY(int marginHeight) {
        this.marginTop = marginHeight;
        this.marginBottom = marginHeight;
        return this;
    }

    public ComponentBuilder<T> gridMargin(int margin) {
        gridMargin(margin, margin);
        return this;
    }

    public ComponentBuilder<T> gridMargin(int marginHeight, int marginWidth) {
        gridMargin(marginHeight, marginWidth, marginHeight);
        return this;
    }

    public ComponentBuilder<T> gridMargin(int marginTop, int marginWidth, int marginBottom) {
        gridMargin(marginTop, marginWidth, marginBottom, marginWidth);
        return this;
    }

    public ComponentBuilder<T> gridMargin(int marginTop, int marginRight, int marginBottom, int marginLeft) {
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
        return this;
    }

    private void gridMargin() {
        boolean isMarginSet = marginTop != null || marginRight != null || marginBottom != null || marginLeft != null;
        if (!isMarginSet) {
            return;
        }

        Composite targetContainer = null;
        if (control instanceof Composite) {
            targetContainer = (Composite) control;
        } else {
            targetContainer = ComponentUtil.createGridLayout(control.getParent());
            targetContainer.setLayout(ObjectUtil.clone(control.getParent().getLayout()));

            ComponentUtil.setGridMargin(targetContainer, 0);
            GridData controlLayoutData = ComponentUtil.getGridData(control);
            int colSpan = controlLayoutData.horizontalSpan;
            int rowSpan = controlLayoutData.verticalSpan;
            targetContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, colSpan, rowSpan));

            control.setParent(targetContainer);
            controlLayoutData.horizontalAlignment = SWT.CENTER;
        }
        if (marginTop != null) {
            ComponentUtil.setGridMarginTop(targetContainer, marginTop);
        }
        if (marginRight != null) {
            ComponentUtil.setGridMarginRight(targetContainer, marginRight);
        }
        if (marginBottom != null) {
            ComponentUtil.setGridMarginBottom(targetContainer, marginBottom);
        }
        if (marginLeft != null) {
            ComponentUtil.setGridMarginLeft(targetContainer, marginLeft);
        }
    }

    public ComponentBuilder<T> gridVerticalSpacing(int verticalSpacing) {
        if (control instanceof Composite) {
            ComponentUtil.setGridVerticalSpacing((Composite) control, verticalSpacing);
        }
        return this;
    }

    public ComponentBuilder<T> gridHorizontalSpacing(int horizontalSpacing) {
        if (control instanceof Composite) {
            ComponentUtil.setGridHorizontalSpacing((Composite) control, horizontalSpacing);
        }
        return this;
    }

    public ComponentBuilder<T> gridSpacing(int spacing) {
        if (control instanceof Composite) {
            ComponentUtil.setGridSpacing((Composite) control, spacing);
        }
        return this;
    }

    public ComponentBuilder<T> gridSpacing(int verticalSpacing, int horizontalSpacing) {
        if (control instanceof Composite) {
            ComponentUtil.setGridSpacing((Composite) control, verticalSpacing, horizontalSpacing);
        }
        return this;
    }

    public ComponentBuilder<T> rowSpacing(int spacing) {
        if (control instanceof Composite) {
            ComponentUtil.setRowSpacing((Composite) control, spacing);
        }
        return this;
    }

    public ComponentBuilder<T> rowJustify() {
        return rowJustify(true);
    }

    public ComponentBuilder<T> rowJustify(boolean justify) {
        if (control instanceof Composite && ((Composite) control).getLayout() instanceof RowLayout) {
            ComponentUtil.setRowJustify((Composite) control, justify);
        }
        return this;
    }

    public ComponentBuilder<T> fill() {
        ComponentUtil.gridFill(control);
        return this;
    }

    public ComponentBuilder<T> center() {
        ComponentUtil.gridAlignCenter(control);
        return this;
    }

    public ComponentBuilder<T> left() {
        ComponentUtil.gridAlignLeft(control);
        return this;
    }

    public ComponentBuilder<T> right() {
        ComponentUtil.gridAlignRight(control);
        return this;
    }

    public ComponentBuilder<T> align(int align) {
        ComponentUtil.gridAlign(control, align);
        return this;
    }

    public ComponentBuilder<T> middle() {
        ComponentUtil.gridAlignMiddle(control);
        return this;
    }

    public ComponentBuilder<T> verticalAlign(int align) {
        ComponentUtil.gridVerticalAlign(control, align);
        return this;
    }

    public ComponentBuilder<T> font(Font font) {
        control.setFont(font);
        return this;
    }

    public ComponentBuilder<T> fontSize(int fontSize) {
        ComponentUtil.fontSize(control, fontSize);
        return this;
    }

    public ComponentBuilder<T> normal() {
        return fontStyle(FontUtil.STYLE_NORMAL);
    }

    public ComponentBuilder<T> bold() {
        return fontStyle(FontUtil.STYLE_BOLD);
    }

    public ComponentBuilder<T> italic() {
        return fontStyle(FontUtil.STYLE_ITALIC);
    }

    public ComponentBuilder<T> boldItalic() {
        return fontStyle(FontUtil.STYLE_BOLD_ITALIC);
    }

    public ComponentBuilder<T> fontStyle(int fontStyle) {
        ComponentUtil.fontStyle(control, fontStyle);
        return this;
    }

    public ComponentBuilder<T> fontFamily(String fontFamily) {
        ComponentUtil.fontFamily(control, fontFamily);
        return this;
    }

    public ComponentBuilder<T> colSpan(int colSpan) {
        ComponentUtil.setGridColSpan(control, colSpan);
        return this;
    }

    public ComponentBuilder<T> rowSpan(int rowSpan) {
        ComponentUtil.setGridRowSpan(control, rowSpan);
        return this;
    }

    public ComponentBuilder<T> width(int width) {
        ComponentUtil.setWidth(control, width);
        return this;
    }

    public ComponentBuilder<T> height(int height) {
        ComponentUtil.setHeight(control, height);
        return this;
    }

    public ComponentBuilder<T> size(int width, int height) {
        ComponentUtil.setSize(control, width, height);
        return this;
    }

    public ComponentBuilder<T> size(int size) {
        ComponentUtil.setSize(control, size);
        return this;
    }

    public ComponentBuilder<T> text(String text) {
        ComponentUtil.setText(control, text);
        return this;
    }

    public ComponentBuilder<T> message(String message) {
        ComponentUtil.setMessage(control, message);
        return this;
    }

    public ComponentBuilder<T> image(Image image) {
        ComponentUtil.setImage(control, image);
        return this;
    }

    public ComponentBuilder<T> image(Image image, int align) {
        ComponentUtil.setImage(control, image, align);
        return this;
    }

    public ComponentBuilder<T> color(Color color) {
        ComponentUtil.setColor(control, color);
        return this;
    }

    public ComponentBuilder<T> background(Color color) {
        ComponentUtil.setBackground(control, color);
        return this;
    }

    public ComponentBuilder<T> hoverColor(Color color) {
        ComponentUtil.setHoverColor(control, color);
        return this;
    }

    public ComponentBuilder<T> hoverBackground(Color color) {
        ComponentUtil.setHoverBackground(control, color);
        return this;
    }

    public ComponentBuilder<T> activeColor(Color color) {
        ComponentUtil.setActiveColor(control, color);
        return this;
    }

    public ComponentBuilder<T> activeBackground(Color color) {
        ComponentUtil.setActiveBackground(control, color);
        return this;
    }

    public ComponentBuilder<T> border() {
        ComponentUtil.setBorder(control);
        return this;
    }

    public ComponentBuilder<T> border(int width) {
        ComponentUtil.setBorder(control, width);
        return this;
    }

    public ComponentBuilder<T> border(Color color) {
        ComponentUtil.setBorder(control, color);
        return this;
    }

    public ComponentBuilder<T> border(int width, Color color) {
        ComponentUtil.setBorder(control, width, color);
        return this;
    }

    public ComponentBuilder<T> borderWidth(int width) {
        ComponentUtil.setBorderWidth(control, width);
        return this;
    }

    public ComponentBuilder<T> borderColor(Color color) {
        ComponentUtil.setBorderColor(control, color);
        return this;
    }

    public ComponentBuilder<T> rounded() {
        return borderRadius();
    }

    public ComponentBuilder<T> borderRadius() {
        ComponentUtil.setBorderRadius(control);
        return this;
    }

    public ComponentBuilder<T> borderRadius(int borderRadius) {
        ComponentUtil.setBorderRadius(control, borderRadius);
        return this;
    }

    public ComponentBuilder<T> primaryButton() {
        ComponentUtil.applyPrimaryButtonStyle(control);
        return this;
    }

    public ComponentBuilder<T> primaryBadge() {
        ComponentUtil.applyPrimaryBadgeStyle(control);
        return this;
    }

    public ComponentBuilder<T> grayBadge() {
        ComponentUtil.applyGrayBadgeStyle(control);
        return this;
    }

    public ComponentBuilder<T> cursorPointer() {
        ComponentUtil.setCursorPointer(control);
        return this;
    }

    public ComponentBuilder<T> cursorDefault() {
        ComponentUtil.setCursorDefault(control);
        return this;
    }

    public ComponentBuilder<T> cursor(int cursorType) {
        ComponentUtil.setCursor(control, cursorType);
        return this;
    }

    public ComponentBuilder<T> data(Object data) {
        ComponentUtil.setData(control, data);
        return this;
    }

    public ComponentBuilder<T> data(String key, Object value) {
        ComponentUtil.setData(control, key, value);
        return this;
    }

    public ComponentBuilder<T> customRender() {
        StyleUtil.customRender(control);
        return this;
    }

    public ComponentBuilder<T> onClick(EventHandler onClick) {
        ComponentUtil.onClick(control, onClick);
        return this;
    }

    public ComponentBuilder<T> onChange(EventHandler onChange) {
        ComponentUtil.onChange(control, onChange);
        return this;
    }

    public ComponentBuilder<T> mouseTrack(MouseTrackListener listener) {
        control.addMouseTrackListener(listener);
        return this;
    }

    public T build() {
        gridMargin();
        if (ComponentDataUtil.shouldUseCustomRender(control)) {
            customRender();
        }
        return control;
    }
}
