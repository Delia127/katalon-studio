package com.kms.katalon.composer.integration.qtest.wizard.page;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.wizard.IWizardPage;

public class FinishPage implements IWizardPage {

    @Override
    public String getTitle() {
        return StringConstants.WZ_P_FINISH_TITLE;
    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    @Override
    public boolean canFinish() {
        return true;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createStepArea(Composite parent) {
        Composite areaComposite = new Composite(parent, SWT.NONE);
        areaComposite.setLayout(new GridLayout(1, false));

        Label lblHeader = new Label(areaComposite, SWT.NONE);
        GridData gdLblHeader = new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1);
        gdLblHeader.verticalIndent = 20;        
        lblHeader.setLayoutData(gdLblHeader);
        lblHeader.setText(StringConstants.WZ_P_FINISH_INFO);
        ControlUtils.setFontSize(lblHeader, 12);
    }

    @Override
    public void setInput(Map<String, Object> sharedData) {

    }

    @Override
    public void registerControlModifyListeners() {

    }

    @Override
    public Map<String, Object> storeControlStates() {
        return null;
    }
}
