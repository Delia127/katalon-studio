package com.kms.katalon.composer.quickstart;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.ComponentUtil.EventHandler;
import com.kms.katalon.composer.components.util.StyleContext;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.CommandUtil;

public class WelcomeBackDialog extends BaseQuickStartDialog {

    public WelcomeBackDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createContent(Composite container) {
        Composite content = ComponentBuilder.gridContainer(container)
                .gridMargin(30, 60, 30)
                .gridVerticalSpacing(5)
                .build();

        createTitle(content);
        createInstructionComposite(content);

        Trackings.trackQuickStartWelcomeBack();
    }

    private void createTitle(Composite parent) {
        ComponentBuilder.label(parent)
                .gridMarginBottom(30)
                .text("Greate to have you back!")
                .center()
                .fontSize(16)
                .bold()
                .build();
    }

    private void createInstructionComposite(Composite parent) {
        Composite actions = ComponentBuilder.gridContainer(parent, 3)
                .center()
                .gridMarginTop(20)
                .gridMarginBottom(30)
                .gridHorizontalSpacing(40)
                .build();

        EventHandler openProjectHandler = (event) -> {
            okPressed();
            CommandUtil.autoHandleExecuteCommand(IdConstants.OPEN_PROJECT_COMMAND_ID,
                    "Unable to open Project Browser dialog!");
            Trackings.trackQuickStartWelcomeBackOpenProject();
        };

        EventHandler cloneProjectHandler = (event) -> {
            okPressed();
            CommandUtil.autoHandleExecuteCommand(IdConstants.CLONE_PROJECT_COMMAND_ID,
                    "Unable to open Clone Project dialog!");
            Trackings.trackQuickStartWelcomeBackCloneProject();
        };

        EventHandler createProjectHandler = (event) -> {
            okPressed();
            CommandUtil.autoHandleExecuteCommand(IdConstants.NEW_PROJECT_COMMAND_ID,
                    "Unable to open New Project dialog!");
            Trackings.trackQuickStartWelcomeBackNewProject();
        };

        createAction(actions, "Open Existing Project", IImageKeys.WELCOME_BACK_OPEN_PROJECT, openProjectHandler);
        createAction(actions, "Clone Project from Git", IImageKeys.WELCOME_BACK_CLONE_PROJECT, cloneProjectHandler);
        createAction(actions, "Create New Project", IImageKeys.WELCOME_BACK_CREATE_PROJECT, createProjectHandler);
    }

    private void createAction(Composite parent, String text, String imageKey, EventHandler onClick) {
        Composite box = ComponentBuilder.gridContainer(parent).build();
        ComponentBuilder.image(box, imageKey, 100).center().gridMarginTop(40).cursorPointer().onClick(onClick).build();
        ComponentBuilder.button(box)
                .text(text)
                .center()
                .primaryButton()
                .color(StyleContext.getColor())
                .background(ColorUtil.getColor("#F5F5F5"))
                .border()
                .gridMarginTop(20)
                .onClick(onClick)
                .build();
    }

    @Override
    protected boolean canHandleShellCloseEvent() {
        return true;
    }

    @Override
    protected void handleShellCloseEvent() {
        okPressed();
    }
}
