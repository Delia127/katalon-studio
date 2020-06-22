package com.kms.katalon.execution.util;

import static com.kms.katalon.core.constants.StringConstants.DF_CHARSET;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class EmailTemplateUtil {

    private static final String KATALON_STUDIO_EMAIL_SIGNATURE = "Katalon Studio";

    private static final String RESOURCES_TEMPLATE_EMAIL_FOLDER = "resources/template/email";

    private static final String TEST_SUITE_EMAIL_TEMPLATE_HTML = "default_template.html";

    private static final String TEST_SUITE_COLLECTION_EMAIL_TEMPLATE_HTML = "default_collection_template.html";

    private static final String TINY_MCE_TEMPLATE_HTML = "tinymce_template.html";

    public static String getTinyMCETemplate() throws IOException, URISyntaxException {
        File htmlTemplateFile = new File(getTemplateFolder(), TINY_MCE_TEMPLATE_HTML);
        String htmlTemplate = FileUtils.readFileToString(htmlTemplateFile, DF_CHARSET);
        return htmlTemplate;
    }

    public static String getHTMLTemplateForTestSuite() throws IOException, URISyntaxException {
        return getHTMLTemplateForTestSuite(KATALON_STUDIO_EMAIL_SIGNATURE);
    }

    public static String getHTMLTemplateForTestSuite(String emailSignature) throws IOException, URISyntaxException {
        File templateFile = new File(getTemplateFolder(), TEST_SUITE_EMAIL_TEMPLATE_HTML);
        String template = FileUtils.readFileToString(templateFile);
        String signature = StringUtils.defaultIfEmpty(emailSignature, KATALON_STUDIO_EMAIL_SIGNATURE);
        template = template.replace(KATALON_STUDIO_EMAIL_SIGNATURE, signature);
        return template;
    }

    public static String getEmailHTMLTemplateForTestSuiteCollection() throws IOException, URISyntaxException {
        File templateFile = new File(getTemplateFolder(), TEST_SUITE_COLLECTION_EMAIL_TEMPLATE_HTML);
        String template = FileUtils.readFileToString(templateFile);
        return template;
    }

    public static File getTemplateFolder() throws IOException {
        Bundle bundle = FrameworkUtil.getBundle(EmailTemplateUtil.class);
        URL templateFolderUrl = FileLocator.find(bundle, new Path(RESOURCES_TEMPLATE_EMAIL_FOLDER), null);
        return FileUtils.toFile(FileLocator.toFileURL(templateFolderUrl));
    }
}
