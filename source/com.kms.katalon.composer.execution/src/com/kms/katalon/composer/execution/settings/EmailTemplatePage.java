package com.kms.katalon.composer.execution.settings;

import static com.kms.katalon.core.constants.StringConstants.DF_CHARSET;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.setting.EmailSettingStore;
import com.kms.katalon.groovy.util.GroovyStringUtil;

public class EmailTemplatePage extends PreferencePageWithHelp {

    private File templateFile;

    private Browser browser;

    private EmailSettingStore settingStore;

    public EmailTemplatePage() {
        settingStore = new EmailSettingStore(ProjectController.getInstance().getCurrentProject());
    }

    @Override
    protected Control createContents(Composite parent) {

        Composite browserComposite = new Composite(parent, SWT.BORDER);
        browserComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        browserComposite.setLayout(new FillLayout());

        browser = new Browser(browserComposite, SWT.BORDER);
        browser.setJavascriptEnabled(true);

        updateInput();
        return browserComposite;
    }

    private void updateInput() {
        try {
            templateFile = new File(settingStore.getTemplateFolder(),
                    String.format("template_%d.html", System.currentTimeMillis()));

            String tinyMCE = FileUtils
                    .readFileToString(new File(settingStore.getTemplateFolder(), "tinymce_template.html"), DF_CHARSET);
            Map<String, Object> variables = new HashMap<>();
            variables.put("htmlTemplate", settingStore.getEmailHTMLTemplate());
            String newContent = GroovyStringUtil.evaluate(tinyMCE, variables);
            FileUtils.write(templateFile, newContent, DF_CHARSET);
            browser.setUrl(templateFile.toURI().toURL().toString());
        } catch (IOException | URISyntaxException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public void dispose() {
        if (isControlCreated()) {
            cleanTempTemplateFile();
        }
        super.dispose();
    }

    private void cleanTempTemplateFile() {
        try {
            FileUtils.forceDelete(templateFile);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private String getHtmlMessage() {
        String body = (String) browser.evaluate("return tinyMCE.get('myTextArea').getContent();");
        return String.format("<html><body>%s</body></html>", body);
    }

    @Override
    protected void performDefaults() {
        try {
            String tinyMCE = FileUtils
                    .readFileToString(new File(settingStore.getTemplateFolder(), "tinymce_template.html"), DF_CHARSET);

            Map<String, Object> variables = new HashMap<>();
            variables.put("htmlTemplate", settingStore.getDefaultEmailHTMLTemplate());

            FileUtils.write(templateFile, GroovyStringUtil.evaluate(tinyMCE, variables), DF_CHARSET);
            browser.setUrl(templateFile.toURI().toURL().toString());
        } catch (IOException | URISyntaxException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR_TITLE, e.getMessage());
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        if (!isControlCreated()) {
            return true;
        }
        try {
            settingStore.setHTMLTemplate(getHtmlMessage());
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR_TITLE, e.getMessage());
            LoggerSingleton.logError(e);
            return false;
        }
    }
}
