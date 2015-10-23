package com.kms.katalon.composer.integration.qtest.wizard.provider;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.qtest.wizard.AbstractWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.IWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.WizardManager;

public class WizardTableLabelProvider extends StyledCellLabelProvider {

    private WizardManager fWizardManager;

    public WizardTableLabelProvider(WizardManager wizardManager) {
        fWizardManager = wizardManager;
    }

    @Override
    public void update(ViewerCell cell) {

        if (cell.getElement() == null) {
            return;
        }

        IWizardPage wizardPage = (IWizardPage) cell.getElement();
        int stepNumber = fWizardManager.getWizardPages().indexOf(wizardPage) + 1;
        int currentStepNumber = fWizardManager.getWizardPages().indexOf(fWizardManager.getCurrentPage()) + 1;
        
        if (wizardPage instanceof AbstractWizardPage) {
            if (stepNumber < currentStepNumber) {
                cell.setImage(ImageConstants.IMG_16_CHECKED);
            } else {
                cell.setImage(ImageConstants.IMG_16_UNCHECKED);
            }
        }
        
        cell.setText(Integer.toString(stepNumber) + ". " + wizardPage.getTitle());
        
        if (stepNumber == currentStepNumber) {
            cell.setBackground(ColorUtil.getSelectedTableItemBackgroundColor());
            cell.setForeground(ColorUtil.getTextWhiteColor());
        } else {
            cell.setBackground(ColorUtil.getWhiteBackgroundColor());
            cell.setForeground(ColorUtil.getDefaultTextColor());
        }

        super.update(cell);
    }

}
