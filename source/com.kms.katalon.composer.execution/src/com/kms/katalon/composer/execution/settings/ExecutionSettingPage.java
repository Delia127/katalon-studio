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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;

public class ExecutionSettingPage extends PreferencePageWithHelp {
    private static final String LBL_DEFAULT_EXECUTION = ExecutionMessageConstants.LBL_DEFAULT_EXECUTION;
    
    // Smart XPath-related functionality - supported only in commercial version
    @SuppressWarnings("unused")
	private static final String LBL_APPLY_NEIGHBOR_XPATHS = ExecutionMessageConstants.LBL_APPLY_NEIGHBOR_XPATHS;

    public static final short TIMEOUT_MIN_VALUE = 0;

    public static final short TIMEOUT_MAX_VALUE = 9999;

    private static final int INPUT_WIDTH = 60;

    private ExecutionDefaultSettingStore defaultSettingStore;
    
    private WebUiExecutionSettingStore webSettingStore;

    private Composite container;

    private Combo cbDefaultBrowser;
    
    private Combo cbDefaultSmartWait;

    private Combo cbLogTestSteps;
    
    private Text txtDefaultElementTimeout, txtDefaultPageLoadTimeout, txtActionDelay, txtDefaultIEHangTimeout;

    @SuppressWarnings("unused")
	private Button chckApplyNeighborXpaths, chckOpenReport, chckQuitDriversTestCase, chckQuitDriversTestSuite;
    
    private Button radioNotUsePageLoadTimeout, radioUsePageLoadTimeout, chckIgnorePageLoadTimeoutException;

    private IRunConfigurationContributor[] runConfigs;

    private String selectedExecutionConfiguration;

    public ExecutionSettingPage() {
        defaultSettingStore = ExecutionDefaultSettingStore.getStore();
        webSettingStore = new WebUiExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
    }
    
    @Override
    protected Control createContents(Composite parent) {
        createLayout(parent);
        
        try {
            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }

        registerListeners();
        
        return container;
    }
    
    private void createLayout(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);
        createDefaultSetting(container);
        createTimeoutSettings(container);
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
        
        Label lblDefaultSmartWait = new Label(comp, SWT.NONE);
        lblDefaultSmartWait.setText("Default Smart Wait");
        GridData gdLblDefaultSmartWait = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblDefaultSmartWait.setLayoutData(gdLblDefaultSmartWait);

