package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.net.URISyntaxException;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.setting.EmailSettingStore;
import com.kms.katalon.execution.util.EmailTemplateUtil;

public class TestSuiteEmailTemplatePage extends EmailTemplatePage {

    private EmailSettingStore settingStore;

    public TestSuiteEmailTemplatePage() {
        settingStore = new EmailSettingStore(ProjectController.getInstance().getCurrentProject());
    }

    @Override
    protected String getHTMLTemplate() throws IOException, URISyntaxException {
        return settingStore.getEmailHTMLTemplateForTestSuite();
    }

    @Override
    protected String getDefaultHTMLTemplate() throws IOException, URISyntaxException {
        return EmailTemplateUtil.getHTMLTemplateForTestSuite();
    }

    @Override
    protected void save() throws IOException {
        settingStore.setHTMLTemplateForTestSuite(getHtmlMessage());
    }

}
