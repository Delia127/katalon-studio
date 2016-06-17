package com.kms.katalon.composer.report.preference;

import java.io.IOException;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.setting.ExecutionSettingStore;

public class ReportSettingPage extends PreferencePage {
    private Composite container;

    private ExecutionSettingStore store;

    private boolean modified;

    private Button btnCheckButton;

    public ReportSettingPage() {
        store = new ExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setLayout(new GridLayout(1, false));

        btnCheckButton = new Button(container, SWT.CHECK);
        btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnCheckButton.setText(StringConstants.PAGE_TXT_ENABLE_TAKE_SCREENSHOT);

        registerControlModifyListeners();

        updateInput();

        return container;
    }

    private void registerControlModifyListeners() {
        btnCheckButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                modified = true;
            }
        });
    }

    private void updateInput() {
        modified = false;
        try {
            btnCheckButton.setSelection(store.getScreenCaptureOption());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_READ_SETTINGS,
                    e.getMessage());
        }
    }

    protected void performDefaults() {
        updateInput();
        super.performApply();
    }

    @Override
    public boolean performOk() {
        if (container == null || container.isDisposed() || !modified) {
            return true;
        }
        try {
            store.setScreenCaptureOption(btnCheckButton.getSelection());
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_UPDATE_SETTINGS,
                    e.getMessage());
            return false;
        }
    }
}
