package com.kms.katalon.composer.webui.setting;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class WebUiSettingsPerferencePage extends PreferencePage {
    private WebUiExecutionSettingStore store;

    private Text txtDefaultPageLoadTimeout, txtActionDelay;

    private Composite fieldEditorParent;

    private Button radioNotUsePageLoadTimeout, radioUsePageLoadTimeout, chckIgnorePageLoadTimeoutException;
    
    public static final short PAGELOAD_TIMEOUT_MIN_VALUE = 0; 
    
    public static final short PAGELOAD_TIMEOUT_MAX_VALUE = 9999;

    public WebUiSettingsPerferencePage() {
        store = new WebUiExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
    }

    @Override
    protected Control createContents(Composite parent) {
        fieldEditorParent = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        fieldEditorParent.setLayout(layout);

        Group grpDefaultPageLoadTimeout = new Group(fieldEditorParent, SWT.NONE);
        grpDefaultPageLoadTimeout.setText(StringConstants.PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT);
        GridLayout glGrpDefaultPageLoadTimeout = new GridLayout(2, false);
        glGrpDefaultPageLoadTimeout.marginLeft = 15;
        grpDefaultPageLoadTimeout.setLayout(glGrpDefaultPageLoadTimeout);
        grpDefaultPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        radioNotUsePageLoadTimeout = new Button(grpDefaultPageLoadTimeout, SWT.RADIO);
        Label lblEnablePageLoadTImeout = new Label(grpDefaultPageLoadTimeout, SWT.NONE);
        lblEnablePageLoadTImeout.setText(StringConstants.PREF_LBL_ENABLE_DEFAULT_PAGE_LOAD_TIMEOUT);

        radioUsePageLoadTimeout = new Button(grpDefaultPageLoadTimeout, SWT.RADIO);
        txtDefaultPageLoadTimeout = new Text(grpDefaultPageLoadTimeout, SWT.BORDER);
        txtDefaultPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        new Label(grpDefaultPageLoadTimeout, SWT.NONE);
        chckIgnorePageLoadTimeoutException = new Button(grpDefaultPageLoadTimeout, SWT.CHECK);
        chckIgnorePageLoadTimeoutException.setText(StringConstants.PREF_LBL_IGNORE_DEFAULT_PAGE_LOAD_TIMEOUT_EXCEPTION);
        
        Composite cpActionDelay = new Composite(fieldEditorParent, SWT.NONE);
        cpActionDelay.setLayout(new GridLayout(2, false));
        cpActionDelay.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        
        Label lblActionDelay = new Label(cpActionDelay, SWT.NONE);
        lblActionDelay.setText(ComposerWebuiMessageConstants.LBL_ACTION_DELAY);
        
        txtActionDelay = new Text(cpActionDelay, SWT.BORDER);
        final GridData ldActionDelay = new GridData();
        ldActionDelay.widthHint = 30;
        txtActionDelay.setLayoutData(ldActionDelay);

        try {
            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        registerListeners();

        return fieldEditorParent;
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
        txtDefaultPageLoadTimeout.addModifyListener(new IntegerTextModifyListener(txtDefaultPageLoadTimeout));
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
    }

    @Override
    protected void performDefaults() {
        if (fieldEditorParent == null) {
            return;
        }
        radioUsePageLoadTimeout.setSelection(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT);
        Boolean usePageLoadTimeout = WebUiExecutionSettingStore.EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT;
        radioUsePageLoadTimeout.setSelection(usePageLoadTimeout);
        radioNotUsePageLoadTimeout.setSelection(!usePageLoadTimeout);
        txtDefaultPageLoadTimeout.setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT));
        txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
        chckIgnorePageLoadTimeoutException.setSelection(WebUiExecutionSettingStore.EXECUTION_DEFAULT_IGNORE_PAGELOAD_TIMEOUT_EXCEPTION);
        chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
        txtActionDelay.setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY));
    }

    @Override
    protected void performApply() {
        if (fieldEditorParent == null) {
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
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        boolean result = super.performOk();
        if (result && isValid()) {
            performApply();
        }
        return true;
    }

    class IntegerTextModifyListener implements ModifyListener {
        private Text integerText;

        public IntegerTextModifyListener(Text integerText) {
            this.integerText = integerText;
        }

        @Override
        public void modifyText(ModifyEvent e) {
            if (!isIntegerTextValid(integerText)) {
                setErrorMessage(MessageFormat.format(   
                        com.kms.katalon.composer.execution.constants.StringConstants.PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BETWEEN_X_Y,
                        PAGELOAD_TIMEOUT_MIN_VALUE,
                        PAGELOAD_TIMEOUT_MAX_VALUE));
                getApplyButton().setEnabled(false);
                return;
            }
            setErrorMessage(null);
            getApplyButton().setEnabled(true);
        }

        private boolean isIntegerTextValid(Text integerText) {
            if (integerText == null || integerText.getText() == null) {
                return true;
            }
            try {
                int value = Integer.parseInt(integerText.getText());
                return (value >= PAGELOAD_TIMEOUT_MIN_VALUE
                        && value <= PAGELOAD_TIMEOUT_MAX_VALUE);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    };
}
