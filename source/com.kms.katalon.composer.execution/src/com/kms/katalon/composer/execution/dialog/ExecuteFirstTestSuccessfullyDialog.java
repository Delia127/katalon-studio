package com.kms.katalon.composer.execution.dialog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.composer.quickstart.BaseQuickStartDialog;
import com.kms.katalon.composer.quickstart.LinkBox;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.tracking.service.Trackings;

public class ExecuteFirstTestSuccessfullyDialog extends BaseQuickStartDialog {

    public ExecuteFirstTestSuccessfullyDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createContent(Composite container) {
        Composite content = ComponentBuilder.gridContainer(container)
                .gridMargin(40, 60, 20)
                .gridVerticalSpacing(5)
                .build();

        createHeader(content);
        createInstructionComposite(content);

        Trackings.trackQuickStartRunPass();
    }

    private void createHeader(Composite parent) {
        ComponentBuilder.label(parent)
                .gridMarginBottom(10)
                .text("Congratulations! Your journey has just begun.")
                .center()
                .fontSize(FontUtil.SIZE_H1)
                .build();

        ComponentBuilder.label(parent)
                .text("You have executed your first test case successfully.")
                .center()
                .fontSize(FontUtil.SIZE_H3)
                .build();
    }

    private void createInstructionComposite(Composite parent) {
        Composite instructionComposite = ComponentBuilder.gridContainer(parent, 1)
                .gridVerticalSpacing(10)
                .gridMarginTop(20)
                .center()
                .build();

        ComponentBuilder.label(instructionComposite)
                .text("To explore Katalon Studio further, refer to these resources")
                .fontSize(13)
                .center()
                .colSpan(1)
                .gridMarginY(20)
                .build();

        Composite resources = ComponentBuilder.gridContainer(parent, 5).center().gridHorizontalSpacing(20).build();
        new LinkBox(resources, "Tutorials", DocumentationMessageConstants.TUTORIALS, IImageKeys.RESOURCES_TUTORIALS);
        new LinkBox(resources, "Forum", DocumentationMessageConstants.FORUM, IImageKeys.RESOURCES_FORUM);
        new LinkBox(resources, "Chatroom", DocumentationMessageConstants.CHATROOM, IImageKeys.RESOURCES_CHATROOM);
        new LinkBox(resources, "Plugins", DocumentationMessageConstants.KATALON_STORE, IImageKeys.RESOURCES_PLUGINS);
        new LinkBox(resources, "Business", "mailto:" + DocumentationMessageConstants.BUSINESS_EMAIL,
                IImageKeys.RESOURCES_BUSINESS);
    }

    @Override
    protected String getMainButtonText() {
        return "Got it";
    }

    @Override
    protected String getTipContent() {
        return "Find the above resources in Help > Katalon help";
    }
}
