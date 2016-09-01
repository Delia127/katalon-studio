package com.kms.katalon.composer.execution.preferences;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
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
import com.kms.katalon.composer.execution.util.ComposerExecutionUtil;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ExecutionPreferencePage extends PreferencePage {
    private Button chckNotifyMe, chckOpenReport, chckQuitDrivers;

    private Text txtDefaultTimeout;

    private Composite fieldEditorParent;

    private Combo executionOptionCombo;

    private IRunConfigurationContributor[] runConfigs;

    private String selectedExecutionConfiguration;

    public static final short PAGELOAD_TIMEOUT_MIN_VALUE = 0;

    public static final short PAGELOAD_TIMEOUT_MAX_VALUE = 9999;

    public ExecutionPreferencePage() {
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER));
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
        lblDefaultExecution.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));
        lblDefaultExecution.setText(StringConstants.PREF_GRP_DEFAULT_EXECUTION_CONFIG);

        executionOptionCombo = new Combo(defaultExecutionComposite, SWT.DROP_DOWN);
        executionOptionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDefaultTimeout = new Label(defaultExecutionComposite, SWT.NONE);
        lblDefaultTimeout.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));
        lblDefaultTimeout.setText(StringConstants.PREF_LBL_DEFAULT_IMPLICIT_TIMEOUT);

        txtDefaultTimeout = new Text(defaultExecutionComposite, SWT.BORDER);
        txtDefaultTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

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

        chckQuitDrivers = new Button(grpAfterExecuting, SWT.CHECK);
        chckQuitDrivers.setText(StringConstants.PREF_CHKBOX_QUIT_DRIVERS_AFTER_EXE_COMPLETELY);

        initialize();

        registerControlModifyListeners();

        return fieldEditorParent;
    }

    private void registerControlModifyListeners() {
        txtDefaultTimeout.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!isTextPageLoadTimeOutValid()) {
                    setErrorMessage(MessageFormat.format(StringConstants.PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BETWEEN_X_Y,
                            PAGELOAD_TIMEOUT_MIN_VALUE, PAGELOAD_TIMEOUT_MAX_VALUE));
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
                ExecutionPreferenceConstants.EXECUTION_NOTIFY_AFTER_EXECUTING));
        chckOpenReport.setSelection(getPreferenceStore().getBoolean(
                ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING));
        chckQuitDrivers.setSelection(getPreferenceStore().getBoolean(
                ExecutionPreferenceConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING));
        txtDefaultTimeout.setText(Integer.toString(getPreferenceStore().getInt(
                ExecutionPreferenceConstants.EXECUTION_DEFAULT_TIMEOUT)));
        selectedExecutionConfiguration = getPreferenceStore().getString(
                ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION);
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
        if (txtDefaultTimeout != null && txtDefaultTimeout.getText() != null) {
            try {
                int value = Integer.parseInt(txtDefaultTimeout.getText());
                if (value < PAGELOAD_TIMEOUT_MIN_VALUE || value > PAGELOAD_TIMEOUT_MAX_VALUE) {
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
                ExecutionPreferenceConstants.EXECUTION_NOTIFY_AFTER_EXECUTING));
        chckOpenReport.setSelection(getPreferenceStore().getDefaultBoolean(
                ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING));
        chckQuitDrivers.setSelection(getPreferenceStore().getDefaultBoolean(
                ExecutionPreferenceConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING));
        txtDefaultTimeout.setText(Integer.toString(getPreferenceStore().getDefaultInt(
                ExecutionPreferenceConstants.EXECUTION_DEFAULT_TIMEOUT)));
        String selectedExecutionConfiguration = getPreferenceStore().getDefaultString(
                ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION);
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
            getPreferenceStore().setValue(ExecutionPreferenceConstants.EXECUTION_NOTIFY_AFTER_EXECUTING,
                    chckNotifyMe.getSelection());
        }

        if (chckOpenReport != null) {
            getPreferenceStore().setValue(ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING,
                    chckOpenReport.getSelection());
        }

        if (chckQuitDrivers != null) {
            getPreferenceStore().setValue(ExecutionPreferenceConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING,
                    chckQuitDrivers.getSelection());
        }

        if (txtDefaultTimeout != null) {
            getPreferenceStore().setValue(ExecutionPreferenceConstants.EXECUTION_DEFAULT_TIMEOUT,
                    Integer.parseInt(txtDefaultTimeout.getText()));
        }

        if (executionOptionCombo != null && runConfigs != null && runConfigs.length > 0
                && !StringUtils.equals(executionOptionCombo.getText(), selectedExecutionConfiguration)) {
            selectedExecutionConfiguration = executionOptionCombo.getText();
            getPreferenceStore().setValue(ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION,
                    selectedExecutionConfiguration);
            ComposerExecutionUtil.updateDefaultLabelForRunDropDownItem(executionOptionCombo.getText());
        }
    }

    public boolean performOk() {
        if (isValid()) {
            performApply();
        }
        return super.performOk();
    }
}
