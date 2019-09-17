package com.kms.katalon.composer.execution.dialog;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.controls.HelpComposite;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.execution.collection.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.execution.collection.dialog.ExecutionProfileSelectionDialog;
import com.kms.katalon.composer.execution.collection.dialog.RunConfigurationSelectionDialog;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionEntryItem;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionGroup;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionItem;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.GenerateCommandPreferenceConstants;
import com.kms.katalon.composer.execution.constants.ImageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.resources.util.ImageUtil;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.application.Application;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.ConsoleOptionCollector;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.console.ConsoleOptionBuilder;
import com.kms.katalon.execution.console.entity.OsgiConsoleOptionContributor;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.integration.analytics.constants.ComposerAnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsApiKey;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.CryptoUtil;

public class GenerateCommandDialog extends AbstractDialog {

    private enum GenerateCommandMode {
        CONSOLE_COMMAND, PROPERTIES_FILE
    };

    private static final String KATALON_EXECUTABLE_LINUX = "./katalon --args";

    private static final String KATALON_EXECUTABLE_WIN32 = "katalon";

    private static final String KATALON_EXECUTABLE_MACOS = "./Katalon\\ Studio.app/Contents/MacOS/katalon --args";

    private static final int GENERATE_PROPERTY_ID = 22;

    private static final int GENERATE_COMMAND_ID = 23;

    private Text txtTestSuite;

    private Text txtRetry;

    private Text txtStatusDelay;
    
    private Text txtAPIKey;

    private Button btnBrowseTestSuite;

    private Button chkDisplayConsoleLog;

    private Button chkKeepConsoleLog;

    private Button chkRetryFailedTestCase;
    
    private Button chkAPIKey;

    private ProjectEntity project;

    private static final String ZERO = "0";

    private static final String DEFAULT_RETRY_TIME = Integer.toString(DefaultRerunSetting.DEFAULT_RERUN_TIME);

    private static final String defaultStatusDelay = Integer.toString(ConsoleMain.DEFAULT_SHOW_PROGRESS_DELAY);

    private static final String defaultPropertyFileName = ConsoleOptionCollector.DEFAULT_EXECUTION_PROPERTY_FILE_NAME;

    private static final String ARG_RUN_MODE = Application.RUN_MODE_OPTION;

    private static final String ARG_PROJECT_PATH = ConsoleMain.PROJECT_PK_OPTION;

    private static final String ARG_OSGI_CONSOLE_LOG = OsgiConsoleOptionContributor.OSGI_CONSOLE_LOG_OPTION;

    private static final String ARG_OSGI_NO_EXIT = OsgiConsoleOptionContributor.OSGI_NO_EXIT_OPTION;

    private static final String ARG_STATUS_DELAY = ConsoleMain.SHOW_STATUS_DELAY_OPTION;

    private static final String ARG_TEST_SUITE_PATH = ConsoleMain.TESTSUITE_ID_OPTION;

    private static final String ARG_TEST_SUITE_COLLECTION_PATH = ConsoleMain.TESTSUITE_COLLECTION_ID_OPTION;

    private static final String ARG_RETRY = DefaultRerunSetting.RETRY_OPTION;

    private static final String ARG_RETRY_FAILED_TEST_CASES = DefaultRerunSetting.RETRY_FAIL_TEST_CASE_ONLY_OPTION;
    
    private static final String ARG_API_KEY = OsgiConsoleOptionContributor.API_KEY_OPTION;

    private Group grpPlatform;

    private Composite main;

    private CLabel lblRunConfiguration, lblConfigurationData;

    private Button btnChangeRunConfigurationData, btnChangeConfiguration;

    private TestExecutionEntryItem testExecutionItem;

    private RunConfigurationDescription runConfigDescription;

    private Composite configurationDataComposite;

    private Composite configurationComposite;

    private CLabel lblProfileName;

    private Button btnChangeProfile;
    
    private AnalyticsSettingStore analyticsSettingStore;

