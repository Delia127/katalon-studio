package com.kms.katalon.composer.intro;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.webservice.support.UrlEncoder;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;

public class UserFeedbackDialog extends Dialog {

    private Browser browser;

    protected boolean shouldShowDialogAgain;

    public UserFeedbackDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.APPLICATION_MODAL | SWT.NO_TRIM | SWT.ON_TOP);
        shouldShowDialogAgain = getPreferenceStore()
                .getBoolean(PreferenceConstants.GENERAL_SHOW_USER_FEEDBACK_DIALOG_ON_APP_CLOSE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 600;
        gdBody.heightHint = 267;
        body.setLayoutData(gdBody);
        GridLayout glBody = new GridLayout(1, false);
        glBody.marginHeight = 0;
        glBody.marginWidth = 0;
        body.setLayout(glBody);

        browser = new Browser(body, SWT.NONE);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        browser.setJavascriptEnabled(true);
        browser.setUrl(getHTMLTemplateFileUrl().toString());

        registerBrowserListeners();
        return body;
    }

    private void registerBrowserListeners() {
        browser.addLocationListener(new LocationListener() {

            @Override
            public void changing(LocationEvent event) {
            }

            @Override
            public void changed(LocationEvent event) {
                new BrowserFunction(browser, "handleOkPressed") {
                    @Override
                    public Object function(Object[] objects) {
                        UserFeedbackDialog.this.handleOkPressed();
                        return null;
                    }
                };

                new BrowserFunction(browser, "handleCancelPressed") {
                    @Override
                    public Object function(Object[] objects) {
                        UserFeedbackDialog.this.handleCancelPressed();
                        return null;
                    }
                };

                new BrowserFunction(browser, "handleCbDoNotShowClicked") {
                    @Override
                    public Object function(Object[] objects) {
                        boolean shouldNotShowDialogAgain = ((Boolean) objects[0]).booleanValue();
                        UserFeedbackDialog.this.shouldShowDialogAgain = !shouldNotShowDialogAgain;
                        return null;
                    }
                };
            }

        });
    }

    private void handleOkPressed() {
        Trackings.trackUserResponseForTwitterDialog("ok");
        Program.launch(getTwitterUrl());
        getPreferenceStore().setValue(PreferenceConstants.GENERAL_SHOW_USER_FEEDBACK_DIALOG_ON_APP_CLOSE, false);
        close();
    }

    private String getTwitterUrl() {
        return "https://twitter.com/intent/tweet?text="
                + UrlEncoder.encode(MessageConstants.UserFeedbackDialog_MSG_USER_TWEET) + "&url="
                + UrlEncoder.encode("https://www.katalon.com");
    }

    private void handleCancelPressed() {
        Trackings.trackUserResponseForTwitterDialog("later");
        getPreferenceStore().setValue(PreferenceConstants.GENERAL_SHOW_USER_FEEDBACK_DIALOG_ON_APP_CLOSE,
                shouldShowDialogAgain);
        close();
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }

    private URL getHTMLTemplateFileUrl() {
        URL templateFileUrl = null;
        try {
            Bundle bundle = FrameworkUtil.getBundle(UserFeedbackDialog.class);
            Path templateFolderPath = new Path("/resources/feedback");

            
            URL templateFolderUrl = FileLocator.find(bundle, templateFolderPath, null);
            File templateFolder = FileUtils.toFile(FileLocator.toFileURL(templateFolderUrl));
           
            File templateFile = FileUtils.getFile(templateFolder, "feedback_dialog_template.html");
            
            templateFileUrl = templateFile.toURI().toURL();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }

        return templateFileUrl;
    }
}
