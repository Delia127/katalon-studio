package com.kms.katalon.composer.report.dialog;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.preference.ReportPreferenceInitializer;

public class AdvancedSearchTestLogDialog extends AbstractDialog {

    private Composite container;
    private Button chckIncludeChildLog;

    public AdvancedSearchTestLogDialog(Shell parentShell) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_ADVANCED_SEARCH);
    }

    @Override
    protected void registerControlModifyListeners() {

    }

    @Override
    protected void setInput() {
        chckIncludeChildLog.setSelection(ReportPreferenceInitializer.isChildLogForFirstMatchIncluded());
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        chckIncludeChildLog = new Button(container, SWT.CHECK);
        chckIncludeChildLog.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        chckIncludeChildLog.setText(StringConstants.DIA_LBL_INCLUDE_CHILD_LOGS);

        return container;
    }

    protected void okPressed() {
        try {
            ReportPreferenceInitializer.includeChildLogForSearching(chckIncludeChildLog.getSelection());
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_ERROR_MSG_UNABLE_TO_UPDATE_ADVANCED_SEARCH,
                    e.getMessage());
            return;
        }
        super.okPressed();
    }
}
