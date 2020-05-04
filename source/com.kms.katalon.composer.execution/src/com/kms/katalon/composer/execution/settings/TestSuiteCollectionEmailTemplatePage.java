package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.net.URISyntaxException;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.setting.EmailSettingStore;
import com.kms.katalon.execution.util.EmailTemplateUtil;

public class TestSuiteCollectionEmailTemplatePage extends EmailTemplatePage {

    private EmailSettingStore settingStore;
    
    public TestSuiteCollectionEmailTemplatePage() {
        settingStore = new EmailSettingStore(ProjectController.getInstance().getCurrentProject());
    }
    
    @Override
    protected String getHTMLTemplate() throws IOException, URISyntaxException {
        return settingStore.getEmailHTMLTemplateForTestSuiteCollection();
    }

    @Override
    protected String getDefaultHTMLTemplate() throws IOException, URISyntaxException {
        return EmailTemplateUtil.getEmailHTMLTemplateForTestSuiteCollection();
    }

    @Override
    protected void save() throws IOException {
        settingStore.setHTMLTemplateForTestSuiteCollection(getHtmlMessage());
    }

}
