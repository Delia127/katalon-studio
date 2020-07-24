package com.kms.katalon.composer.quickstart;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.ComponentUtil;
import com.kms.katalon.composer.components.util.DialogUtil;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.composer.components.util.StyleContext;
import com.kms.katalon.constants.MessageConstants;

public class BaseQuickStartDialog extends AbstractDialog {

    interface SelectionCallback {
        void call(Object option);
    }

    protected Composite tipsComposite;

    public BaseQuickStartDialog(Shell parentShell) {
        super(parentShell, false);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setBackgroundMode(SWT.INHERIT_DEFAULT);
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        return DialogUtil.computeCenterLocation(initialSize);
    }

    @Override
    public String getDialogTitle() {
        return MessageConstants.DIALOG_TITLE_QUICK_START_DIALOG;
    }

    protected Point getContainerMargin() {
        return new Point(10, 10);
    }

    protected String getMainButtonText() {
        return StringUtils.EMPTY;
    }

    protected String getTipContent() {
        return StringUtils.EMPTY;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        beginConstruction(parent);

        Point margin = getContainerMargin();
        Composite container = ComponentBuilder.gridContainer(parent).gridMargin(margin.y, margin.x).build();

        createContent(container);
        createButtons(container);
        createTipsComposite(container);

        endConstruction();
        return container;
    }

    private void beginConstruction(Composite parent) {
        StyleContext.begin();
        StyleContext.setBackground(ColorUtil.getWhiteBackgroundColor());
        StyleContext.setColor(ColorUtil.getColor("#09092E"));

        parent.setBackground(StyleContext.getBackground());
        parent.getParent().setBackground(StyleContext.getBackground());

        StyleContext.setFont(FontUtil.H4);
    }

    private void endConstruction() {
        StyleContext.end();
    }

    protected void createContent(Composite container) {

    }

    protected void createButtons(Composite parent) {
        if (StringUtils.isBlank(getMainButtonText())) {
            return;
        }

        ComponentBuilder.label(parent)
                .text(getMainButtonText())
                // .font(FontUtil.FONT_TTNORMS_MEDIUM)
                .fontSize(FontUtil.SIZE_H3)
                // .bold()
                .size(140, 40)
                .gridMarginTop(10)
                .gridMarginBottom(30)
                .center()
                .primaryButton()
                .onClick(event -> {
                    okPressed();
                })
                .build();
    }

    protected void createTipsComposite(Composite parent) {
        if (StringUtils.isBlank(getTipContent())) {
            return;
        }

        Composite tipsCompositeWrapper = ComponentBuilder.gridContainer(parent).fill().build();

        StyleContext.setFontSize(FontUtil.SIZE_H5);
        StyleContext.setBackground(ColorUtil.getColor("#F5F5F5"));

        Composite tipsCompositeInner = ComponentBuilder.gridContainer(tipsCompositeWrapper)
                .fill()
                .gridMargin(10, 50)
                .build();

        tipsComposite = ComponentBuilder.gridContainer(tipsCompositeInner, 2).center().build();

        ComponentBuilder.label(tipsComposite).text("TIPS").bold().size(40, 20).grayBadge().middle().build();

        ComponentBuilder.label(tipsComposite)
                .text(getTipContent())
                .color(ColorUtil.getColor("#797979"))
                .middle()
                .build();

        createMoreTips(tipsComposite);

        StyleContext.prevBackground();
        StyleContext.prevFont();
    }

    protected void createMoreTips(Composite tipsComposite) {
        // Use this method to add more tip:
        // ComponentUtil.appendGridChild(tipsComposite, control);
    }

    protected void addTip(Control tip) {
        ComponentUtil.appendGridChild(tipsComposite, tip);
        tipsComposite.pack();
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return null;
    }

    @Override
    protected boolean canHandleShellCloseEvent() {
        return true;
    }

    @Override
    protected void handleShellCloseEvent() {
        // Do not allow to skip Quick Start dialog
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
    }
}
