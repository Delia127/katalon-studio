package com.kms.katalon.composer.intro;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.wizard.IWizardPage;
import com.kms.katalon.composer.components.impl.wizard.SimpleWizardDialog;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;

public class FunctionsIntroductionDialog extends SimpleWizardDialog {

    private static final double RATIO = 0.75d;

    private static final Point IMG_SIZE = new Point(1280, 720);

    private FunctionsIntroductionDialogSizeCalculator computeSizeHelper;

    public FunctionsIntroductionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void initializeBounds() {
        computeSizeHelper = new FunctionsIntroductionDialogSizeCalculator(getShell(), IMG_SIZE, RATIO);
        super.initializeBounds();
    }

    @Override
    protected Collection<IWizardPage> getWizardPages() {
       return Arrays.asList(new IWizardPage[] {
                new IntroPage(ImageConstants.IMG_INTRO_SCREEN_1),
                new IntroPage(ImageConstants.IMG_INTRO_SCREEN_2),
                new IntroPage(ImageConstants.IMG_INTRO_SCREEN_3),
                new IntroPage(ImageConstants.IMG_INTRO_SCREEN_4),
                new IntroPage(ImageConstants.IMG_INTRO_SCREEN_5),
        });
    }
    
    protected Composite createButtonBarComposite(Composite parent) {
        Composite buttonBarComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        buttonBarComposite.setLayout(layout);

        createButton(buttonBarComposite, BACK_BUTTON_ID, StringConstants.WZ_SETUP_BTN_BACK);
        createButton(buttonBarComposite, NEXT_BUTTON_ID, StringConstants.WZ_SETUP_BTN_NEXT);
        createButton(buttonBarComposite, FINISH_BUTTON_ID, StringConstants.DIA_CLOSE);
        layout.numColumns = buttonMap.size();
        return buttonBarComposite;
    }

    @Override
    protected void setInput() {
        super.setInput();
        computeSizeHelper.computeDialogSize(stepDetailsComposite);
    }
    
    protected String getDialogTitle() {
        return StringConstants.DIA_TITLE_FUNCTIONS_INTRODUCTION;
    }

    protected Point getInitialSize() {
        return computeSizeHelper.getBestSize();
    }
}
