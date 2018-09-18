package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class WebUiSettingsPerferencePage extends PreferencePage {

    public static final short TIMEOUT_MIN_VALUE = 0;

    public static final short TIMEOUT_MAX_VALUE = 9999;

    private static final int INPUT_WIDTH = 60;

    private WebUiExecutionSettingStore store;

    private Text txtDefaultPageLoadTimeout, txtActionDelay, txtDefaultIEHangTimeout;

    private Composite container;

    private Button radioNotUsePageLoadTimeout, radioUsePageLoadTimeout, chckIgnorePageLoadTimeoutException;

    public WebUiSettingsPerferencePage() {
        store = new WebUiExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        createTimeoutSettings(container);

        try {
            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        registerListeners();

        return container;
    }

    private void createTimeoutSettings(Composite container) {
        Label lblActionDelay = new Label(container, SWT.NONE);
        lblActionDelay.setText(ComposerWebuiMessageConstants.LBL_ACTION_DELAY);
        GridData gdLblActionDelay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblActionDelay.setLayoutData(gdLblActionDelay);

        txtActionDelay = new Text(container, SWT.BORDER);
        GridData ldActionDelay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        ldActionDelay.widthHint = INPUT_WIDTH;
        txtActionDelay.setLayoutData(ldActionDelay);

        Label lblDefaultIEHangTimeout = new Label(container, SWT.NONE);
        lblDefaultIEHangTimeout.setText(StringConstants.PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT);
        lblDefaultIEHangTimeout.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtDefaultIEHangTimeout = new Text(container, SWT.BORDER);
        GridData gdTxtDefaultIEHangTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtDefaultIEHangTimeout.widthHint = INPUT_WIDTH;
        txtDefaultIEHangTimeout.setLayoutData(gdTxtDefaultIEHangTimeout);

        Label lblDefaultPageLoadTimeout = new Label(container, SWT.NONE);
        lblDefaultPageLoadTimeout.setText(StringConstants.PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT);
        lblDefaultPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Composite compPageLoad = new Composite(container, SWT.NONE);
        GridLayout glCompPageLoad = new GridLayout(2, false);
        glCompPageLoad.marginWidth = 0;
        glCompPageLoad.marginHeight = 0;
        glCompPageLoad.marginLeft = 15;
        compPageLoad.setLayout(glCompPageLoad);
        compPageLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        radioNotUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioNotUsePageLoadTimeout.setText(StringConstants.PREF_LBL_ENABLE_DEFAULT_PAGE_LOAD_TIMEOUT);
        radioNotUsePageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        radioUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioUsePageLoadTimeout.setText(StringConstants.PREF_LBL_CUSTOM_PAGE_LOAD_TIMEOUT);
        GridData gdRadioPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        radioUsePageLoadTimeout.setLayoutData(gdRadioPageLoadTimeout);

        txtDefaultPageLoadTimeout = new Text(compPageLoad, SWT.BORDER);
        GridData gdDefaultPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdDefaultPageLoadTimeout.widthHint = INPUT_WIDTH;
        txtDefaultPageLoadTimeout.setLayoutData(gdDefaultPageLoadTimeout);

        new Label(compPageLoad, SWT.NONE);
        chckIgnorePageLoadTimeoutException = new Button(compPageLoad, SWT.CHECK);
        chckIgnorePageLoadTimeoutException.setText(StringConstants.PREF_LBL_IGNORE_DEFAULT_PAGE_LOAD_TIMEOUT_EXCEPTION);
        chckIgnorePageLoadTimeoutException.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    private void addNumberVerification(Text txtInput, final int min, final int max) {
        if (txtInput == null || txtInput.isDisposed()) {
            return;
        }
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

    protected void registerListeners() {
        radioUsePageLoadTimeout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean usePageLoadTimeout = radioUsePageLoadTimeout.getSelection();
                txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
                chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
            }
        });
        addNumberVerification(txtActionDelay, TIMEOUT_MIN_VALUE, TIMEOUT_MAX_VALUE);
        addNumberVerification(txtDefaultIEHangTimeout, TIMEOUT_MIN_VALUE, TIMEOUT_MAX_VALUE);
        addNumberVerification(txtDefaultPageLoadTimeout, TIMEOUT_MIN_VALUE, TIMEOUT_MAX_VALUE);
    }

    private void initialize() throws IOException {
        Boolean usePageLoadTimeout = store.getEnablePageLoadTimeout();
        radioUsePageLoadTimeout.setSelection(usePageLoadTimeout);
        radioNotUsePageLoadTimeout.setSelection(!usePageLoadTimeout);
        txtDefaultPageLoadTimeout.setText(String.valueOf(store.getPageLoadTimeout()));
        txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
        chckIgnorePageLoadTimeoutException.setSelection(store.getIgnorePageLoadTimeout());
        chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
        txtActionDelay.setText(String.valueOf(store.getActionDelay()));
        txtDefaultIEHangTimeout.setText(Integer.toString(store.getIEHangTimeout()));
    }

    @Override
    protected void performDefaults() {
        if (container == null) {
            return;
        }
        radioUsePageLoadTimeout.setSelection(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT);
        Boolean usePageLoadTimeout = WebUiExecutionSettingStore.EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT;
        radioUsePageLoadTimeout.setSelection(usePageLoadTimeout);
        radioNotUsePageLoadTimeout.setSelection(!usePageLoadTimeout);
        txtDefaultPageLoadTimeout
                .setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT));
        txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
        chckIgnorePageLoadTimeoutException
                .setSelection(WebUiExecutionSettingStore.EXECUTION_DEFAULT_IGNORE_PAGELOAD_TIMEOUT_EXCEPTION);
        chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
        txtActionDelay.setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY));
        txtDefaultIEHangTimeout
                .setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING));
        try {
            store.setDefaultCapturedTestObjectLocators();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void performApply() {
        if (container == null) {
            return;
        }
        try {
            if (radioUsePageLoadTimeout != null) {
                store.setEnablePageLoadTimeout(radioUsePageLoadTimeout.getSelection());
            }
            if (txtDefaultPageLoadTimeout != null) {
                store.setPageLoadTimeout(Integer.parseInt(txtDefaultPageLoadTimeout.getText()));
            }
            if (chckIgnorePageLoadTimeoutException != null) {
                store.setIgnorePageLoadTimeout(chckIgnorePageLoadTimeoutException.getSelection());
            }
            if (txtActionDelay != null) {
                store.setActionDelay(Integer.parseInt(txtActionDelay.getText()));
            }
            if (txtDefaultIEHangTimeout != null) {
                store.setIEHangTimeout(Integer.parseInt(txtDefaultIEHangTimeout.getText()));
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        if (super.performOk() && isValid()) {
            performApply();
        }
        return true;
    }

}
