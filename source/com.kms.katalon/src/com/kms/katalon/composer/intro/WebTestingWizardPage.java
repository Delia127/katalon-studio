package com.kms.katalon.composer.intro;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;
import com.kms.katalon.constants.ImageConstants;

public class WebTestingWizardPage extends AbstractWizardPage {

    public WebTestingWizardPage() {
        
    }
    
    @Override
    public String getTitle() {
        return "Web Testing";
    }

    @Override
    public void createStepArea(Composite parent) {
        Composite imageCompositeImage = new Composite(parent, SWT.NONE);
        GridData gridDataImage = new GridData(SWT.RIGHT, SWT.FILL, true, true);    
        Image imageTitleArea = ImageConstants.IMG_INTRO_SCREEN_WEB_TESTING;
        gridDataImage.widthHint = imageTitleArea.getBounds().width;
        gridDataImage.heightHint = imageTitleArea.getBounds().height;
        imageCompositeImage.setLayoutData(gridDataImage);
        imageCompositeImage.setBackgroundImage(imageTitleArea);
    }
    
    @Override
    public boolean canFlipToNextPage() {
        return false;
    }
}
