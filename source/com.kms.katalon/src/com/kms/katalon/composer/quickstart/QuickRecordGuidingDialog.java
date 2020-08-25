package com.kms.katalon.composer.quickstart;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.tracking.service.Trackings;

public class QuickRecordGuidingDialog extends BaseQuickStartDialog {

    public QuickRecordGuidingDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createContent(Composite container) {
        Composite body = ComponentBuilder.gridContainer(container).gridMargin(30).gridVerticalSpacing(10).build();

        createGuidings(body);
    }

    private void createGuidings(Composite parent) {
        ComponentBuilder.label(parent)
                .text("To create automated test cases by Katalon Studio Recorder:")
                .fontSize(FontUtil.SIZE_H3)
                .build();

        String step1 = "1. Interact with the web application following the scenario you want to test.";
        ComponentBuilder.label(parent).text(step1).build();

        String step2 = "2. Once you're done, click [Save Script]. All your interactions will be stored as scripts to a new test case.";
        ComponentBuilder.label(parent).text(step2).width(500).build().requestLayout();
    }

    @Override
    protected String getMainButtonText() {
        return "Got it!";
    }

    @Override
    public boolean close() {
        Trackings.trackQuickStartGuidingDialog();
        return super.close();
    }
}
