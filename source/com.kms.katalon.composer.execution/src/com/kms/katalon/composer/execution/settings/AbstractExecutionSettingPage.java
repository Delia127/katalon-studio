package com.kms.katalon.composer.execution.settings;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.DocumentationMessageConstants;

public abstract class AbstractExecutionSettingPage extends PreferencePageWithHelp {

    public static final short TIMEOUT_MIN_VALUE_IN_SEC = 0;

    public static final short TIMEOUT_MAX_VALUE_IN_SEC = 9999;

    public static final short TIMEOUT_MIN_VALUE_IN_MILISEC = 0;

    public static final int TIMEOUT_MAX_VALUE_IN_MILISEC = 9999999;

    protected static final int INPUT_WIDTH = 60;

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

    protected Composite createSettingsArea(Composite containter) {
        return containter;
    }

    protected void registerListeners() {
        // Register event listener for the controls
    }

    protected void initialize() throws IOException {
        // Initial setting values
    }

    protected void addNumberVerification(Text txtInput, final int min, final int max) {
        if (txtInput == null || txtInput.isDisposed()) {
            return;
        }

        txtInput.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent event) {
                handleInputChanged(txtInput, event);
            }
        });

        txtInput.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                String oldValue = ((Text) e.getSource()).getText();
                String enterValue = e.text;
                String newValue = oldValue.substring(0, e.start) + enterValue + oldValue.substring(e.end);
                if (!newValue.matches("\\d+")) {
                    e.doit = false;
                    return;
                }
                try {
                    int val = Integer.parseInt(newValue);
                    e.doit = val >= min && val <= max;
                } catch (NumberFormatException ex) {
                    e.doit = false;
                }
            }
        });

        txtInput.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                ((Text) e.getSource()).selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                Text inputField = (Text) e.getSource();
                String value = inputField.getText();
                if (value.length() <= 1 || !value.startsWith("0")) {
                    return;
                }
                try {
                    int val = Integer.parseInt(value);
                    inputField.setText(String.valueOf(val));
                } catch (NumberFormatException ex) {
                    // Do nothing
                }
            }
        });

        txtInput.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                ((Text) e.getSource()).selectAll();
            }
        });
    }

    protected void handleInputChanged(Control input, ModifyEvent event) {
        updateApplyButton();
    }

    @Override
    protected void applyDialogFont(Composite composite) {
        super.applyDialogFont(composite);
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
        return super.isValid();
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
        return DocumentationMessageConstants.SETTINGS_EXECUTION;
    }
}
