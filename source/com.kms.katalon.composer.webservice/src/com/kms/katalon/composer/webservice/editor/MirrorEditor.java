package com.kms.katalon.composer.webservice.editor;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class MirrorEditor extends Composite {

    private static final String RESOURCES_TEMPLATE_EDITOR = "resources/template/editor";

    private Browser browser;

    private boolean documentReady = false;

    private DocumentReadyHandler documentReadyHandler;

    private File templateFile;

    private Widget parent;

    public MirrorEditor(Composite parent, int style) {
        super(parent, style);
        this.parent = parent;

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        browser = new Browser(this, style);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        browser.setJavascriptEnabled(true);
        
        templateFile = initHTMLTemplateFile();
        try {
            browser.setUrl(templateFile.toURI().toURL().toString());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }

        browser.addProgressListener(new ProgressListener() {

            @Override
            public void completed(ProgressEvent event) {
                documentReady = true;
            }

            @Override
            public void changed(ProgressEvent event) {
            }
        });
    }

    private File initHTMLTemplateFile() {
        File templateFile = null;
        try {
            File codeMirrorTempFolder = new File(ProjectController.getInstance().getNonremovableTempDir(),
                    "editor/codemirror");
            if (!codeMirrorTempFolder.exists() || ArrayUtils.isEmpty(codeMirrorTempFolder.listFiles())) {
                codeMirrorTempFolder.mkdirs();

                // Todo Thai get current bundle.
                File bundleLocation = FileLocator.getBundleFile(FrameworkUtil.getBundle(TextBodyEditor.class));

                if (bundleLocation.isDirectory()) {
                    FileUtils.copyDirectory(new File(bundleLocation, RESOURCES_TEMPLATE_EDITOR),
                            codeMirrorTempFolder.getParentFile());
                } else {
                    FileUtils.copyDirectory(
                            new File(ClassPathResolver.getConfigurationFolder(), RESOURCES_TEMPLATE_EDITOR),
                            codeMirrorTempFolder.getParentFile());
                }
            }
            templateFile = new File(codeMirrorTempFolder,
                    String.format("template_%d.html", System.currentTimeMillis()));
            FileUtils.copyFile(new File(codeMirrorTempFolder, "template.html"), templateFile);

        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(ComposerWebserviceMessageConstants.PA_MSG_UNABLE_TO_OPEN_BODY_EDITOR,
                    e.getMessage(), ExceptionsUtil.getMessageForThrowable(e));
        }
        return templateFile;
    }

    public void setEditable(boolean editable) {
        browser.evaluate(MessageFormat.format("editor.setOption(\"{0}\", {1});", "readOnly", !editable));
    }

    public void setText(String text) {
        if (!documentReady) {
            sleepForDocumentReady();
        }
        browser.evaluate(String.format("editor.setValue(\"%s\");", StringEscapeUtils.escapeEcmaScript(text)));
    }

    public void wrapLine(boolean wrapped) {
        browser.evaluate(MessageFormat.format("editor.setOption(\"{0}\", {1});", "lineWrapping", wrapped));
    }

    public Object evaluate(String script) {
        return browser.evaluate(script);
    }

    public void sleepForDocumentReady() {
        Thread thread = new Thread(() -> {
            while (!documentReady) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException ignored) {}
            }
            UISynchronizeService.syncExec(() -> documentReady());
        });
        thread.start();
    }

    private void documentReady() {
        if (documentReadyHandler != null) {
            documentReadyHandler.onDocumentReady();
        }

        new BrowserFunction(browser, "handleEditorChanged") {
            @Override
            public Object function(Object[] objects) {
                parent.notifyListeners(SWT.Modify, new Event());
                return null;
            }
        };

        addDisposeListener(e -> {
            if (templateFile != null && templateFile.exists()) {
                templateFile.delete();
            }
        });
    }

    public void registerDocumentHandler(DocumentReadyHandler handler) {
        this.documentReadyHandler = handler;
    }

}
