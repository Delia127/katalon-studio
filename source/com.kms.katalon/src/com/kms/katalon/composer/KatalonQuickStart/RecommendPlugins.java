package com.kms.katalon.composer.KatalonQuickStart;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.wizard.IWizardPage;
import com.kms.katalon.imp.wizard.WizardRecommend;

public class RecommendPlugins extends WizardRecommend {

    public RecommendPlugins(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createContents(Composite parent) {
        // create the top level composite for the dialog
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setLayoutData(gridData);
        applyDialogFont(composite);
        Label lb = new Label(composite, SWT.NONE);
        lb.setText("\n\t\t\tMost recommended plugins\n");
        org.eclipse.swt.graphics.Font defaultFont = new org.eclipse.swt.graphics.Font(null, "Aria", 10, SWT.BOLD);
        lb.setFont(defaultFont);
        // initialize the dialog units
        initializeDialogUnits(composite);
        // create the dialog area and button bar
        dialogArea = createDialogArea(composite);

        return composite;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogComposite = (Composite) super.oldDialogArea(parent);
        GridLayout glDialogComposite = (GridLayout) dialogComposite.getLayout();
        glDialogComposite.marginHeight = 20;
        glDialogComposite.marginWidth = 20;
        glDialogComposite.verticalSpacing = 20;
        glDialogComposite.horizontalSpacing = 20;

        createStepArea(dialogComposite);
        createWizardArea(dialogComposite);

        return dialogComposite;
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(arg | SWT.RESIZE);
    }

    @Override
    protected Collection<IWizardPage> getWizardPages() {
        return Arrays.asList(new IWizardPage[] {});
    }

    @Override
    protected Point getInitialSize() {
        return new Point(770, 500);
    }

    @Override
    protected String getDialogTitle() {
        return "Most Recommended Plugins";
    }

    @Override
    public String getStepIndexAsString() {
        return null;
    }

    @Override
    public boolean isChild() {
        return false;
    }

}
