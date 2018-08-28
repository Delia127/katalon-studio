package com.kms.katalon.composer.intro;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.control.ResizableBackgroundImageComposite;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;

public class FunctionsIntroductionFinishDialog extends Dialog {

    private static final double RATIO = 0.75d;

    private static final Point IMG_SIZE = new Point(1280, 720);

    private FunctionsIntroductionDialogSizeCalculator computeSizeHelper;

    private ResizableBackgroundImageComposite imageComposite;

    public FunctionsIntroductionFinishDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void initializeBounds() {
        computeSizeHelper = new FunctionsIntroductionDialogSizeCalculator(getShell(), IMG_SIZE, RATIO);
        super.initializeBounds();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        imageComposite = new ResizableBackgroundImageComposite(container, SWT.NONE, ImageConstants.IMG_INTRO_SCREEN_WELCOME);

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, StringConstants.DIA_LET_START, true);
//        createButton(parent, IDialogConstants.CANCEL_ID, StringConstants.DIA_CANCEL, false);
    }

    @Override
    public void create() {
        super.create();
        computeSizeHelper.computeDialogSize(imageComposite);
    }

    @Override
    protected Point getInitialSize() {
        return computeSizeHelper.getBestSize();
    }
}
