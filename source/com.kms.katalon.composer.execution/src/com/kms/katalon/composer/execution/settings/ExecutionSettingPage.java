package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public class ExecutionSettingPage extends AbstractExecutionSettingPage {

    private static final String LBL_DEFAULT_EXECUTION = ExecutionMessageConstants.LBL_DEFAULT_EXECUTION;

    private ExecutionDefaultSettingStore defaultSettingStore;

    private Combo cbDefaultBrowser;

    private Combo cbLogTestSteps;

    private Text txtDefaultElementTimeout;

    private Button chckOpenReport, chckQuitDriversTestCase, chckQuitDriversTestSuite;

    private IRunConfigurationContributor[] runConfigs;

    private String selectedExecutionConfiguration;

    private GridData gdCbLogTestSteps;
    
    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    public ExecutionSettingPage() {
        defaultSettingStore = ExecutionDefaultSettingStore.getStore();
    }

    @Override
    protected Composite createSettingsArea(Composite container) {
        createDefaultSetting(container);
        return container;
    }

    private void createDefaultSetting(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout glComp = new GridLayout(2, false);
        glComp.verticalSpacing = 10;
        glComp.marginHeight = 0;
        glComp.marginWidth = 0;
        comp.setLayout(glComp);

        Label lblDefaultBrowser = new Label(comp, SWT.NONE);
        lblDefaultBrowser.setText(LBL_DEFAULT_EXECUTION);
        GridData gdLblDefaultBrowser = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblDefaultBrowser.setLayoutData(gdLblDefaultBrowser);

        cbDefaultBrowser = new Combo(comp, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
        GridData gdCbDefaultBrowser = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbDefaultBrowser.widthHint = INPUT_WIDTH * 2;
        cbDefaultBrowser.setLayoutData(gdCbDefaultBrowser);

        Label lblLogTestSteps = new Label(comp, SWT.NONE);
        lblLogTestSteps.setText(StringConstants.PREF_LBL_LOG_TEST_STEPS);
        GridData gdLblLogTestSteps = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblLogTestSteps.setLayoutData(gdLblLogTestSteps);

        cbLogTestSteps = new Combo(comp, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
        gdCbLogTestSteps = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbLogTestSteps.widthHint = INPUT_WIDTH * 2;
        cbLogTestSteps.setLayoutData(gdCbLogTestSteps);

        Label lblDefaultElementTimeout = new Label(comp, SWT.NONE);
        lblDefaultElementTimeout.setText(StringConstants.PREF_LBL_DEFAULT_IMPLICIT_TIMEOUT);
        GridData gdLblDefaultElementTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblDefaultElementTimeout.setLayoutData(gdLblDefaultElementTimeout);

        txtDefaultElementTimeout = new Text(comp, SWT.BORDER);
        GridData gdTxtDefaultElementTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtDefaultElementTimeout.widthHint = INPUT_WIDTH;
        txtDefaultElementTimeout.setLayoutData(gdTxtDefaultElementTimeout);

        Group grpAfterExecuting = new Group(parent, SWT.NONE);
        grpAfterExecuting.setText(StringConstants.PREF_GRP_POST_EXECUTION_OPTIONS);
        GridLayout glGrpAfterExecuting = new GridLayout();
        glGrpAfterExecuting.marginLeft = 15;
        grpAfterExecuting.setLayout(glGrpAfterExecuting);
        grpAfterExecuting.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        chckOpenReport = new Button(grpAfterExecuting, SWT.CHECK);
        chckOpenReport.setText(StringConstants.PREF_CHKBOX_OPEN_RPT_AFTER_EXE_COMPLETELY);

        chckQuitDriversTestCase = new Button(grpAfterExecuting, SWT.CHECK);
        chckQuitDriversTestCase.setText(StringConstants.PREF_CHKBOX_QUIT_DRIVERS_AFTER_EXE_COMPLETELY);

        chckQuitDriversTestSuite = new Button(grpAfterExecuting, SWT.CHECK);
        chckQuitDriversTestSuite
                .setText(ComposerExecutionMessageConstants.PREF_CHKBOX_QUIT_DRIVERS_AFTER_EXE_TEST_SUITE_COMPLETELY);
    }

    @Override
    protected void registerListeners() {
        cbLogTestSteps.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!featureService.canUse(KSEFeature.CONSOLE_LOG_CUSTOMIZATION)) {
                    KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.CONSOLE_LOG_CUSTOMIZATION);
                    cbLogTestSteps.select(0); // Free users cannot disable this setting
                }
            }
        });

        addNumberVerification(txtDefaultElementTimeout, TIMEOUT_MIN_VALUE_IN_SEC, TIMEOUT_MAX_VALUE_IN_SEC);
    }

    @Override
    protected void initialize() throws IOException {
        selectedExecutionConfiguration = defaultSettingStore.getExecutionConfiguration();
        runConfigs = RunConfigurationCollector.getInstance().getAllBuiltinRunConfigurationContributors();
        if (runConfigs.length > 0) {
            List<String> runConfigIds = Arrays.stream(runConfigs)
                    .map(config -> config.getId())
                    .collect(Collectors.toList());
            int selectedIndex = Math.max(runConfigIds.indexOf(selectedExecutionConfiguration), 0);
            String[] runConfigIdList = runConfigIds.toArray(new String[0]);
            cbDefaultBrowser.setItems(runConfigIdList);
            cbDefaultBrowser.select(selectedIndex);
        }

        Boolean selectedLogTestSteps = defaultSettingStore.getLogTestSteps();
        cbLogTestSteps.setItems(new String[] { "Enable", "Disable" });
        cbLogTestSteps.select(selectedLogTestSteps.booleanValue() ? 0 : 1);
        if (!featureService.canUse(KSEFeature.CONSOLE_LOG_CUSTOMIZATION)) {
            cbLogTestSteps.select(0); // Enable by default for free user
        }

        txtDefaultElementTimeout.setText(Integer.toString(defaultSettingStore.getElementTimeout()));

        chckOpenReport.setSelection(defaultSettingStore.isPostExecOpenReport());
        chckQuitDriversTestCase.setSelection(defaultSettingStore.isPostTestCaseExecQuitDriver());
        chckQuitDriversTestSuite.setSelection(defaultSettingStore.isPostTestSuiteExecQuitDriver());

        // if (!LicenseUtil.isNotFreeLicense()) {
        // gdCbLogTestSteps.heightHint = 0;
        // container.layout(true);
        // }
    }

    @Override
    protected void performDefaults() {
        String selectedExecutionConfiguration = ExecutionDefaultSettingStore.getStore()
                .getDefaultExecutionConfiguration();
        runConfigs = RunConfigurationCollector.getInstance().getAllBuiltinRunConfigurationContributors();
        if (runConfigs.length > 0) {
            List<String> runConfigIds = Arrays.stream(runConfigs)
                    .map(config -> config.getId())
                    .collect(Collectors.toList());
            int selectedIndex = Math.max(runConfigIds.indexOf(selectedExecutionConfiguration), 0);
            String[] runConfigIdList = runConfigIds.toArray(new String[0]);
            cbDefaultBrowser.setItems(runConfigIdList);
            cbDefaultBrowser.select(selectedIndex);
        }

        cbLogTestSteps.setItems(new String[] { "Enable", "Disable" });
        cbLogTestSteps.select(0);

        txtDefaultElementTimeout
                .setText(Integer.toString(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_TIMEOUT_VALUE));
        chckOpenReport.setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);
        chckQuitDriversTestCase
                .setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE);
        chckQuitDriversTestSuite
                .setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE);
    }

    @Override
    protected boolean saveSettings() {
        try {
            if (cbDefaultBrowser != null && runConfigs != null && runConfigs.length > 0
                    && !StringUtils.equals(cbDefaultBrowser.getText(), selectedExecutionConfiguration)) {
                selectedExecutionConfiguration = cbDefaultBrowser.getText();
                defaultSettingStore.setExecutionConfiguration(selectedExecutionConfiguration);
            }

            if (cbLogTestSteps != null) {
                defaultSettingStore.setLogTestSteps(
                        cbLogTestSteps.getSelectionIndex() == 0 ? Boolean.valueOf(true) : Boolean.valueOf(false));
            }

            if (txtDefaultElementTimeout != null) {
                defaultSettingStore.setElementTimeout(Integer.parseInt(txtDefaultElementTimeout.getText()));
            }

            if (chckOpenReport != null) {
                defaultSettingStore.setPostExecOpenReport(chckOpenReport.getSelection());
            }

            if (chckQuitDriversTestCase != null) {
                defaultSettingStore.setPostTestCaseExecQuitDriver(chckQuitDriversTestCase.getSelection());
            }

            if (chckQuitDriversTestSuite != null) {
                defaultSettingStore.setPostTestSuiteExecQuitDriver(chckQuitDriversTestSuite.getSelection());
            }
        } catch (IOException error) {
            LoggerSingleton.logError(error);
            return false;
        }

        return true;
    }
}
