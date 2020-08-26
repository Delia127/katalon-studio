package com.kms.katalon.composer.parts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.editors.MarkdownPart;
import com.kms.katalon.composer.components.impl.util.ResourcesUtil;

public class WelcomeOnlineRightPart extends Composite {
    private boolean shouldSetContent = true;

    
    public WelcomeOnlineRightPart(Composite parent) throws MalformedURLException, IOException {
        super(parent, SWT.NONE);
        GridLayout glWrapper = new GridLayout(1, false);
        glWrapper.marginTop = 2;
        glWrapper.marginBottom = 2;
        this.setLayout(glWrapper);
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        init();
    }
    
    private void init() throws MalformedURLException, IOException {
        @SuppressWarnings("deprecation")
        String content = StringEscapeUtils
                .escapeEcmaScript(getContentFromHTML("https://raw.githubusercontent.com/katalon-studio/katalon-studio/master/katalon-help.md"));
        Composite container = new Composite(this, SWT.NONE);
        container.setLayout(new FillLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Browser browser = new Browser(container, SWT.NONE);
        browser.setJavascriptEnabled(true);

        String sourceContent = ResourcesUtil.getFileContent(MarkdownPart.class,
                "resources/template/markdown_template.html");
        browser.setText(sourceContent);

        browser.addProgressListener(new ProgressListener() {

            @Override
            public void completed(ProgressEvent event) {
                if (shouldSetContent) {
                    shouldSetContent = false;
                    browser.evaluate("document.getElementById('content').innerHTML = marked('" + content + "');");
                }
            }

            @Override
            public void changed(ProgressEvent event) {

            }
        });
    }
    
    private String getContentFromHTML(String link) throws MalformedURLException, IOException {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            StringBuilder sb = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine).append("\n");
            }
            br.close();
            return sb.toString();
    }
}
