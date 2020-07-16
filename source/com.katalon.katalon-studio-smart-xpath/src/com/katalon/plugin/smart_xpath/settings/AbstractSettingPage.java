package com.katalon.plugin.smart_xpath.settings;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;

public abstract class AbstractSettingPage extends PreferencePageWithHelp {

    protected Composite container;

    @Override
    protected Control createContents(Composite parent) {
        container = createContainer(parent);
        createSettingsArea(container);

        try {
            initialize();
        } catch (IOException error) {
            LoggerSingleton.logError(error);
        }

        registerListeners();

        return container;
    }

    protected Composite createContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginRight = 10;
        container.setLayout(layout);

        return container;
    }

    protected void createSettingsArea(Composite containter) {
        // Create settings area
    }

    protected void registerListeners() {
        // Register event listener for the controls
    }

    protected void initialize() throws IOException {
        // Initial setting values
    }

    protected void handleInputChanged(Control input, ModifyEvent event) {
        updateApplyButton();
    }

    @Override
    protected void applyDialogFont(Composite composite) {
        super.applyDialogFont(composite);
        handlePageLoad();
    }

    protected void handlePageLoad() {
        updateApplyButton();
    }

    @Override
    protected void performDefaults() {
        updateApplyButton();
    }

    @Override
    protected void performApply() {
        if (isValid()) {
            saveSettings();
        }
        updateApplyButton();
    }

    @Override
    public boolean performOk() {
        return (isValid() && super.performOk() && saveSettings()) || true;
    }

    protected boolean saveSettings() {
        return isValid();
    }

    @Override
    protected void updateApplyButton() {
        Button applyButton = getApplyButton();
        if (applyButton != null && !applyButton.isDisposed()) {
            applyButton.setEnabled(hasChanged());
        }
    }

    @Override
    public boolean isValid() {
        return super.isValid() && container != null;
    }

    protected boolean hasChanged() {
        return true;
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return "#";
    }
}
