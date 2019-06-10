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

public class APITestingWizardPage extends AbstractWizardPage implements WizardPage{

    public APITestingWizardPage() {
        
    }
    
    @Override
    public String getTitle() {
        return "API Testing";
    }

    @Override
    public void createStepArea(Composite parent) {
        Composite imageCompositeImage = new Composite(parent,SWT.FILL);
        GridData gridDataImage = new GridData(SWT.RIGHT, SWT.FILL, true, true);
        Image imageTitleArea = ImageConstants.IMG_INTRO_SCREEN_API_TESTING;
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
        return "2";
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
