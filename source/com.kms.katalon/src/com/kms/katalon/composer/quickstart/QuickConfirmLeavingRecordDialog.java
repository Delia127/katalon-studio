package com.kms.katalon.composer.quickstart;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.tracking.service.Trackings;

public class QuickConfirmLeavingRecordDialog extends BaseQuickStartDialog {

    private final int CONTENT_WIDTH = 500;

    private boolean isActionButtonClicked = false;

    public QuickConfirmLeavingRecordDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public String getDialogTitle() {
         return "We're sad you are leaving...";
    }

    @Override
    protected void createContent(Composite container) {
        Composite body = ComponentBuilder.gridContainer(container).gridMargin(30).gridVerticalSpacing(10).build();

        createGuidings(body);
    }

    private void createGuidings(Composite parent) {
        String confirmQuestion = "You are closing the Katalon Studio Record without saving your test scripts. Are you sure you do not want to see how your automation test case looks like and how to run it?";
        ComponentBuilder.label(parent).text(confirmQuestion).width(CONTENT_WIDTH).build();
    }

    @Override
    protected void createButtons(Composite parent) {
        Composite buttonsCompositeWrapper = ComponentBuilder.gridContainer(parent).fill().build();

        Composite buttonsComposite = ComponentBuilder.gridContainer(buttonsCompositeWrapper, 2).right().build();

        ComponentBuilder.button(buttonsComposite).text("Yeah, I'm sure").grayButton().onClick(event -> {
            isActionButtonClicked = true;
            Trackings.trackQuickRecordLeave();
            cancelPressed();
        }).build();

        ComponentBuilder.button(buttonsComposite).text("Go back").primaryButton().onClick(event -> {
            isActionButtonClicked = true;
            Trackings.trackQuickRecordContinue();
            okPressed();
        }).build();
    }

    @Override
    public boolean close() {
        if (!isActionButtonClicked) { // Click close [X] button
            Trackings.trackQuickRecordContinue();
            setReturnCode(OK);
        }
        return super.close();
    }

    @Override
    protected boolean canClose() {
        return true;
    }
}