    public GenerateCommandDialog(Shell parentShell, ProjectEntity project) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_GENERATE_COMMAND_FOR_CONSOLE);

        this.project = project;
    }

    @Override
    protected Point getInitialSize() {
        return super.getInitialSize();
    }

    @Override
    protected int getShellStyle() {
        return SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | getDefaultOrientation() | SWT.RESIZE;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        main = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout();
        glMain.marginHeight = 0;
        glMain.marginWidth = 0;
        main.setLayout(glMain);

        createTestSuitePart(main);
        createPlatformPart(main);
        createOptionsPart(main);
        getConfigurationAnalytics();
        changeEnabled();
        
        return main;
    }

    private void createTestSuitePart(Composite parent) {
        Composite testsuiteContainer = new Composite(parent, SWT.NONE);
        testsuiteContainer.setLayout(new GridLayout(3, false));
        testsuiteContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblTestSuite = new Label(testsuiteContainer, SWT.NONE);
        lblTestSuite.setText(StringConstants.TEST_SUITE);

        txtTestSuite = new Text(testsuiteContainer, SWT.READ_ONLY | SWT.BORDER);
        txtTestSuite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnBrowseTestSuite = new Button(testsuiteContainer, SWT.FLAT);
        btnBrowseTestSuite.setText(StringConstants.BROWSE);
    }

    private void createPlatformPart(Composite parent) {
        Composite platformContainer = new Composite(parent, SWT.NONE);
        GridLayout glPlatformContainer = new GridLayout(1, false);
        glPlatformContainer.marginHeight = 0;
        platformContainer.setLayout(glPlatformContainer);
        platformContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        grpPlatform = new Group(platformContainer, SWT.NONE);
        grpPlatform.setLayout(new GridLayout(1, false));
        grpPlatform.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        grpPlatform.setText(StringConstants.DIA_GRP_EXECUTED_PLATFORM);

        createConfigurationComposite();

        createConfigurationDataComposite();

        createExecutionProfileComposite();
        
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject.getType() == ProjectType.WEBSERVICE) {
            ((GridData) configurationComposite.getLayoutData()).exclude = true;
            configurationComposite.setVisible(false);

            ((GridData) configurationDataComposite.getLayoutData()).exclude = true;
            configurationDataComposite.setVisible(false);
        }
    }

    private void createExecutionProfileComposite() {
        Composite executionComposite = new Composite(grpPlatform, SWT.NONE);
        executionComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdExecutionComposite = new GridLayout(2, false);
        gdExecutionComposite.marginWidth = 0;
        gdExecutionComposite.marginHeight = 0;
        executionComposite.setLayout(gdExecutionComposite);

        Label lblRunWith = new Label(executionComposite, SWT.NONE);
        lblRunWith.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
        lblRunWith.setText(ComposerExecutionMessageConstants.DIA_LBL_PROFILE);

        Composite profileDetailsComposite = new Composite(executionComposite, SWT.NONE);
        profileDetailsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdConfiguration = new GridLayout(2, false);
        gdConfiguration.marginWidth = 0;
        gdConfiguration.marginHeight = 0;
        profileDetailsComposite.setLayout(gdConfiguration);

        lblProfileName = new CLabel(profileDetailsComposite, SWT.NONE);
        lblProfileName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        btnChangeProfile = new Button(profileDetailsComposite, SWT.FLAT);
        btnChangeProfile.setImage(ImageConstants.IMG_16_EDIT);
        btnChangeProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        btnChangeProfile.setText(StringConstants.EDIT);
    }

    private void createConfigurationComposite() {
        configurationComposite = new Composite(grpPlatform, SWT.NONE);
        configurationComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdConfigurationData = new GridLayout(2, false);
        gdConfigurationData.marginWidth = 0;
        gdConfigurationData.marginHeight = 0;
        configurationComposite.setLayout(gdConfigurationData);

        Label lblRunWith = new Label(configurationComposite, SWT.NONE);
        lblRunWith.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
        lblRunWith.setText(ComposerExecutionMessageConstants.DIA_LBL_RUN_WITH);

        Composite configurationDetailsComposite = new Composite(configurationComposite, SWT.NONE);
        configurationDetailsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdConfiguration = new GridLayout(2, false);
        gdConfiguration.marginWidth = 0;
        gdConfiguration.marginHeight = 0;
        configurationDetailsComposite.setLayout(gdConfiguration);

        lblRunConfiguration = new CLabel(configurationDetailsComposite, SWT.NONE);
        lblRunConfiguration.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        lblRunConfiguration.setText(ComposerExecutionMessageConstants.DIA_TITLE_RUN_CONFIG_SELECTION);

        btnChangeConfiguration = new Button(configurationDetailsComposite, SWT.FLAT);
        btnChangeConfiguration.setImage(ImageConstants.IMG_16_EDIT);
        btnChangeConfiguration.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        btnChangeConfiguration.setText(StringConstants.EDIT);
    }

    private void createConfigurationDataComposite() {
        configurationDataComposite = new Composite(grpPlatform, SWT.NONE);
        configurationDataComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdConfigurationData = new GridLayout(2, false);
        gdConfigurationData.marginWidth = 0;
        gdConfigurationData.marginHeight = 0;
        configurationDataComposite.setLayout(gdConfigurationData);

        Label lblRunConfiguration = new Label(configurationDataComposite, SWT.NONE);
        lblRunConfiguration
                .setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER));
        lblRunConfiguration.setText(ComposerExecutionMessageConstants.DIA_LBL_RUN_CONFIGURATION);

        Composite configurationDataDetails = new Composite(configurationDataComposite, SWT.NONE);
        configurationDataDetails.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdConfigurationDataDetails = new GridLayout(2, false);
        gdConfigurationDataDetails.marginWidth = 0;
        gdConfigurationDataDetails.marginHeight = 0;
        configurationDataDetails.setLayout(gdConfigurationDataDetails);

        lblConfigurationData = new CLabel(configurationDataDetails, SWT.NONE);
        lblConfigurationData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        lblConfigurationData.setText(ComposerExecutionMessageConstants.DIA_MSG_CONFIGURATION_IS_REQUIRED);

        btnChangeRunConfigurationData = new Button(configurationDataDetails, SWT.FLAT);
        btnChangeRunConfigurationData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        btnChangeRunConfigurationData.setImage(ImageConstants.IMG_16_EDIT);
        btnChangeRunConfigurationData.setText(StringConstants.EDIT);
    }

    private void createOptionsPart(Composite parent) {
        Composite optionsContainer = new Composite(parent, SWT.NONE);
        optionsContainer.setLayout(new GridLayout());
        optionsContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Group grpOptionsContainer = new Group(optionsContainer, SWT.NONE);
        grpOptionsContainer.setLayout(new GridLayout(3, false));
        grpOptionsContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpOptionsContainer.setText(StringConstants.DIA_GRP_OTHER_OPTIONS);

        chkDisplayConsoleLog = new Button(grpOptionsContainer, SWT.CHECK);
        chkDisplayConsoleLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        chkDisplayConsoleLog.setText(StringConstants.DIA_CHK_DISPLAY_CONSOLE_LOG);

        chkKeepConsoleLog = new Button(grpOptionsContainer, SWT.CHECK);
        chkKeepConsoleLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        chkKeepConsoleLog.setText(StringConstants.DIA_CHK_KEEP_CONSOLE_LOG);

        Composite compRetry = new Composite(grpOptionsContainer, SWT.NONE);
        GridLayout glRetry = new GridLayout(4, false);
        glRetry.marginWidth = 0;
        glRetry.marginHeight = 0;
        compRetry.setLayout(glRetry);
        compRetry.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));

        Label lblRetry = new Label(compRetry, SWT.NONE);
        lblRetry.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblRetry.setText(StringConstants.DIA_LBL_RETRY_TEST_SUITE);
        lblRetry.setToolTipText(com.kms.katalon.composer.testsuite.constants.StringConstants.PA_LBL_TOOLTIP_RETRY);

        txtRetry = new Text(compRetry, SWT.BORDER | SWT.CENTER);
        GridData gdTxtRetry = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtRetry.widthHint = 30;
        txtRetry.setLayoutData(gdTxtRetry);
        txtRetry.setTextLimit(3);
        txtRetry.setToolTipText(com.kms.katalon.composer.testsuite.constants.StringConstants.PA_LBL_TOOLTIP_RETRY);

        Label lblRetry1 = new Label(compRetry, SWT.NONE);
        lblRetry1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblRetry1.setText(StringConstants.DIA_LBL_RETRY_TIMES);

        chkRetryFailedTestCase = new Button(compRetry, SWT.CHECK);
        chkRetryFailedTestCase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        chkRetryFailedTestCase.setText(StringConstants.DIA_CHK_FOR_FAILED_TEST_CASES);
        chkRetryFailedTestCase.setToolTipText(
                com.kms.katalon.composer.testsuite.constants.StringConstants.PA_LBL_TOOLTIP_TEST_CASE_ONLY);

        Label lblUpdateStatusTiming = new Label(grpOptionsContainer, SWT.NONE);
        lblUpdateStatusTiming.setText(StringConstants.DIA_LBL_UPDATE_EXECUTION_STATUS);

        txtStatusDelay = new Text(grpOptionsContainer, SWT.BORDER | SWT.CENTER);
        GridData gdTxtUpdateStatusTiming = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtUpdateStatusTiming.widthHint = 30;
        txtStatusDelay.setLayoutData(gdTxtUpdateStatusTiming);
        txtStatusDelay.setTextLimit(5);

        Label lblSeconds = new Label(grpOptionsContainer, SWT.NONE);
        lblSeconds.setText(StringConstants.DIA_LBL_SECONDS);
        
        chkAPIKey = new Button(grpOptionsContainer, SWT.CHECK);
        chkAPIKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        chkAPIKey.setText(StringConstants.DIA_API_KEY);
        
        txtAPIKey = new Text(grpOptionsContainer, SWT.BORDER);
        GridData gdTxtAPIKey = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1);
        gdTxtAPIKey.widthHint = 600;
        txtAPIKey.setLayoutData(gdTxtAPIKey);
        
        Composite pluginOptionsComposite = new Composite(grpOptionsContainer, SWT.NONE);
        pluginOptionsComposite.setLayout(new GridLayout(2, false));
        pluginOptionsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
        
        Label lblApiKeyUsage = new Label(pluginOptionsComposite, SWT.NONE);
        ControlUtils.setFontStyle(lblApiKeyUsage, SWT.BOLD | SWT.ITALIC, -1);
        lblApiKeyUsage.setText(StringConstants.DIA_LBL_API_KEY_USAGE);

        new HelpComposite(pluginOptionsComposite, DocumentationMessageConstants.KSTORE_API_KEYS_USAGE);
    }
    
    private void changeEnabled() {
    	txtAPIKey.setEnabled(chkAPIKey.getSelection());
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, GENERATE_PROPERTY_ID, StringConstants.DIA_BTN_GEN_PROPERTY_FILE, false);
        createButton(parent, GENERATE_COMMAND_ID, StringConstants.DIA_BTN_GEN_COMMAND, true);
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    @Override
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
        return DocumentationMessageConstants.DIALOG_GENERATE_COMMAND;
    }

    @Override
    protected void setInput() {
        txtRetry.setText(DEFAULT_RETRY_TIME);
        chkRetryFailedTestCase.setSelection(DefaultRerunSetting.DEFAULT_RERUN_FAILED_TEST_CASE_ONLY);
        txtStatusDelay.setText(defaultStatusDelay);
        enableRetryFailedTestCase();

        loadLastWorkingData();
        updatePlatformLayout();
    }

    private void loadLastWorkingData() {
        try {
            ScopedPreferenceStore prefs = getPreference();

            if (!prefs.isDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_DISPLAY_CONSOLE_LOG)) {
                chkDisplayConsoleLog.setSelection(
                        prefs.getBoolean(GenerateCommandPreferenceConstants.GEN_COMMAND_DISPLAY_CONSOLE_LOG));
            }

            if (!prefs.isDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_NO_CLOSE_CONSOLE_LOG)) {
                chkKeepConsoleLog.setSelection(
                        prefs.getBoolean(GenerateCommandPreferenceConstants.GEN_COMMAND_NO_CLOSE_CONSOLE_LOG));
            }

            if (!prefs.isDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_UPDATE_STATUS_TIME_INTERVAL)) {
                txtStatusDelay.setText(String.valueOf(
                        prefs.getInt(GenerateCommandPreferenceConstants.GEN_COMMAND_UPDATE_STATUS_TIME_INTERVAL)));
            }

            if (!prefs.isDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY)) {
                txtRetry.setText(String.valueOf(prefs.getInt(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY)));
                enableRetryFailedTestCase();
            }

            if (!prefs.isDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY_FOR_FAILED_TEST_CASES)) {
                chkRetryFailedTestCase.setSelection(
                        prefs.getBoolean(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY_FOR_FAILED_TEST_CASES));
            }

            if (!prefs.isDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_SUITE_ID)) {
                String prefSuiteId = prefs.getString(GenerateCommandPreferenceConstants.GEN_COMMAND_SUITE_ID);
                changeSuiteArtifact(getSelectedTestSuite(prefSuiteId));
            }

            onRunConfigurationChanged(getStoredConfigurationDescription());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private FileEntity getSelectedTestSuite(String prefSuiteId) throws Exception {
        if (isTestSuite(prefSuiteId)) {
            return TestSuiteController.getInstance().getTestSuiteByDisplayId(prefSuiteId, project);
        }
        return TestSuiteCollectionController.getInstance().getTestSuiteCollection(prefSuiteId);
    }

    private void enableRetryFailedTestCase() {
        String retry = txtRetry.getText();
        chkRetryFailedTestCase.setEnabled(!(ZERO.equals(retry) || retry.isEmpty()));
    }

    @Override
    protected void registerControlModifyListeners() {
        btnBrowseTestSuite.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    // Open test suite browser
                    TestSuiteSelectionDialog dialog = new TestSuiteSelectionDialog(getShell(),
                            new EntityLabelProvider(), new EntityProvider(),
                            new EntityViewerFilter(new EntityProvider()));
                    dialog.setInput(
                            TreeEntityUtil.getChildren(null, FolderController.getInstance().getTestSuiteRoot(project)));

                    if (dialog.open() != Window.OK) {
                        return;
                    }

                    Object result = dialog.getFirstResult();
                    if (result == null) {
                        return;
                    }

                    ITreeEntity tsTreeEntity = (ITreeEntity) result;
                    FileEntity fileEntity = (FileEntity) tsTreeEntity.getObject();
                    changeSuiteArtifact(fileEntity);
                    onRunConfigurationChanged(GenerateCommandDialog.this.runConfigDescription);
                } catch (Exception e) {
                    logError(e);
                }
            }
        });

        VerifyListener verifyNumberListener = new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                // Number input only
                if (!isNumeric(e.text)) {
                    e.doit = false;
                }
            }
        };

        txtRetry.addVerifyListener(verifyNumberListener);
        txtRetry.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                enableRetryFailedTestCase();
            }
        });
        txtRetry.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                correctNumberInput(txtRetry, DEFAULT_RETRY_TIME);
            }

            @Override
            public void focusGained(FocusEvent e) {
                txtRetry.selectAll();
            }
        });

        txtStatusDelay.addVerifyListener(verifyNumberListener);
        txtStatusDelay.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                correctNumberInput(txtStatusDelay, defaultStatusDelay);
            }

            @Override
            public void focusGained(FocusEvent e) {
                txtStatusDelay.selectAll();
            }
        });

        btnChangeConfiguration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                RunConfigurationSelectionDialog dialog = new RunConfigurationSelectionDialog(getParentShell(),
                        runConfigDescription);
                if (dialog.open() != RunConfigurationSelectionDialog.OK) {
                    return;
                }
                onRunConfigurationChanged(dialog.getSelectedConfiguration());
            }
        });

        btnChangeRunConfigurationData.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (runConfigDescription == null) {
                    return;
                }
                Map<String, String> newValue = testExecutionItem.changeRunConfigurationData(getShell(),
                        runConfigDescription.getRunConfigurationData());
                runConfigDescription.setRunConfigurationData(newValue);
                onRunConfigurationDataChanged();
            }
        });

        btnChangeProfile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    List<ExecutionProfileEntity> profiles = GlobalVariableController.getInstance()
                            .getAllGlobalVariableCollections(ProjectController.getInstance().getCurrentProject());
                    ExecutionProfileEntity selectedProfile = profiles.stream()
                            .filter(p -> p.getName().equals(runConfigDescription.getProfileName()))
                            .findFirst()
                            .orElse(null);
                    ExecutionProfileSelectionDialog dialog = new ExecutionProfileSelectionDialog(getParentShell(),
                            profiles, selectedProfile);
                    if (dialog.open() != ExecutionProfileSelectionDialog.OK) {
                        return;
                    }
                    runConfigDescription.setProfileName(dialog.getSelectedProfile().getName());
                    updateExecutionProfileLabel();
                } catch (ControllerException ex) {
                    MultiStatusErrorDialog.showErrorDialog(
                            ComposerExecutionMessageConstants.PA_MSG_UNABLE_TO_SELECT_EXECUTION_PROFILES,
                            ex.getMessage(), ExceptionsUtil.getMessageForThrowable(ex));
                }
            }
        });
                
        chkAPIKey.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeEnabled();
            }
        });
    }

    private void onRunConfigurationDataChanged() {
        try {
            updateRunConfigurationDataLabel();
        } finally {
            setGenerateCommandButtonStates();
        }
    }

    private void setGenerateCommandButtonStates() {
        boolean isValidInput = isValidInput();
        getButton(GENERATE_COMMAND_ID).setEnabled(isValidInput);
        getButton(GENERATE_PROPERTY_ID).setEnabled(isValidInput);
    }

    private void onRunConfigurationChanged(RunConfigurationDescription configurationDescription) {
        try {
            this.testExecutionItem = getSelectedExecutionItem(configurationDescription);
            if (testExecutionItem != null && !testExecutionItem.shouldBeDisplayed(project)) {
                this.testExecutionItem = null;
            }
            if (testExecutionItem == null) {
                this.runConfigDescription = TestExecutionGroupCollector.getInstance().getDefaultConfiguration(
                        ProjectController.getInstance().getCurrentProject());

                this.testExecutionItem = getSelectedExecutionItem(this.runConfigDescription);
            } else {
                this.runConfigDescription = configurationDescription;
            }

            updateRunConfigurationLabel();
            updateRunConfigurationDataLabel();
            updateExecutionProfileLabel();
            updateConfigurationDataCompositeLayout();
        } finally {
            setGenerateCommandButtonStates();
        }
    }

    private void updateExecutionProfileLabel() {
        lblProfileName.setImage(ImageConstants.IMG_16_PROFILE);
        lblProfileName.setText(runConfigDescription.getProfileName());
        lblProfileName.getParent().layout();
    }

    private void resetLabel(CLabel label, String defautText) {
        label.setImage(null);
        label.setText(defautText);
    }

    private void layoutShell() {
        Shell shell = getShell();
        shell.setRedraw(false);
        shell.setSize(shell.getBounds().width, shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        shell.setRedraw(true);
        shell.layout(true);
    }

    private void updateControlLayout(Composite composite, boolean visible) {
        GridData gdConfigurationData = (GridData) composite.getLayoutData();
        gdConfigurationData.exclude = !visible;
        composite.setVisible(visible);
        composite.getParent().pack();
        main.layout(true, true);
        layoutShell();
    }

    private void updatePlatformLayout() {
        updateControlLayout(grpPlatform, isTestSuite(txtTestSuite.getText()));
        updateConfigurationDataCompositeLayout();
    }

    private void updateConfigurationDataCompositeLayout() {
        updateControlLayout(configurationDataComposite,
                testExecutionItem != null && testExecutionItem.requiresExtraConfiguration()
                && testExecutionItem.shouldBeDisplayed(project));
    }

    private void updateRunConfigurationLabel() {
        if (testExecutionItem == null) {
            return;
        }
        lblRunConfiguration.setText(testExecutionItem.getName());
        try {
            String imageUrlAsString = testExecutionItem.getImageUrlAsString();
            if (StringUtils.isNotEmpty(imageUrlAsString)) {
                lblRunConfiguration.setImage(ImageUtil.loadImage(imageUrlAsString));
            } else {
                lblRunConfiguration.setImage(null);
            }
        } catch (MalformedURLException e) {
            LoggerSingleton.logError(e);
        }
        configurationComposite.layout(true, true);
    }

    private void updateRunConfigurationDataLabel() {
        if (testExecutionItem == null) {
            return;
        }
        if (runConfigDescription == null) {
            resetLabel(lblConfigurationData, ComposerExecutionMessageConstants.DIA_MSG_CONFIGURATION_IS_REQUIRED);
        }
        String text = StringUtils.defaultIfEmpty(
                testExecutionItem.displayRunConfigurationData(runConfigDescription.getRunConfigurationData()),
                ComposerExecutionMessageConstants.DIA_MSG_CONFIGURATION_IS_REQUIRED);
        lblConfigurationData.setText(text);
        configurationDataComposite.layout(true, true);
    }

    private TestExecutionEntryItem getSelectedExecutionItem(RunConfigurationDescription runConfigurationDescription) {
        if (runConfigurationDescription == null) {
            return null;
        }
        String runConfigurationId = runConfigurationDescription.getRunConfigurationId();
        TestExecutionGroup group = TestExecutionGroupCollector.getInstance()
                .getGroup(runConfigurationDescription.getGroupName());
        if (group == null) {
            resetLabel(lblRunConfiguration, ComposerExecutionMessageConstants.DIA_TITLE_RUN_CONFIG_SELECTION);
            return null;
        }
        Optional<TestExecutionItem> executionItemOpt = group.getItem(runConfigurationId);
        if (!executionItemOpt.isPresent()) {
            resetLabel(lblRunConfiguration, ComposerExecutionMessageConstants.DIA_TITLE_RUN_CONFIG_SELECTION);
            return null;
        }
        return (TestExecutionEntryItem) executionItemOpt.get();
    }

    private void correctNumberInput(Text txtFieldNumber, String defaultValue) {
        String textNumber = txtFieldNumber.getText();
        if (isBlank(textNumber)) {
            // Default set to default if no input
            txtFieldNumber.setText(defaultValue);
            return;
        }

        if (textNumber.length() > 1 && textNumber.startsWith(ZERO)) {
            // remove leading zeros in text number
            txtFieldNumber.setText(Integer.valueOf(textNumber).toString());
        }
    }

    private String projectLocation() {
        return project.getFolderLocation();
    }

    @Override
    protected void buttonPressed(int buttonId) {
        switch (buttonId) {
            case IDialogConstants.CLOSE_ID:
                close();
                break;
            case GENERATE_PROPERTY_ID:
                generatePropertyPressed();
                break;
            case GENERATE_COMMAND_ID:
                generateCommandPressed();
                break;
        }
    }

    private void generatePropertyPressed() {
        try {
            FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
            dialog.setFilterNames(new String[] { "Property Files (*.properties)" });
            dialog.setFilterExtensions(new String[] { "*.properties" });
            dialog.setFilterPath(projectLocation());
            dialog.setFileName(defaultPropertyFileName);
            String result = dialog.open();

            // User pressed cancel
            if (result == null) {
                return;
            }
            savePropertyFile(result);
        } catch (Exception e) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN_TITLE, e.getMessage());
        }
    }

    private void generateCommandPressed() {
        try {
        	if (chkAPIKey.getSelection() && StringUtils.isEmpty(txtAPIKey.getText())) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(),
                        ComposerAnalyticsStringConstants.ERROR,
                        StringConstants.REPORT_MSG_MUST_ENTER_API_KEY);
                return;
            }
        	
            GeneratedCommandDialog generatedCommandDialog = new GeneratedCommandDialog(getShell(), generateCommand());
            generatedCommandDialog.open();

            Trackings.trackGenerateCmd();
        } catch (Exception e) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN_TITLE, e.getMessage());
        }
    }

    private void savePropertyFile(String fileLocation) throws Exception {
        if (isBlank(fileLocation)) {
            throw new Exception(StringConstants.DIA_MSG_PLS_SPECIFY_FILE_LOCATION);
        }
        try {
            ExecutionUtil.savePropertiesFile(getUserConsoleAgrsMap(GenerateCommandMode.PROPERTIES_FILE), fileLocation);
        } catch (IOException e) {
            logError(e);
        }
    }

    private String generateCommand() throws ExecutionException {
        Map<String, String> consoleAgrsMap = getUserConsoleAgrsMap(GenerateCommandMode.CONSOLE_COMMAND);
        StringBuilder commandBuilder = new StringBuilder();

        switch (Platform.getOS()) {
            case Platform.OS_MACOSX:
                commandBuilder.append(KATALON_EXECUTABLE_MACOS);
                break;
                
            case Platform.OS_WIN32:
                commandBuilder.append(KATALON_EXECUTABLE_WIN32);
                break;
            default:
                commandBuilder.append(KATALON_EXECUTABLE_LINUX);
        }

        commandBuilder.append(" -noSplash ");

        for (String key : consoleAgrsMap.keySet()) {
            commandBuilder.append(" ");
            commandBuilder.append(wrapArgName(key));
            String value = consoleAgrsMap.get(key);
            if (isNotEmpty(value)) {
                commandBuilder.append(ConsoleMain.ARGUMENT_SPLITTER);
                commandBuilder.append(value);
            }
        }
        return commandBuilder.toString();
    }

    private Map<String, String> getUserConsoleAgrsMap(GenerateCommandMode generateCommandMode)
            throws ExecutionException {
        Map<String, String> args = new LinkedHashMap<String, String>();
        if (generateCommandMode == GenerateCommandMode.CONSOLE_COMMAND) {
            args.put(ARG_RUN_MODE, Application.RUN_MODE_OPTION_CONSOLE);
            if (chkDisplayConsoleLog.getSelection()) {
                // OSGi argument
                args.put(ARG_OSGI_CONSOLE_LOG, StringConstants.EMPTY);
            }

            if (chkKeepConsoleLog.getSelection()) {
                // OSGi argument
                args.put(ARG_OSGI_NO_EXIT, StringConstants.EMPTY);
            }
        }

        args.put(ARG_PROJECT_PATH, getArgumentValueToSave(project.getLocation(), generateCommandMode));

        if (!StringUtils.equals(txtStatusDelay.getText(), defaultStatusDelay)) {
            args.put(ARG_STATUS_DELAY, txtStatusDelay.getText());
        }

        String numOfRetry = txtRetry.getText();
        args.put(ARG_RETRY, numOfRetry);
        if (!StringUtils.equals(numOfRetry, ZERO) && chkRetryFailedTestCase.isEnabled()) {
            args.put(ARG_RETRY_FAILED_TEST_CASES, Boolean.toString(chkRetryFailedTestCase.getSelection()));
        }

        String entityId = txtTestSuite.getText();
        if (isTestSuite(entityId)) {
            args.put(ARG_TEST_SUITE_PATH, getArgumentValueToSave(entityId, generateCommandMode));

            for (Entry<String, String> entry : ConsoleOptionBuilder.argsMap(runConfigDescription).entrySet()) {
                args.put(entry.getKey(), getArgumentValueToSave(entry.getValue(), generateCommandMode));
            }
        } else {
            args.put(ARG_TEST_SUITE_COLLECTION_PATH, getArgumentValueToSave(entityId, generateCommandMode));
        }
        
        if (chkAPIKey.getSelection()) {
        	args.put(ARG_API_KEY, wrapArgumentValue(txtAPIKey.getText()));
        }

        return args;
    }

    private boolean isTestSuite(String id) {
        try {
            return TestSuiteController.getInstance().getTestSuiteByDisplayId(id,
                    ProjectController.getInstance().getCurrentProject()) != null;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    private String wrapArgName(String name) {
        return ConsoleMain.ARGUMENT_PREFIX + name;
    }

    private String getArgumentValueToSave(String value, GenerateCommandMode generateCommandMode) {
        if (generateCommandMode == GenerateCommandMode.PROPERTIES_FILE) {
            return value;
        }
        return wrapArgumentValue(value);
    }

    private String wrapArgumentValue(String value) {
        return "\"" + value + "\"";
    }

    private boolean isValidInput() {
        String entityId = txtTestSuite.getText();
        if (isBlank(entityId)) {
            return false;
        }
        if (!isTestSuite(entityId)) {
            return true;
        }
        if (runConfigDescription == null || testExecutionItem == null
                || runConfigDescription.getRunConfigurationId().isEmpty()) {
            return false;
        }
        if (!testExecutionItem.requiresExtraConfiguration()) {
            return true;
        }
        Map<String, String> runConfigurationData = runConfigDescription.getRunConfigurationData();
        return runConfigurationData != null && !runConfigurationData.isEmpty();
    }

    private class GeneratedCommandDialog extends Dialog {
        private static final int COPY_TO_CLIPBOARD_ID = 24;

        private String command;

        private Text txtCommand;

        public GeneratedCommandDialog(Shell parentShell, String command) {
            super(parentShell);
            this.command = command;
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite main = (Composite) super.createDialogArea(parent);
            GridLayout glMain = (GridLayout) main.getLayout();
            glMain.numColumns = 1;

            Label message = new Label(main, SWT.NONE);
            message.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            message.setText(StringConstants.DIA_LBL_GENERATED_COMMAND_MESSAGE);

            txtCommand = new Text(main, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
            txtCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            txtCommand.setText(getCommand());

            return main;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(parent, COPY_TO_CLIPBOARD_ID, StringConstants.DIA_BTN_COPY_TO_CLIPBOARD, true);
            createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
        }

        @Override
        protected void buttonPressed(int buttonId) {
            switch (buttonId) {
                case IDialogConstants.CLOSE_ID:
                    close();
                    break;
                case COPY_TO_CLIPBOARD_ID:
                    txtCommand.selectAll();
                    txtCommand.copy();
                    break;
            }
        }

        @Override
        protected Point getInitialSize() {
            return new Point(500, 300);
        }

        @Override
        protected void configureShell(Shell newShell) {
            newShell.setText(StringConstants.DIA_TITLE_GENERATED_COMMAND);
            super.configureShell(newShell);
        }

        public String getCommand() {
            if (command == null) {
                return StringConstants.EMPTY;
            }
            return command;
        }
    }

    private void changeSuiteArtifact(FileEntity testSuite) {
        if (testSuite == null) {
            return;
        }
        txtTestSuite.setText(testSuite.getIdForDisplay());
        boolean isTestSuite = false;
        if (testSuite instanceof TestSuiteEntity) {
            TestSuiteEntity testSuiteEntity = (TestSuiteEntity) testSuite;
            txtRetry.setText(Integer.toString(testSuiteEntity.getNumberOfRerun()));
            chkRetryFailedTestCase.setSelection(testSuiteEntity.isRerunFailedTestCasesOnly());
            isTestSuite = true;
        }
        ControlUtils.recursiveSetEnabled(grpPlatform, isTestSuite);
        updatePlatformLayout();
    }

    private static ScopedPreferenceStore getPreference() {
        return PreferenceStoreManager.getPreferenceStore(GenerateCommandDialog.class);
    }

    private void saveUserInput() {
        ScopedPreferenceStore prefs = getPreference();
        prefs.setValue(GenerateCommandPreferenceConstants.GEN_COMMAND_SUITE_ID, txtTestSuite.getText());
        prefs.setValue(GenerateCommandPreferenceConstants.GEN_COMMAND_DISPLAY_CONSOLE_LOG,
                chkDisplayConsoleLog.getSelection());
        prefs.setValue(GenerateCommandPreferenceConstants.GEN_COMMAND_NO_CLOSE_CONSOLE_LOG,
                chkKeepConsoleLog.getSelection());
        prefs.setValue(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY, txtRetry.getText());
        prefs.setValue(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY_FOR_FAILED_TEST_CASES,
                chkRetryFailedTestCase.getSelection());
        prefs.setValue(GenerateCommandPreferenceConstants.GEN_COMMAND_UPDATE_STATUS_TIME_INTERVAL,
                txtStatusDelay.getText());
        prefs.setValue(GenerateCommandPreferenceConstants.GEN_COMMAND_CONFIGURATION_DESCRIPTION,
                runConfigDescription != null ? JsonUtil.toJson(runConfigDescription) : StringUtils.EMPTY);
        try {
            prefs.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
    
    private void getConfigurationAnalytics() {
        analyticsSettingStore = new AnalyticsSettingStore(ProjectController.getInstance().getCurrentProject().getFolderLocation());

        try {
            boolean enableApiKey = analyticsSettingStore.isIntegrationEnabled() && analyticsSettingStore.isAutoSubmit();
            chkAPIKey.setSelection(enableApiKey);
            getApiKey();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
    
    private void getApiKey() {
        try {
            boolean isEncryptionEnabled = analyticsSettingStore.isEncryptionEnabled();
            String serverUrl = analyticsSettingStore.getServerEndpoint(isEncryptionEnabled);
            String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
            String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
            if (!Strings.isNullOrEmpty(email) && !Strings.isNullOrEmpty(encryptedPassword)) {
                String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                List<AnalyticsApiKey> apiKeys = AnalyticsApiProvider.getApiKeys(serverUrl, token.getAccess_token());
                if (!apiKeys.isEmpty()) {
                    txtAPIKey.setText(apiKeys.get(0).getKey());
                }
            }
        } catch (Exception ex) {
        }
    }
    
    private RunConfigurationDescription getStoredConfigurationDescription() {
        ScopedPreferenceStore prefs = getPreference();
        String runConfigAsJson = prefs
                .getString(GenerateCommandPreferenceConstants.GEN_COMMAND_CONFIGURATION_DESCRIPTION);
        if (StringUtils.isEmpty(runConfigAsJson)) {
            return null;
        }
        return JsonUtil.fromJson(runConfigAsJson, RunConfigurationDescription.class);
    }

    @Override
    public boolean close() {
        saveUserInput();
        return super.close();
    }

}