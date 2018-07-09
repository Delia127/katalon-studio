package com.kms.katalon.composer.integration.qtest.wizard.page;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.wizard.IWizardPage;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;

public class TestStuctureMappingPage implements IWizardPage, QTestWizardPage {
    @Override
    public String getStepIndexAsString() {
        return "3";
    }

    @Override
    public String getTitle() {
        return ComposerIntegrationQtestMessageConstants.WZ_P_TEST_STRUCTURE_MAPPING_TITLE;
    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    @Override
    public boolean canFinish() {
        return false;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createStepArea(Composite parent) {
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

    @Override
    public boolean autoFlip() {
        return true;
    }
}
