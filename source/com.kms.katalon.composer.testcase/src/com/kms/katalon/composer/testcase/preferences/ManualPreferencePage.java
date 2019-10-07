package com.kms.katalon.composer.testcase.preferences;

import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.defaultStore;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.enableLineWrapping;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.getMaximumLineWidth;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.isLineWrappingEnabled;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.setMaximumLineWidth;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.updateStore;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;

public class ManualPreferencePage extends PreferencePage {
    public ManualPreferencePage() {
    }

    private Composite container;

    private Text txtMaximumLineWidth;

    private Button btnAllowLineWrapping;

    private Composite cpsWrappingLineWidth;

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setLayout(new GridLayout(1, false));

        Group grpLineWrappingSettings = new Group(container, SWT.NONE);
        grpLineWrappingSettings.setText(StringConstants.PREF_MANUAL_GRP_LINE_WRAPPING);
        grpLineWrappingSettings.setLayout(new GridLayout(1, false));
        grpLineWrappingSettings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        btnAllowLineWrapping = new Button(grpLineWrappingSettings, SWT.CHECK);
        btnAllowLineWrapping.setText(StringConstants.PREF_MANUAL_BTN_ENABLE_LINE_WRAPPING);

        cpsWrappingLineWidth = new Composite(grpLineWrappingSettings, SWT.NONE);
        GridData gdCpsWrappingLineWidth = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdCpsWrappingLineWidth.horizontalIndent = 10;
        cpsWrappingLineWidth.setLayoutData(gdCpsWrappingLineWidth);
        GridLayout glCpsWrappingLineWidth = new GridLayout(2, false);
        glCpsWrappingLineWidth.marginHeight = 0;
        glCpsWrappingLineWidth.marginWidth = 0;
        cpsWrappingLineWidth.setLayout(glCpsWrappingLineWidth);

        Label lblNewLabel = new Label(cpsWrappingLineWidth, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel.setText(StringConstants.PREF_MANUAL_LBL_LINE_WIDTH);

        txtMaximumLineWidth = new Text(cpsWrappingLineWidth, SWT.BORDER);
        GridData gdTxtMaximumLineWidth = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdTxtMaximumLineWidth.widthHint = 50;
        txtMaximumLineWidth.setLayoutData(gdTxtMaximumLineWidth);

        registerControlModifyListeners();
        updateInput();

        return container;
    }

    private void registerControlModifyListeners() {
        btnAllowLineWrapping.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableWrappingLineComposite(btnAllowLineWrapping.getSelection());
            }
        });

        // Prevent user enter invalid line width
        txtMaximumLineWidth.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent e) {
                final String oldS = txtMaximumLineWidth.getText();
                final String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
                if (StringUtils.isEmpty(newS)) {
                    return;
                }
                e.doit = isPositive(newS);
            }
            
            private boolean isPositive(String s) {
                try {
                    return Integer.parseInt(s) >= 1;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
        });

        txtMaximumLineWidth.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String lineWidth = txtMaximumLineWidth.getText();
                if (StringUtils.isEmpty(lineWidth)) {
                    txtMaximumLineWidth.setText(Integer.toString(getMaximumLineWidth()));
                }
            }
        });
    }

    private void enableWrappingLineComposite(boolean enabled) {
        ControlUtils.recursiveSetEnabled(cpsWrappingLineWidth, enabled);
    }

    private void checkButtonAndNotifyToListener(Button btn, boolean selected) {
        btn.setSelection(selected);
        btn.notifyListeners(SWT.Selection, new Event());
    }

    private void updateInput() {
        checkButtonAndNotifyToListener(btnAllowLineWrapping, isLineWrappingEnabled());

        txtMaximumLineWidth.setText(Integer.toString(getMaximumLineWidth()));
    }

    @Override
    protected void performDefaults() {
        if (isNotAbleToUpdate()) {
            return;
        }
        defaultStore();
        updateInput();
    }

    @Override
    public boolean performOk() {
        try {
            if (isNotAbleToUpdate()) {
                return true;
            }

            enableLineWrapping(btnAllowLineWrapping.getSelection());
            setMaximumLineWidth(Integer.valueOf(txtMaximumLineWidth.getText()));

            updateStore();
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PREF_MANUAL_MSG_UNABLE_TO_UPDATE, e.getMessage());
            LoggerSingleton.logError(e);
            return false;
        }
    }

    private boolean isNotAbleToUpdate() {
        return container == null || container.isDisposed();
    }
}
