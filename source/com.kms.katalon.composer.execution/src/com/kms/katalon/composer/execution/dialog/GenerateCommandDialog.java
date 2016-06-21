package com.kms.katalon.composer.execution.dialog;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.core.util.PathUtil.absoluteToRelativePath;
import static com.kms.katalon.core.util.PathUtil.relativeToAbsolutePath;
import static com.kms.katalon.execution.util.MailUtil.getDistinctRecipients;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static org.apache.commons.lang.StringUtils.join;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.AddMailRecipientDialog;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.execution.addons.TestExecutionAddon;
import com.kms.katalon.composer.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.util.MobileDeviceUIProvider;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.appium.driver.AppiumDriverManager;
import com.kms.katalon.core.application.Application;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.ConsoleOptionCollector;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.console.entity.OsgiConsoleOptionContributor;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.entity.ReportLocationSetting;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class GenerateCommandDialog extends AbstractDialog {

    private enum GenerateCommandMode {
        CONSOLE_COMMAND, PROPERTIES_FILE
    };

    private static final String KATALON_EXECUTABLE = "katalon";

    private static final int GENERATE_PROPERTY_ID = 22;

    private static final int GENERATE_COMMAND_ID = 23;

    private Text txtTestSuite;

    private Text txtRemoteWebDriverURL;

    private Text txtOutputLocation;

    private Text txtReportName;

    private Text txtRetry;

    private Text txtStatusDelay;

    private Button btnBrowseTestSuite;

    private Button btnBrowseOutputLocation;

    private Button btnAddEmail;

    private Button btnDeleteEmail;

    private Button btnClearEmail;

    private Button chkUseRelativePath;

    private Button chkSendEmail;

    private Button chkDisplayConsoleLog;

    private Button chkKeepConsoleLog;

    private Button chkRetryFailedTestCase;

    private Combo comboBrowser;

    private Combo comboCustomExecution;

    private Combo comboRemoteWebDriverType;

    private Combo comboMobileDevice;

    private org.eclipse.swt.widgets.List listMailRecipient;

    private ListViewer listMailRecipientViewer;

    private String preferenceRecipients;

    private ProjectEntity project;

    private String defaultOutputReportLocation;

    private static final String ZERO = "0";

    private static final String BROWSER_TYPE_CUSTOM = TestExecutionAddon.CUSTOM_RUN_MENU_LABEL;

    private static final String DEFAULT_RETRY_TIME = Integer.toString(DefaultRerunSetting.DEFAULT_RERUN_TIME);

    private static final String defaultStatusDelay = Integer.toString(ConsoleMain.DEFAULT_SHOW_PROGRESS_DELAY);

    private static final String defaultPropertyFileName = ConsoleOptionCollector.DEFAULT_EXECUTION_PROPERTY_FILE_NAME;

    private static final String ARG_RUN_MODE = Application.RUN_MODE_OPTION;

    private static final String ARG_PROJECT_PATH = ConsoleMain.PROJECT_PK_OPTION;

    private static final String ARG_REPORT_FOLDER = ReportLocationSetting.REPORT_FOLDER_OPTION;

    private static final String ARG_REPORT_FILE_NAME = ReportLocationSetting.REPORT_FILE_NAME_OPTION;

    private static final String ARG_SEND_MAIL = EmailConfig.SEND_EMAIL_OPTION;

    private static final String ARG_OSGI_CONSOLE_LOG = OsgiConsoleOptionContributor.OSGI_CONSOLE_LOG_OPTION;

    private static final String ARG_OSGI_NO_EXIT = OsgiConsoleOptionContributor.OSGI_NO_EXIT_OPTION;

    private static final String ARG_STATUS_DELAY = ConsoleMain.SHOW_STATUS_DELAY_OPTION;

    private static final String ARG_TEST_SUITE_PATH = ConsoleMain.TESTSUITE_ID_OPTION;

    private static final String ARG_REMOTE_WEB_DRIVER_URL = DriverFactory.REMOTE_WEB_DRIVER_URL;

    private static final String ARG_REMOTE_WEB_DRIVER_TYPE = DriverFactory.REMOTE_WEB_DRIVER_TYPE;

    private static final String ARG_MOBILE_DEVICE_ID = AppiumDriverManager.EXECUTED_DEVICE_ID;

    private static final String ARG_BROWSER_TYPE = ConsoleMain.BROWSER_TYPE_OPTION;

    private static final String ARG_RETRY = DefaultRerunSetting.RETRY_OPTION;

    private static final String ARG_RETRY_FAILED_TEST_CASES = DefaultRerunSetting.RETRY_FAIL_TEST_CASE_ONLY_OPTION;

    private List<MobileDeviceInfo> deviceInfos = new ArrayList<>();

    public GenerateCommandDialog(Shell parentShell, ProjectEntity project) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_GENERATE_COMMAND_FOR_CONSOLE);

        this.project = project;
        defaultOutputReportLocation = projectLocation() + File.separator + StringConstants.ROOT_FOLDER_NAME_REPORT;

        ScopedPreferenceStore prefs = getPreferenceStore(GenerateCommandDialog.class);
        boolean isSendAttachmentPrefEnabled = prefs.getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT);
        if (isSendAttachmentPrefEnabled) {
            preferenceRecipients = prefs.getString(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS);
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(550, super.getInitialSize().y);
    }

    @Override
    protected int getShellStyle() {
        return SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | getDefaultOrientation();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout();
        glMain.marginHeight = 0;
        glMain.marginWidth = 0;
        main.setLayout(glMain);

        createTestSuitePart(main);
        createPlatformPart(main);
        createReportConfigPart(main);
        createOptionsPart(main);
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
        platformContainer.setLayout(new GridLayout());
        platformContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Group grpPlatform = new Group(platformContainer, SWT.NONE);
        grpPlatform.setLayout(new GridLayout(3, false));
        grpPlatform.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpPlatform.setText(StringConstants.DIA_GRP_EXECUTED_PLATFORM);

        Label lblBrowserType = new Label(grpPlatform, SWT.NONE);
        lblBrowserType.setText(StringConstants.DIA_RADIO_BROWSER);

        comboBrowser = new Combo(grpPlatform, SWT.READ_ONLY);
        comboBrowser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label lblRemoteWebDriverURL = new Label(grpPlatform, SWT.NONE);
        lblRemoteWebDriverURL.setText(StringConstants.DIA_REMOTE_WEB_DRIVER_URL);

        txtRemoteWebDriverURL = new Text(grpPlatform, SWT.BORDER);
        txtRemoteWebDriverURL.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        comboRemoteWebDriverType = new Combo(grpPlatform, SWT.READ_ONLY);

        Label lblMobileDevice = new Label(grpPlatform, SWT.NONE);
        lblMobileDevice.setText(StringConstants.DIA_RADIO_MOBILE_DEVICE);

        comboMobileDevice = new Combo(grpPlatform, SWT.READ_ONLY);
        comboMobileDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label lblCustomExecution = new Label(grpPlatform, SWT.NONE);
        lblCustomExecution.setText(StringConstants.DIA_LBL_CUSTOM_EXECUTION);

        comboCustomExecution = new Combo(grpPlatform, SWT.READ_ONLY);
        comboCustomExecution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    }

    private void createReportConfigPart(Composite parent) {
        Composite reportConfigContainer = new Composite(parent, SWT.NONE);
        reportConfigContainer.setLayout(new GridLayout());
        reportConfigContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Group grpReportConfigContainer = new Group(reportConfigContainer, SWT.NONE);
        grpReportConfigContainer.setLayout(new GridLayout(3, false));
        grpReportConfigContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpReportConfigContainer.setText(StringConstants.DIA_GRP_REPORT_CONFIG);

        Label lblFolderLocation = new Label(grpReportConfigContainer, SWT.NONE);
        lblFolderLocation.setText(StringConstants.DIA_OUTPUT_LOCATION);
        txtOutputLocation = new Text(grpReportConfigContainer, SWT.READ_ONLY | SWT.BORDER);
        txtOutputLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnBrowseOutputLocation = new Button(grpReportConfigContainer, SWT.FLAT);
        btnBrowseOutputLocation.setText(StringConstants.BROWSE);

        new Label(grpReportConfigContainer, SWT.NONE);
        chkUseRelativePath = new Button(grpReportConfigContainer, SWT.CHECK);
        chkUseRelativePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        chkUseRelativePath.setText(StringConstants.DIA_CHK_USE_RELATIVE_PATH);

        Label lblReportName = new Label(grpReportConfigContainer, SWT.NONE);
        lblReportName.setText(StringConstants.DIA_LBL_REPORT_NAME);
        txtReportName = new Text(grpReportConfigContainer, SWT.NONE | SWT.BORDER);
        txtReportName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(grpReportConfigContainer, SWT.NONE);

        Label lblPostExecution = new Label(grpReportConfigContainer, SWT.NONE);
        lblPostExecution.setText(StringConstants.DIA_LBL_POST_EXECUTION);
        chkSendEmail = new Button(grpReportConfigContainer, SWT.CHECK);
        chkSendEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        chkSendEmail.setText(StringConstants.DIA_CHK_SEND_SUMMARY_REPORT);

        Label lblMailRecipients = new Label(grpReportConfigContainer, SWT.NONE);
        lblMailRecipients.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblMailRecipients.setText(StringConstants.DIA_LBL_MAIL_RECIPIENTS);

        listMailRecipientViewer = new ListViewer(grpReportConfigContainer, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        listMailRecipient = listMailRecipientViewer.getList();
        listMailRecipient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        listMailRecipientViewer.setContentProvider(ArrayContentProvider.getInstance());

        Composite mailRecipientsBtnContainer = new Composite(grpReportConfigContainer, SWT.NONE);
        mailRecipientsBtnContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        GridLayout glMailRecipientsBtnContainer = new GridLayout();
        glMailRecipientsBtnContainer.marginHeight = 0;
        glMailRecipientsBtnContainer.marginWidth = 0;
        mailRecipientsBtnContainer.setLayout(glMailRecipientsBtnContainer);

        btnAddEmail = new Button(mailRecipientsBtnContainer, SWT.FLAT);
        btnAddEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnAddEmail.setText(StringConstants.ADD);

        btnDeleteEmail = new Button(mailRecipientsBtnContainer, SWT.FLAT);
        btnDeleteEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnDeleteEmail.setText(StringConstants.DELETE);

        btnClearEmail = new Button(mailRecipientsBtnContainer, SWT.FLAT);
        btnClearEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnClearEmail.setText(StringConstants.CLEAR);
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
        chkRetryFailedTestCase.setToolTipText(com.kms.katalon.composer.testsuite.constants.StringConstants.PA_LBL_TOOLTIP_TEST_CASE_ONLY);

        Label lblUpdateStatusTiming = new Label(grpOptionsContainer, SWT.NONE);
        lblUpdateStatusTiming.setText(StringConstants.DIA_LBL_UPDATE_EXECUTION_STATUS);

        txtStatusDelay = new Text(grpOptionsContainer, SWT.BORDER | SWT.CENTER);
        GridData gdTxtUpdateStatusTiming = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtUpdateStatusTiming.widthHint = 30;
        txtStatusDelay.setLayoutData(gdTxtUpdateStatusTiming);
        txtStatusDelay.setTextLimit(5);

        Label lblSeconds = new Label(grpOptionsContainer, SWT.NONE);
        lblSeconds.setText(StringConstants.DIA_LBL_SECONDS);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, GENERATE_PROPERTY_ID, StringConstants.DIA_BTN_GEN_PROPERTY_FILE, false);
        createButton(parent, GENERATE_COMMAND_ID, StringConstants.DIA_BTN_GEN_COMMAND, true);
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    @Override
    protected void setInput() {
        List<String> browsers = new ArrayList<>(Arrays.asList(WebUIDriverType.stringValues()));
        browsers.add(BROWSER_TYPE_CUSTOM);
        comboBrowser.setItems(browsers.toArray(new String[0]));

        txtRemoteWebDriverURL.setEnabled(false);
        comboRemoteWebDriverType.setEnabled(false);
        comboRemoteWebDriverType.setItems(RemoteWebDriverConnectorType.stringValues());
        comboRemoteWebDriverType.select(0);

        comboMobileDevice.setEnabled(false);
        comboMobileDevice.setItems(getMobileDevices());

        comboCustomExecution.setEnabled(false);
        comboCustomExecution.setItems(RunConfigurationCollector.getInstance().getAllCustomRunConfigurationIds());

        txtOutputLocation.setText(absoluteToRelativePath(defaultOutputReportLocation, projectLocation()));
        chkUseRelativePath.setSelection(true);
        txtReportName.setText(StringConstants.DIA_TXT_DEFAULT_REPORT_NAME);
        listMailRecipient.setEnabled(false);
        updateRecipientList();
        enableMailRecipientButtons(chkSendEmail.getSelection());
        txtRetry.setText(DEFAULT_RETRY_TIME);
        chkRetryFailedTestCase.setSelection(DefaultRerunSetting.DEFAULT_RERUN_FAILED_TEST_CASE_ONLY);
        txtStatusDelay.setText(defaultStatusDelay);
        enableRetryFailedTestCase();
    }

    private void enableRetryFailedTestCase() {
        String retry = txtRetry.getText();
        chkRetryFailedTestCase.setEnabled(!(ZERO.equals(retry) || retry.isEmpty()));
    }

    private void updateRecipientList() {
        try {
            TestSuiteEntity testsuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(
                    txtTestSuite.getText(), project);
            String testsuiteRecipients = null;
            if (testsuite != null) {
                testsuiteRecipients = testsuite.getMailRecipient();
            }

            listMailRecipientViewer.setInput(getDistinctRecipients(testsuiteRecipients, preferenceRecipients));
        } catch (Exception e) {
            logError(e);
        }
    }

    @Override
    protected void registerControlModifyListeners() {
        btnBrowseTestSuite.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    // Open test suite browser
                    TestSuiteSelectionDialog dialog = new TestSuiteSelectionDialog(getShell(),
                            new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(
                                    new EntityProvider()));
                    dialog.setInput(TreeEntityUtil.getChildren(null,
                            FolderController.getInstance().getTestSuiteRoot(project)));

                    if (dialog.open() != Window.OK) {
                        return;
                    }

                    Object result = dialog.getFirstResult();
                    if (result == null) {
                        return;
                    }

                    TestSuiteTreeEntity tsTreeEntity = (TestSuiteTreeEntity) result;
                    TestSuiteEntity testSuiteEntity = (TestSuiteEntity) tsTreeEntity.getObject();
                    txtTestSuite.setText(testSuiteEntity.getIdForDisplay());

                    updateRecipientList();
                    txtRetry.setText(Integer.toString(testSuiteEntity.getNumberOfRerun()));
                    chkRetryFailedTestCase.setSelection(testSuiteEntity.isRerunFailedTestCasesOnly());
                } catch (Exception e) {
                    logError(e);
                }
            }
        });

        comboBrowser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isRemoteWebDriverSelected = browserTypeIs(WebUIDriverType.REMOTE_WEB_DRIVER.toString());
                txtRemoteWebDriverURL.setEnabled(isRemoteWebDriverSelected);
                comboRemoteWebDriverType.setEnabled(isRemoteWebDriverSelected);
                comboMobileDevice.setEnabled(browserTypeIs(WebUIDriverType.ANDROID_DRIVER.toString())
                        || browserTypeIs(WebUIDriverType.IOS_DRIVER.toString()));
                comboCustomExecution.setEnabled(browserTypeIs(BROWSER_TYPE_CUSTOM));
            }
        });

        btnBrowseOutputLocation.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // Open system folder selection dialog
                DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
                directoryDialog.setFilterPath(getReportOutputAbsolutePath());
                String outputLocation = directoryDialog.open();
                if (isBlank(outputLocation)) {
                    return;
                }
                updateReportOutputLocation(outputLocation);
            }
        });

        chkUseRelativePath.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String outputLocation = txtOutputLocation.getText();
                if (isBlank(outputLocation) && chkUseRelativePath.getSelection()) {
                    return;
                }
                outputLocation = relativeToAbsolutePath(outputLocation, projectLocation());
                updateReportOutputLocation(outputLocation);
            }
        });

        chkSendEmail.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isChecked = chkSendEmail.getSelection();
                listMailRecipient.setEnabled(isChecked);
                enableMailRecipientButtons(isChecked);
            }
        });

        listMailRecipient.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                btnDeleteEmail.setEnabled(listMailRecipient.getSelectionCount() > 0);
            }
        });

        btnAddEmail.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = Display.getDefault().getActiveShell();
                AddMailRecipientDialog addMailDialog = new AddMailRecipientDialog(shell,
                        listMailRecipientViewer.getList().getItems());
                addMailDialog.open();

                if (addMailDialog.getReturnCode() == Dialog.OK) {
                    String[] emails = addMailDialog.getEmails();
                    if (emails.length > 0) {
                        listMailRecipientViewer.add(addMailDialog.getEmails());
                    }
                }
            }
        });

        btnDeleteEmail.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (listMailRecipientViewer.getList().getSelectionCount() == 0) {
                    return;
                }
                listMailRecipientViewer.remove(listMailRecipientViewer.getList().getSelection());
                enableMailRecipientButtons(true);
            }
        });

        btnClearEmail.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (listMailRecipientViewer.getList().getItemCount() == 0) {
                    return;
                }
                listMailRecipientViewer.setInput(new String[0]);
                enableMailRecipientButtons(true);
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

    private void updateReportOutputLocation(String location) {
        if (chkUseRelativePath.getSelection()) {
            location = absoluteToRelativePath(location, projectLocation());
        }
        txtOutputLocation.setText(location);
    }

    private String getReportOutputAbsolutePath() {
        String path = txtOutputLocation.getText();
        if (chkUseRelativePath.getSelection()) {
            path = relativeToAbsolutePath(path, projectLocation());
        }
        return path;
    }

    private String projectLocation() {
        return project.getFolderLocation();
    }

    private void enableMailRecipientButtons(boolean isEnabled) {
        btnAddEmail.setEnabled(isEnabled);
        btnDeleteEmail.setEnabled(isEnabled && listMailRecipientViewer.getList().getSelectionCount() > 0);
        btnClearEmail.setEnabled(isEnabled && listMailRecipientViewer.getList().getItemCount() > 0);
    }

    private String[] getMobileDevices() {
        String[] devices = new String[0];
        return getAllDevicesName().toArray(devices);
    }

    private List<String> getAllDevicesName() {
        deviceInfos.clear();
        deviceInfos.addAll(MobileDeviceUIProvider.getAllDevices());
        List<String> devicesNameList = new ArrayList<String>();
        for (MobileDeviceInfo deviceInfo : deviceInfos) {
            devicesNameList.add(deviceInfo.getDisplayName());
        }
        return devicesNameList;
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
            validateUserInput();
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
            GeneratedCommandDialog generatedCommandDialog = new GeneratedCommandDialog(getShell(), generateCommand());
            generatedCommandDialog.open();
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

    private String generateCommand() throws Exception {
        validateUserInput();

        Map<String, String> consoleAgrsMap = getUserConsoleAgrsMap(GenerateCommandMode.CONSOLE_COMMAND);
        StringBuilder commandBuilder = new StringBuilder(KATALON_EXECUTABLE);
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

    private Map<String, String> getUserConsoleAgrsMap(GenerateCommandMode generateCommandMode) {
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

        if (useCustomReportFolder()) {
            args.put(ARG_REPORT_FOLDER, getArgumentValueToSave(txtOutputLocation.getText(), generateCommandMode));

            // -reportFileName only affects when using with -reportFolder option
            if (!StringUtils.equals(txtReportName.getText(), StringConstants.DIA_TXT_DEFAULT_REPORT_NAME)) {
                args.put(ARG_REPORT_FILE_NAME, getArgumentValueToSave(txtReportName.getText(), generateCommandMode));
            }
        }

        if (chkSendEmail.getSelection() && listMailRecipient.getItemCount() > 0) {
            args.put(
                    ARG_SEND_MAIL,
                    getArgumentValueToSave(join(listMailRecipient.getItems(), MailUtil.EMAIL_SEPARATOR),
                            generateCommandMode));
        }

        if (!StringUtils.equals(txtStatusDelay.getText(), defaultStatusDelay)) {
            args.put(ARG_STATUS_DELAY, txtStatusDelay.getText());
        }

        String numOfRetry = txtRetry.getText();
        args.put(ARG_RETRY, numOfRetry);
        if (!StringUtils.equals(numOfRetry, ZERO) && chkRetryFailedTestCase.isEnabled()) {
            args.put(ARG_RETRY_FAILED_TEST_CASES, Boolean.toString(chkRetryFailedTestCase.getSelection()));
        }

        args.put(ARG_TEST_SUITE_PATH, getArgumentValueToSave(txtTestSuite.getText(), generateCommandMode));

        String browserType = browserTypeIs(BROWSER_TYPE_CUSTOM) ? comboCustomExecution.getText()
                : comboBrowser.getText();
        args.put(ARG_BROWSER_TYPE, getArgumentValueToSave(browserType, generateCommandMode));

        if (browserTypeIs(WebUIDriverType.REMOTE_WEB_DRIVER.toString())) {
            args.put(ARG_REMOTE_WEB_DRIVER_URL,
                    getArgumentValueToSave(txtRemoteWebDriverURL.getText(), generateCommandMode));
            args.put(ARG_REMOTE_WEB_DRIVER_TYPE, comboRemoteWebDriverType.getText());
        }

        if (browserTypeIs(WebUIDriverType.ANDROID_DRIVER.toString())
                || browserTypeIs(WebUIDriverType.IOS_DRIVER.toString())) {
            args.put(
                    ARG_MOBILE_DEVICE_ID,
                    getArgumentValueToSave(deviceInfos.get(comboMobileDevice.getSelectionIndex()).getDeviceId(),
                            generateCommandMode));
        }

        return args;
    }

    private boolean browserTypeIs(String typeString) {
        return StringUtils.equals(comboBrowser.getText(), typeString);
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

    private boolean useCustomReportFolder() {
        return !StringUtils.equals(getReportOutputAbsolutePath(), defaultOutputReportLocation);
    }

    private void validateUserInput() throws Exception {
        List<String> messages = new ArrayList<String>();

        if (isBlank(txtTestSuite.getText())) {
            messages.add(MessageFormat.format(StringConstants.DIA_MSG_PLS_SPECIFY_X, StringConstants.TEST_SUITE));
        }

        if (isBlank(comboBrowser.getText())) {
            messages.add(MessageFormat.format(StringConstants.DIA_MSG_PLS_SPECIFY_X, StringConstants.DIA_RADIO_BROWSER));
        }

        if (txtRemoteWebDriverURL.isEnabled() && isBlank(txtRemoteWebDriverURL.getText())) {
            messages.add(MessageFormat.format(StringConstants.DIA_MSG_PLS_SPECIFY_X,
                    StringConstants.DIA_REMOTE_WEB_DRIVER_URL));
        }

        if (comboMobileDevice.isEnabled() && comboMobileDevice.getSelectionIndex() == -1) {
            messages.add(MessageFormat.format(StringConstants.DIA_MSG_PLS_SPECIFY_X,
                    StringConstants.DIA_RADIO_MOBILE_DEVICE));
        }

        if (comboCustomExecution.isEnabled() && comboCustomExecution.getSelectionIndex() == -1) {
            messages.add(MessageFormat.format(StringConstants.DIA_MSG_PLS_SPECIFY_X,
                    StringConstants.DIA_LBL_CUSTOM_EXECUTION));
        }

        if (!messages.isEmpty()) {
            throw new Exception(StringUtils.join(messages, "\n"));
        }
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

}
