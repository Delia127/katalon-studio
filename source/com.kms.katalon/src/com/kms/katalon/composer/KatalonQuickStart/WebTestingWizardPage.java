package com.kms.katalon.composer.KatalonQuickStart;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;
import com.kms.katalon.constants.ImageConstants;

public class WebTestingWizardPage extends AbstractWizardPage implements WizardPage {

    public WebTestingWizardPage() {

    }

    @Override
    public String getTitle() {
        return "Web Testing";
    }

    @Override
    public void createStepArea(Composite parent) {

        /*ScrolledComposite c1 = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.CENTER);
        c1.setExpandHorizontal(true);
        c1.setExpandVertical(true);
        

        Composite leftComposite = new Composite(c1, SWT.NONE);
        c1.setContent(leftComposite);
        GridLayout glLeft = new GridLayout(1, false);
        glLeft.marginWidth = 0;
        glLeft.marginHeight = 0;
        glLeft.marginLeft = 0;
        glLeft.horizontalSpacing = 0;
        glLeft.verticalSpacing = 0;
        Image imageTitleArea = ImageConstants.IMG_INTRO_SCREEN_WEB_TESTING;
        c1.setMinSize(1000, 1000);
        leftComposite.setLayout(glLeft);
        leftComposite.setBackgroundImage(imageTitleArea);
        ;
        GridData gdLeft = new GridData(SWT.FILL, SWT.FILL, true, true);
        c1.setLayoutData(gdLeft);
        c1.setVisible(true);*/
       
         Composite imageCompositeImage = new Composite(parent,SWT.FILL);
         GridData gridDataImage = new GridData(SWT.RIGHT, SWT.FILL, true, true);
         Image imageTitleArea = ImageConstants.IMG_INTRO_SCREEN_WEB_TESTING;
         gridDataImage.widthHint = imageTitleArea.getBounds().width;
         gridDataImage.heightHint = imageTitleArea.getBounds().height;
         imageCompositeImage.setLayoutData(gridDataImage);
         imageCompositeImage.setBackgroundImage(imageTitleArea);

    }

    @Override
    public void setInput(Map<String, Object> sharedData) {

    }

    @Override
    public void registerControlModifyListeners() {

    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    @Override
    public boolean autoFlip() {
        return false;
    }

    @Override
    public String getStepIndexAsString() {
        return "1";
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public boolean canFinish() {
        return true;
    }
}
