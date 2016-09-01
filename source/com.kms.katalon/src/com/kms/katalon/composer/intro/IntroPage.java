package com.kms.katalon.composer.intro;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.control.ResizableBackgroundImageComposite;
import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;

public class IntroPage extends AbstractWizardPage {

    private Image image;

    public IntroPage(Image image) {
        this.image = image;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createStepArea(Composite parent) {
        new ResizableBackgroundImageComposite(parent, SWT.NONE, image);
    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    @Override
    public boolean canFinish() {
        return true;
    }
}
