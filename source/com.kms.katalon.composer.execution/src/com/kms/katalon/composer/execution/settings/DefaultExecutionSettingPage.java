package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;

public class DefaultExecutionSettingPage extends PreferencePageWithHelp {

    private static final String LBL_DEFAULT_EXECUTION = ExecutionMessageConstants.LBL_DEFAULT_EXECUTION;
    
    // Smart XPath-related functionality - supported only in commercial version
    @SuppressWarnings("unused")
	private static final String LBL_APPLY_NEIGHBOR_XPATHS = ExecutionMessageConstants.LBL_APPLY_NEIGHBOR_XPATHS;

    public static final short TIMEOUT_MIN_VALUE = 0;

    public static final short TIMEOUT_MAX_VALUE = 9999;

    private static final int INPUT_WIDTH = 60;

    private ExecutionDefaultSettingStore store;

    private Composite container;

    private Combo cbDefaultBrowser;

    private Text txtDefaultElementTimeout;

    @SuppressWarnings("unused")
	private Button chckApplyNeighborXpaths, chckOpenReport, chckQuitDriversTestCase, chckQuitDriversTestSuite;

    private IRunConfigurationContributor[] runConfigs;

    private String selectedExecutionConfiguration;

    public DefaultExecutionSettingPage() {
        store = ExecutionDefaultSettingStore.getStore();
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        Composite comp = new Composite(container, SWT.NONE);
        GridLayout glComp = new GridLayout(2, false);
        glComp.verticalSpacing = 10;
        glComp.marginHeight = 0;
        glComp.marginWidth = 0;
        comp.setLayout(glComp);

        Label lblDefaultBrowser = new Label(comp, SWT.NONE);
        lblDefaultBrowser.setText(LBL_DEFAULT_EXECUTION);
        lblDefaultBrowser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        cbDefaultBrowser = new Combo(comp, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
        GridData gdCbDefaultBrowser = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbDefaultBrowser.widthHint = INPUT_WIDTH * 2;
        cbDefaultBrowser.setLayoutData(gdCbDefaultBrowser);

        Label lblDefaultElementTimeout = new Label(comp, SWT.NONE);
        lblDefaultElementTimeout.setText(StringConstants.PREF_LBL_DEFAULT_IMPLICIT_TIMEOUT);
        lblDefaultElementTimeout.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtDefaultElementTimeout = new Text(comp, SWT.BORDER);
        GridData gdTxtDefaultElementTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtDefaultElementTimeout.widthHint = INPUT_WIDTH;
        txtDefaultElementTimeout.setLayoutData(gdTxtDefaultElementTimeout);
        
        /*
         * 	// Smart XPath-related functionality - supported only in commercial version
         * 	Label lblApplyNeighborXpaths = new Label(comp, SWT.NONE);
	        lblApplyNeighborXpaths.setText(LBL_APPLY_NEIGHBOR_XPATHS);
	        lblApplyNeighborXpaths.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	        
	        chckApplyNeighborXpaths= new Button(comp, SWT.CHECK);
	        chckApplyNeighborXpaths.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
         */
        
        Group grpAfterExecuting = new Group(container, SWT.NONE);
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

        try {
            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }

        registerListeners();

        return container;
    }

    private void registerListeners() {
        addNumberVerification(txtDefaultElementTimeout, TIMEOUT_MIN_VALUE, TIMEOUT_MAX_VALUE);
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

    private void initialize() throws IOException {
        selectedExecutionConfiguration = store.getExecutionConfiguration();
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

        // Smart XPath-related functionality - supported only in commercial version
        // chckApplyNeighborXpaths.setSelection(store.isAutoApplyNeighborXpathsEnabled());
        
        txtDefaultElementTimeout.setText(Integer.toString(store.getElementTimeout()));
        chckOpenReport.setSelection(store.isPostExecOpenReport());
        chckQuitDriversTestCase.setSelection(store.isPostTestCaseExecQuitDriver());
        chckQuitDriversTestSuite.setSelection(store.isPostTestSuiteExecQuitDriver());
    }

    @Override
    protected void performDefaults() {
        if (container == null) {
            return;
        }
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
        txtDefaultElementTimeout
                .setText(Integer.toString(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_TIMEOUT_VALUE));
        chckOpenReport.setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);
        chckQuitDriversTestCase
                .setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE);
        chckQuitDriversTestSuite
                .setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE);
    }

    @Override
    protected void performApply() {
        if (container == null) {
            return;
        }
        try {
            if (cbDefaultBrowser != null && runConfigs != null && runConfigs.length > 0
                    && !StringUtils.equals(cbDefaultBrowser.getText(), selectedExecutionConfiguration)) {
                selectedExecutionConfiguration = cbDefaultBrowser.getText();
                store.setExecutionConfiguration(selectedExecutionConfiguration);
            }
            
            /*if (chckApplyNeighborXpaths != null) {
            	store.setApplyNeighborXpathsEnabled(chckApplyNeighborXpaths.getSelection());
            }*/
            
            if (txtDefaultElementTimeout != null) {
                store.setElementTimeout(Integer.parseInt(txtDefaultElementTimeout.getText()));
            }

            if (chckOpenReport != null) {
                store.setPostExecOpenReport(chckOpenReport.getSelection());
            }

            if (chckQuitDriversTestCase != null) {
                store.setPostTestCaseExecQuitDriver(chckQuitDriversTestCase.getSelection());
            }

            if (chckQuitDriversTestSuite != null) {
                store.setPostTestSuiteExecQuitDriver(chckQuitDriversTestSuite.getSelection());
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

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EXECUTION;
    }

}
