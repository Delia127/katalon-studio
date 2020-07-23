package com.kms.katalon.composer.execution.dialog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.composer.components.util.StyleContext;
import com.kms.katalon.composer.quickstart.BaseQuickStartDialog;
import com.kms.katalon.composer.quickstart.LinkBox;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.tracking.service.Trackings;

public class ExecuteFirstTestUnsuccessfullyDialog extends BaseQuickStartDialog {

    public ExecuteFirstTestUnsuccessfullyDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createContent(Composite container) {
        Composite content = ComponentBuilder.gridContainer(container)
                .gridMargin(40, 60, 20)
                .gridVerticalSpacing(5)
                .build();

        createTitle(content);
        createInstructionComposite(content);

        Trackings.trackQuickStartRunFail();
    }

    private void createTitle(Composite parent) {
        ComponentBuilder.label(parent)
                .gridMarginBottom(10)
                .text("Your test execution has failed. Let's investigate!")
                .center()
                .fontSize(FontUtil.SIZE_H1)
                .build();
    }

    private void createInstructionComposite(Composite parent) {
        Composite instructionComposite = ComponentBuilder.gridContainer(parent, 1)
                .gridVerticalSpacing(10)
                .gridMarginTop(20)
                .gridMarginBottom(10)
                .center()
                .build();

        Composite row = ComponentBuilder.gridContainer(instructionComposite, 4).center().build();

        StyleContext.setFontSize(FontUtil.SIZE_H3);

        // "You can find the root cause at the Log Viewer and Console panel"
        ComponentBuilder.label(row).text("You can find the root cause at the").build();
        ComponentBuilder.image(row, IImageKeys.INVESTIGATE_LOG_VIEWER).build();
        ComponentBuilder.label(row).text("&&").build();
        ComponentBuilder.image(row, IImageKeys.INVESTIGATE_CONSOLE).build();

        StyleContext.prevFont();

        ComponentBuilder.label(instructionComposite)
                .text("If you need more help, get support via")
                .fontSize(13)
                .center()
                .gridMarginY(20)
                .build();

        Composite resources = ComponentBuilder.gridContainer(parent, 3).center().gridHorizontalSpacing(20).build();
        new LinkBox(resources, "Troubleshot", DocumentationMessageConstants.TROUBLESHOOT_WEB_TESTING,
                IImageKeys.RESOURCES_TROUBLESHOOT);
        new LinkBox(resources, "Chatroom", DocumentationMessageConstants.CHATROOM, IImageKeys.RESOURCES_CHATROOM);
        new LinkBox(resources, "Forum", DocumentationMessageConstants.FORUM, IImageKeys.RESOURCES_FORUM);
    }

    @Override
    protected String getMainButtonText() {
        return "Got it";
    }

//    @Override
//    protected String getTipContent() {
//        return "Find the above resources in Help > Katalon help";
//    }
}
