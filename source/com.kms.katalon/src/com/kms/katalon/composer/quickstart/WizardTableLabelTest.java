package com.kms.katalon.composer.quickstart;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.components.impl.wizard.IWizardPage;
import com.kms.katalon.composer.components.impl.wizard.WizardManager;
import com.kms.katalon.composer.components.util.ColorUtil;

public class WizardTableLabelTest extends TypeCheckedStyleCellLabelProvider<IWizardPage> {

    private WizardManager fWizardManager;

    private Object currentElement;

    public WizardTableLabelTest(WizardManager wizardManager) {
        super(0);
        fWizardManager = wizardManager;
    }

    @Override
    protected void paint(Event event, Object element) {
        currentElement = element;
        super.paint(event, element);
    }

    @Override
    protected Color getBackground(Color background, IWizardPage wizardPage) {
        return (getStepNumber(wizardPage) == getCurrentStepNumber()) ? ColorUtil.getSelectedTableItemBackgroundColor()
                : ColorUtil.getWhiteBackgroundColor();
    }

    private int getCurrentStepNumber() {
        return fWizardManager.getWizardPages().indexOf(fWizardManager.getCurrentPage()) + 1;
    }

    private int getStepNumber(IWizardPage wizardPage) {
        return fWizardManager.getWizardPages().indexOf(wizardPage) + 1;
    }

    @Override
    protected Color getForeground(Color foreground, IWizardPage wizardPage) {
        return (getStepNumber(wizardPage) == getCurrentStepNumber()) ? ColorUtil.getTextWhiteColor()
                : ColorUtil.getDefaultTextColor();
    }

    @Override
    protected Class<IWizardPage> getElementType() {
        return IWizardPage.class;
    }

    @Override
    protected String getText(IWizardPage wizardPage) {
        return wizardPage.getTitle();
    }

    
    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        CellLayoutInfo layoutInfo = super.getCellLayoutInfo();
        return new CellLayoutInfo() {

            @Override
            public int getSpace() {
                return layoutInfo.getSpace();
            }

            @Override
            public int getRightMargin() {
                return layoutInfo.getRightMargin();
            }

            @Override
            public int getLeftMargin() {
                int extendedSpace = 0;
                if (currentElement instanceof WizardPage) {
                    WizardPage wizardPage = (WizardPage) currentElement;
                    extendedSpace += wizardPage.isChild() ? 15 : 0;
                }
                return layoutInfo.getLeftMargin() + extendedSpace;
            }
        };
    }

    @Override
    protected Image getImage(IWizardPage element) {
        // TODO Auto-generated method stub
        return null;
    }
}