        cbDefaultSmartWait = new Combo(comp, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
        GridData gdCbDefaultSmartWait = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbDefaultSmartWait.widthHint = INPUT_WIDTH * 2;
        cbDefaultSmartWait.setLayoutData(gdCbDefaultSmartWait);

        Label lblDefaultElementTimeout = new Label(comp, SWT.NONE);
        lblDefaultElementTimeout.setText(StringConstants.PREF_LBL_DEFAULT_IMPLICIT_TIMEOUT);
        GridData gdLblDefaultElementTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblDefaultElementTimeout.setLayoutData(gdLblDefaultElementTimeout);
        
        txtDefaultElementTimeout = new Text(comp, SWT.BORDER);
        GridData gdTxtDefaultElementTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtDefaultElementTimeout.widthHint = INPUT_WIDTH;
        txtDefaultElementTimeout.setLayoutData(gdTxtDefaultElementTimeout);
        
        Label lblLogTestSteps = new Label(comp, SWT.NONE);
        lblLogTestSteps.setText(StringConstants.PREF_LBL_LOG_TEST_STEPS);
        GridData gdLblLogTestSteps = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblLogTestSteps.setLayoutData(gdLblLogTestSteps);
        
        cbLogTestSteps = new Combo(comp, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
        GridData gdCbLogTestSteps = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbLogTestSteps.widthHint = INPUT_WIDTH * 2;
        cbLogTestSteps.setLayoutData(gdCbLogTestSteps);
        
        /* 	// Smart XPath's related functionality - only supported in commercial ver
         * 	Label lblApplyNeighborXpaths = new Label(comp, SWT.NONE);
	        lblApplyNeighborXpaths.setText(LBL_APPLY_NEIGHBOR_XPATHS);
	        GridData gdLblApplyNeighborXpaths = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	        lblApplyNeighborXpaths.setLayoutData(gdLblApplyNeighborXpaths);
	        
	        chckApplyNeighborXpaths= new Button(comp, SWT.CHECK);
	        GridData gdChckApplyNeighborXpaths = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	        chckApplyNeighborXpaths.setLayoutData(gdChckApplyNeighborXpaths);
         */
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
    
    private void createTimeoutSettings(Composite parent) {
        Group comp = new Group(parent, SWT.NONE);
        comp.setText("Web UI");
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        comp.setLayout(layout);
        
        Label lblActionDelay = new Label(comp, SWT.NONE);
        lblActionDelay.setText(ComposerExecutionMessageConstants.LBL_ACTION_DELAY);
        GridData gdLblActionDelay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblActionDelay.setLayoutData(gdLblActionDelay);

        txtActionDelay = new Text(comp, SWT.BORDER);
        GridData ldActionDelay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        ldActionDelay.widthHint = INPUT_WIDTH;
        txtActionDelay.setLayoutData(ldActionDelay);

        Label lblDefaultIEHangTimeout = new Label(comp, SWT.NONE);
        lblDefaultIEHangTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT);
        lblDefaultIEHangTimeout.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtDefaultIEHangTimeout = new Text(comp, SWT.BORDER);
        GridData gdTxtDefaultIEHangTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtDefaultIEHangTimeout.widthHint = INPUT_WIDTH;
        txtDefaultIEHangTimeout.setLayoutData(gdTxtDefaultIEHangTimeout);

        Label lblDefaultPageLoadTimeout = new Label(comp, SWT.NONE);
        lblDefaultPageLoadTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT);
        lblDefaultPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Composite compPageLoad = new Composite(comp, SWT.NONE);
        GridLayout glCompPageLoad = new GridLayout(2, false);
        glCompPageLoad.marginWidth = 0;
        glCompPageLoad.marginHeight = 0;
        glCompPageLoad.marginLeft = 15;
        compPageLoad.setLayout(glCompPageLoad);
        compPageLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        radioNotUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioNotUsePageLoadTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_ENABLE_DEFAULT_PAGE_LOAD_TIMEOUT);
        radioNotUsePageLoadTimeout.setText("Wait until the page is loaded");
        radioNotUsePageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        radioUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioUsePageLoadTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_CUSTOM_PAGE_LOAD_TIMEOUT);
        radioUsePageLoadTimeout.setText("Wait for (in seconds)");
        GridData gdRadioPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        radioUsePageLoadTimeout.setLayoutData(gdRadioPageLoadTimeout);

        txtDefaultPageLoadTimeout = new Text(compPageLoad, SWT.BORDER);
        GridData gdDefaultPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdDefaultPageLoadTimeout.widthHint = INPUT_WIDTH;
        txtDefaultPageLoadTimeout.setLayoutData(gdDefaultPageLoadTimeout);

        new Label(compPageLoad, SWT.NONE);
        chckIgnorePageLoadTimeoutException = new Button(compPageLoad, SWT.CHECK);
        chckIgnorePageLoadTimeoutException.setText(ComposerExecutionMessageConstants.PREF_LBL_IGNORE_DEFAULT_PAGE_LOAD_TIMEOUT_EXCEPTION);
        chckIgnorePageLoadTimeoutException.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }
    
    private void registerListeners() {
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
        
        Boolean selectedSmartWaitMode = defaultSettingStore.getDefaultSmartWaitMode();
        cbDefaultSmartWait.setItems(new String[] { "Enable", "Disable" });
        cbDefaultSmartWait.select(selectedSmartWaitMode.booleanValue() ? 0 : 1);
        
        Boolean selectedLogTestSteps = defaultSettingStore.getLogTestSteps();
        cbLogTestSteps.setItems(new String[] { "Enable", "Disable" });
        cbLogTestSteps.select(selectedLogTestSteps.booleanValue() ? 0 : 1);
        
        txtDefaultElementTimeout.setText(Integer.toString(defaultSettingStore.getElementTimeout()));
        
		/*		
		 * Smart XPath-related functionality - only supported in commercialized version        
		   chckApplyNeighborXpaths.setSelection(defaultSettingStore.isAutoApplyNeighborXpathsEnabled());        
		*/
        chckOpenReport.setSelection(defaultSettingStore.isPostExecOpenReport());
        chckQuitDriversTestCase.setSelection(defaultSettingStore.isPostTestCaseExecQuitDriver());
        chckQuitDriversTestSuite.setSelection(defaultSettingStore.isPostTestSuiteExecQuitDriver());
        
        Boolean usePageLoadTimeout = webSettingStore.getEnablePageLoadTimeout();
        radioUsePageLoadTimeout.setSelection(usePageLoadTimeout);
        radioNotUsePageLoadTimeout.setSelection(!usePageLoadTimeout);
        txtDefaultPageLoadTimeout.setText(String.valueOf(webSettingStore.getPageLoadTimeout()));
        txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
        chckIgnorePageLoadTimeoutException.setSelection(webSettingStore.getIgnorePageLoadTimeout());
        chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
        txtActionDelay.setText(String.valueOf(webSettingStore.getActionDelay()));
        txtDefaultIEHangTimeout.setText(Integer.toString(webSettingStore.getIEHangTimeout()));
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
        
        cbDefaultSmartWait.setItems(new String[] { "Enable", "Disable" });
        cbDefaultSmartWait.select(0);
        
        cbLogTestSteps.setItems(new String[] { "Enable", "Disable" });
        cbLogTestSteps.select(0);
        
        txtDefaultElementTimeout
                .setText(Integer.toString(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_TIMEOUT_VALUE));
        chckOpenReport.setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);
        chckQuitDriversTestCase
                .setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE);
        chckQuitDriversTestSuite
                .setSelection(ExecutionDefaultSettingStore.EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE);
        
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
            webSettingStore.setDefaultCapturedTestObjectAttributeLocators();
            webSettingStore.setDefaultCapturedTestObjectXpathLocators();
            webSettingStore.setDefaultCapturedTestObjectSelectorMethods();
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
            if (cbDefaultBrowser != null && runConfigs != null && runConfigs.length > 0
                    && !StringUtils.equals(cbDefaultBrowser.getText(), selectedExecutionConfiguration)) {
                selectedExecutionConfiguration = cbDefaultBrowser.getText();
                defaultSettingStore.setExecutionConfiguration(selectedExecutionConfiguration);
            }
            
            if (cbDefaultSmartWait != null) {
                defaultSettingStore.setDefaultSmartWaitMode(
                        cbDefaultSmartWait.getSelectionIndex() == 0 ? Boolean.valueOf(true) : Boolean.valueOf(false));
            }
            
            if (cbLogTestSteps != null) {
                defaultSettingStore.setLogTestSteps(
                        cbLogTestSteps.getSelectionIndex() == 0 ? Boolean.valueOf(true) : Boolean.valueOf(false));
            }
            
            /* 
            if (chckApplyNeighborXpaths != null) {
                defaultSettingStore.setApplyNeighborXpathsEnabled(chckApplyNeighborXpaths.getSelection());
            }*/
            
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
            
            if (radioUsePageLoadTimeout != null) {
                webSettingStore.setEnablePageLoadTimeout(radioUsePageLoadTimeout.getSelection());
            }
            
            if (txtDefaultPageLoadTimeout != null) {
                webSettingStore.setPageLoadTimeout(Integer.parseInt(txtDefaultPageLoadTimeout.getText()));
            }
            
            if (chckIgnorePageLoadTimeoutException != null) {
                webSettingStore.setIgnorePageLoadTimeout(chckIgnorePageLoadTimeoutException.getSelection());
            }
            
            if (txtActionDelay != null) {
                webSettingStore.setActionDelay(Integer.parseInt(txtActionDelay.getText()));
            }
            
            if (txtDefaultIEHangTimeout != null) {
                webSettingStore.setIEHangTimeout(Integer.parseInt(txtDefaultIEHangTimeout.getText()));
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
