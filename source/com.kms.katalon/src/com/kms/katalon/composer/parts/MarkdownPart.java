package com.kms.katalon.composer.parts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.ResourcesUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class MarkdownPart {
    private boolean shouldSetContent = true;

    @PostConstruct
    public void createComposite(Composite parent, MPart mpart) {
        File sourceFile = (File) mpart.getObject();

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        Browser browser = new Browser(container, SWT.NONE);
        browser.setJavascriptEnabled(true);

        String sourceContent = ResourcesUtil.getFileContent(MarkdownPart.class,
                "resources/template/markdown_template.html");
        browser.setText(sourceContent);

        browser.addProgressListener(new ProgressListener() {

            @Override
            public void completed(ProgressEvent event) {
                try {
                    if (shouldSetContent) {
                        shouldSetContent = false;
                        String content = StringEscapeUtils
                                .escapeEcmaScript(FileUtils.readFileToString(sourceFile, StandardCharsets.UTF_8));
                        browser.evaluate("document.getElementById('content').innerHTML = marked('" + content + "');");
                    }
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }

            @Override
            public void changed(ProgressEvent event) {

            }
        });
    }

}
