package com.kms.katalon.composer.execution.preferences;

import java.text.MessageFormat;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.TestEnvironmentController;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class ExecutionPreferencePage extends PreferencePage {
    private Button chckNotifyMe, chckOpenReport;
    private Text txtPageLoadTimeout;
    private Composite fieldEditorParent;
    private Combo executionOptionCombo;
    private IRunConfigurationContributor[] runConfigs;

    public ExecutionPreferencePage() {
    }

    @Override
    protected Control createContents(Composite parent) {
        fieldEditorParent = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        fieldEditorParent.setLayout(layout);

        Composite defaultExecutionComposite = new Composite(fieldEditorParent, SWT.NONE);
        GridLayout defaultExecutionCompositeGridLayout = new GridLayout(2, false);
        defaultExecutionComposite.setLayout(defaultExecutionCompositeGridLayout);
        defaultExecutionComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblDefaultExecution = new Label(defaultExecutionComposite, SWT.NONE);
        lblDefaultExecution.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDefaultExecution.setText(StringConstants.PREF_GRP_DEFAULT_EXECUTION_CONFIG);

        executionOptionCombo = new Combo(defaultExecutionComposite, SWT.DROP_DOWN);

        Composite pageLoadTimeOutComposite = new Composite(fieldEditorParent, SWT.NONE);
        GridLayout glComposite = new GridLayout(2, false);
        pageLoadTimeOutComposite.setLayout(glComposite);
        pageLoadTimeOutComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblDefaultTimeout = new Label(pageLoadTimeOutComposite, SWT.NONE);
        lblDefaultTimeout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDefaultTimeout.setText(StringConstants.PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT);

        txtPageLoadTimeout = new Text(pageLoadTimeOutComposite, SWT.BORDER);
        txtPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Group grpAfterExecuting = new Group(fieldEditorParent, SWT.NONE);
        grpAfterExecuting.setText(StringConstants.PREF_GRP_POST_EXECUTION_OPTIONS);
        GridLayout glGrpAfterExecuting = new GridLayout(1, false);
        glGrpAfterExecuting.marginLeft = 15;
        grpAfterExecuting.setLayout(glGrpAfterExecuting);
        grpAfterExecuting.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        chckNotifyMe = new Button(grpAfterExecuting, SWT.CHECK);
        chckNotifyMe.setText(StringConstants.PREF_CHKBOX_NOTIFY_ME_AFTER_EXE_COMPLETELY);

        chckOpenReport = new Button(grpAfterExecuting, SWT.CHECK);
        chckOpenReport.setText(StringConstants.PREF_CHKBOX_OPEN_RPT_AFTER_EXE_COMPLETELY);

        initialize();

        registerControlModifyListeners();

        return fieldEditorParent;
    }

    private void registerControlModifyListeners() {
        txtPageLoadTimeout.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!isTextPageLoadTimeOutValid()) {
                    setErrorMessage(MessageFormat.format(StringConstants.PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BETWEEN_X_Y,
                            TestEnvironmentController.getInstance().getPageLoadTimeOutMinimumValue(),
                            TestEnvironmentController.getInstance().getPageLoadTimeOutMaximumValue()));
                    getApplyButton().setEnabled(false);
                } else {
                    setErrorMessage(null);
                    getApplyButton().setEnabled(true);
                }
            }
        });
    }

    private void initialize() {
        chckNotifyMe.setSelection(getPreferenceStore().getBoolean(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_NOTIFY_AFTER_EXECUTING));
        chckOpenReport.setSelection(getPreferenceStore().getBoolean(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_OPEN_REPORT_AFTER_EXECUTING));
        txtPageLoadTimeout.setText(Integer.toString(getPreferenceStore().getInt(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_TIMEOUT)));
        String selectedExecutionConfiguration = getPreferenceStore().getString(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_CONFIGURATION);
        runConfigs = RunConfigurationCollector.getInstance().getAllBuiltinRunConfigurationContributors();
        String[] runConfigIdList = new String[runConfigs.length];
        int selectedIndex = 0;
        for (int i = 0; i < runConfigs.length; i++) {
            runConfigIdList[i] = runConfigs[i].getId();
            if (runConfigIdList[i].equals(selectedExecutionConfiguration)) {
                selectedIndex = i;
            }
        }
        executionOptionCombo.setItems(runConfigIdList);
        executionOptionCombo.select(selectedIndex);
    }

    private boolean isTextPageLoadTimeOutValid() {
        if (txtPageLoadTimeout != null && txtPageLoadTimeout.getText() != null) {
            try {
                int value = Integer.parseInt(txtPageLoadTimeout.getText());
                if (value < TestEnvironmentController.getInstance().getPageLoadTimeOutMinimumValue()
                        || value > TestEnvironmentController.getInstance().getPageLoadTimeOutMaximumValue()) {
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void performDefaults() {
        if (fieldEditorParent == null) {
            return;
        }

        chckNotifyMe.setSelection(getPreferenceStore().getDefaultBoolean(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_NOTIFY_AFTER_EXECUTING));
        chckOpenReport.setSelection(getPreferenceStore().getDefaultBoolean(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_OPEN_REPORT_AFTER_EXECUTING));
        txtPageLoadTimeout.setText(Integer.toString(getPreferenceStore().getDefaultInt(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_TIMEOUT)));
        String selectedExecutionConfiguration = getPreferenceStore().getDefaultString(
                PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_CONFIGURATION);
        runConfigs = RunConfigurationCollector.getInstance().getAllBuiltinRunConfigurationContributors();
        int selectedIndex = 0;
        if (runConfigs.length > 0) {
            String[] runConfigIdList = new String[runConfigs.length];
            for (int i = 0; i < runConfigs.length; i++) {
                runConfigIdList[i] = runConfigs[i].getId();
                if (runConfigIdList[i].equals(selectedExecutionConfiguration)) {
                    selectedIndex = i;
                }
            }
            executionOptionCombo.setItems(runConfigIdList);
            executionOptionCombo.select(selectedIndex);
        }
        super.performDefaults();
    }

    @Override
    public boolean isValid() {
        return isTextPageLoadTimeOutValid();
    }

    @Override
    protected void performApply() {
        if (fieldEditorParent == null) {
            return;
        }

        if (chckNotifyMe != null) {
            getPreferenceStore().setValue(
                    PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_NOTIFY_AFTER_EXECUTING,
                    chckNotifyMe.getSelection());
        }

        if (chckOpenReport != null) {
            getPreferenceStore().setValue(
                    PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_OPEN_REPORT_AFTER_EXECUTING,
                    chckOpenReport.getSelection());
        }

        if (txtPageLoadTimeout != null) {
            getPreferenceStore().setValue(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_TIMEOUT,
                    Integer.parseInt(txtPageLoadTimeout.getText()));
        }

        if (executionOptionCombo != null && runConfigs != null && runConfigs.length > 0) {
            getPreferenceStore().setValue(
                    PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_CONFIGURATION,
                    executionOptionCombo.getText());
        }
    }

    public boolean performOk() {
        boolean result = super.performOk();
        if (result) {
            if (isValid()) {
                performApply();
            }
        }
        return true;
    }
}
