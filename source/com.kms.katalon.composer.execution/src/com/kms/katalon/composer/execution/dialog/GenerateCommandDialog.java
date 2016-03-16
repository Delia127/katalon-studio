package com.kms.katalon.composer.execution.dialog;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.core.util.PathUtil.absoluteToRelativePath;
import static com.kms.katalon.core.util.PathUtil.relativeToAbsolutePath;
import static com.kms.katalon.execution.util.MailUtil.getDistinctRecipients;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static org.apache.commons.lang.StringUtils.join;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.constants.PreferenceConstants.ExecutionPreferenceConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.launcher.manager.ConsoleMain;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class GenerateCommandDialog extends AbstractDialog {

    private static final int GENERATE_PROPERTY_ID = 22;

    private static final int GENERATE_COMMAND_ID = 23;

    private Text txtTestSuite;

    private Text txtRemoteWebDriverURL;

    private Text txtOutputLocation;

    private Text txtReportName;

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

    private Combo comboBrowser;

    private Combo comboRemoteWebDriverType;

    private Combo comboMobileDevice;

    private org.eclipse.swt.widgets.List listMailRecipient;

    private ListViewer listMailRecipientViewer;

    private String preferenceRecipients;

    private ProjectEntity project;

    private String defaultOutputReportLocation;

    private static final String defaultStatusDelay = Integer.toString(ConsoleMain.DEFAULT_SHOW_PROGRESS_DELAY);

    private static final String defaultPropertyFileName = "default.properties";

    private static final String ARG_RUN_MODE = ConsoleMain.RUN_MODE_OPTION;

    private static final String ARG_PROJECT_PATH = ConsoleMain.PROJECT_PK_OPTION;

    private static final String ARG_REPORT_FOLDER = ConsoleMain.REPORT_FOLDER_OPTION;

    private static final String ARG_REPORT_FILE_NAME = ConsoleMain.REPORT_FILE_NAME_OPTION;

    private static final String ARG_SEND_MAIL = "sendMail";

    private static final String ARG_OSGI_CONSOLE_LOG = "consoleLog";

    private static final String ARG_OSGI_NO_EXIT = "noExit";

    private static final String ARG_STATUS_DELAY = ConsoleMain.SHOW_STATUS_DELAY_OPTION;

    private static final String ARG_TEST_SUITE_PATH = ConsoleMain.TESTSUITE_ID_OPTION;

    private static final String ARG_REMOTE_WEB_DRIVER_URL = DriverFactory.REMOTE_WEB_DRIVER_URL;

    private static final String ARG_REMOTE_WEB_DRIVER_TYPE = DriverFactory.REMOTE_WEB_DRIVER_TYPE;

    private static final String ARG_MOBILE_DEVICE_ID = DriverFactory.EXECUTED_MOBILE_DEVICE_ID;

    private static final String ARG_BROWSER_TYPE = ConsoleMain.BROWSER_TYPE_OPTION;

    public GenerateCommandDialog(Shell parentShell, ProjectEntity project) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_GENERATE_COMMAND_FOR_CONSOLE);

        this.project = project;
        defaultOutputReportLocation = projectLocation() + File.separator + StringConstants.ROOT_FOLDER_NAME_REPORT
                + File.separator;

        IPreferenceStore prefs = ((IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                ExecutionPreferenceConstants.QUALIFIER));
        boolean isSendAttachmentPrefEnabled = prefs.getBoolean(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT);
        if (isSendAttachmentPrefEnabled) {
            preferenceRecipients = prefs.getString(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS);
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(550, 600);
    }

    @Override
    protected int getShellStyle() {
        return SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | getDefaultOrientation();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout(1, false);
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
        platformContainer.setLayout(new GridLayout(1, false));
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
    }

    private void createReportConfigPart(Composite parent) {
        Composite reportConfigContainer = new Composite(parent, SWT.NONE);
        reportConfigContainer.setLayout(new GridLayout(1, false));
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
        GridLayout glMailRecipientsBtnContainer = new GridLayout(1, false);
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
        optionsContainer.setLayout(new GridLayout(1, false));
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

        Label lblUpdateStatusTiming = new Label(grpOptionsContainer, SWT.NONE);
        lblUpdateStatusTiming.setText(StringConstants.DIA_LBL_UPDATE_EXECUTED_STATUS);

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
        comboBrowser.setItems(WebUIDriverType.stringValues());

        txtRemoteWebDriverURL.setEnabled(false);
        comboRemoteWebDriverType.setEnabled(false);
        comboRemoteWebDriverType.setItems(RemoteWebDriverConnectorType.stringValues());
        comboRemoteWebDriverType.select(0);

        comboMobileDevice.setEnabled(false);
        comboMobileDevice.setItems(getMobileDevices());

        txtOutputLocation.setText(absoluteToRelativePath(defaultOutputReportLocation, projectLocation()));
        chkUseRelativePath.setSelection(true);
        txtReportName.setText(StringConstants.DIA_TXT_DEFAULT_REPORT_NAME);
        listMailRecipient.setEnabled(false);
        updateRecipientList();
        enableMailRecipientButtons(chkSendEmail.getSelection());
        txtStatusDelay.setText(defaultStatusDelay);
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
                    txtTestSuite.setText(((TestSuiteEntity) tsTreeEntity.getObject()).getIdForDisplay());

                    updateRecipientList();
                } catch (Exception e) {
                    logError(e);
                }
            }
        });

        comboBrowser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isRemoteWebDriverSelected = browserTypeIs(WebUIDriverType.REMOTE_WEB_DRIVER);
                txtRemoteWebDriverURL.setEnabled(isRemoteWebDriverSelected);
                comboRemoteWebDriverType.setEnabled(isRemoteWebDriverSelected);
                comboMobileDevice.setEnabled(browserTypeIs(WebUIDriverType.ANDROID_DRIVER)
                        || browserTypeIs(WebUIDriverType.IOS_DRIVER));
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
                AddMailRecipientDialog addMailDialog = new AddMailRecipientDialog(shell, listMailRecipientViewer
                        .getList().getItems());
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

        txtStatusDelay.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                // Number input only
                if (!isNumeric(e.text)) {
                    e.doit = false;
                    return;
                }
            }
        });

        txtStatusDelay.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                correctStatusDelayInput();
            }

            @Override
            public void focusGained(FocusEvent e) {
                txtStatusDelay.selectAll();
            }
        });
    }

    private void correctStatusDelayInput() {
        String textNumber = txtStatusDelay.getText();
        if (isBlank(textNumber)) {
            // Default set to default if no input
            txtStatusDelay.setText(defaultStatusDelay);
            return;
        }

        if (textNumber.length() > 1 && textNumber.startsWith("0")) {
            // remove leading zeros in text number
            String numberValue = Integer.valueOf(textNumber).toString();
            txtStatusDelay.setText(numberValue);
        }
    }

    private void updateReportOutputLocation(String location) {
        location = location + File.separator;
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
        try {
            return MobileDriverFactory.getDevices().toArray(devices);
        } catch (Exception e) {
            logError(e);
            return devices;
        }
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

            String fileLocation = dialog.open();
            if (isBlank(fileLocation)) {
                return;
            }
            savePropertyFile(fileLocation);
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
        if (fileLocation == null) {
            throw new Exception(StringConstants.DIA_MSG_PLS_SPECIFY_FILE_LOCATION);
        }

        validateUserInput();
        Map<String, String> consoleAgrsMap = getUserConsoleAgrsMap();

        Properties prop = null;
        OutputStream output = null;
        try {
            output = new FileOutputStream(fileLocation);
            prop = new Properties();
            for (String key : consoleAgrsMap.keySet()) {
                // set the properties value
                prop.setProperty(key, consoleAgrsMap.get(key));
            }
            // save properties
            prop.store(output, null);
        } catch (IOException e) {
            logError(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logError(e);
                }
            }
        }
    }

    private String generateCommand() throws Exception {
        validateUserInput();

        Map<String, String> consoleAgrsMap = getUserConsoleAgrsMap();
        StringBuilder commandBuilder = new StringBuilder("katalon");
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

    private Map<String, String> getUserConsoleAgrsMap() {
        Map<String, String> args = new LinkedHashMap<String, String>();
        args.put(ARG_RUN_MODE, "console");
        args.put(ARG_PROJECT_PATH, wrapArgValue(project.getLocation()));

        if (useCustomReportFolder()) {
            args.put(ARG_REPORT_FOLDER, wrapArgValue(txtOutputLocation.getText()));

            // -reportFileName only affects when using with -reportFolder option
            if (!StringUtils.equals(txtReportName.getText(), StringConstants.DIA_TXT_DEFAULT_REPORT_NAME)) {
                args.put(ARG_REPORT_FILE_NAME, wrapArgValue(txtReportName.getText()));
            }
        }

        if (chkSendEmail.getSelection() && listMailRecipient.getItemCount() > 0) {
            args.put(ARG_SEND_MAIL, wrapArgValue(join(listMailRecipient.getItems(), MailUtil.EMAIL_SEPARATOR)));
        }

        if (chkDisplayConsoleLog.getSelection()) {
            // OSGi argument
            args.put(ARG_OSGI_CONSOLE_LOG, "");
        }

        if (chkKeepConsoleLog.getSelection()) {
            // OSGi argument
            args.put(ARG_OSGI_NO_EXIT, "");
        }

        if (!StringUtils.equals(txtStatusDelay.getText(), defaultStatusDelay)) {
            args.put(ARG_STATUS_DELAY, txtStatusDelay.getText());
        }

        args.put(ARG_TEST_SUITE_PATH, wrapArgValue(txtTestSuite.getText()));

        args.put(ARG_BROWSER_TYPE, comboBrowser.getText());

        if (browserTypeIs(WebUIDriverType.REMOTE_WEB_DRIVER)) {
            args.put(ARG_REMOTE_WEB_DRIVER_URL, wrapArgValue(txtRemoteWebDriverURL.getText()));
            args.put(ARG_REMOTE_WEB_DRIVER_TYPE, comboRemoteWebDriverType.getText());
            return args;
        }

        if (browserTypeIs(WebUIDriverType.ANDROID_DRIVER) || browserTypeIs(WebUIDriverType.IOS_DRIVER)) {
            args.put(ARG_MOBILE_DEVICE_ID, wrapArgValue(MobileDriverFactory.getDeviceId(comboMobileDevice.getText())));
            return args;
        }

        return args;
    }

    private boolean browserTypeIs(WebUIDriverType type) {
        return StringUtils.equals(comboBrowser.getText(), type.toString());
    }

    private String wrapArgName(String name) {
        return ConsoleMain.ARGUMENT_PREFIX + name;
    }

    private String wrapArgValue(String value) {
        return "\"" + value + "\"";
    }

    private boolean useCustomReportFolder() {
        return !StringUtils.equals(getReportOutputAbsolutePath(), defaultOutputReportLocation);
    }

    private void validateUserInput() throws Exception {
        List<String> messages = new ArrayList<String>();

        if (isBlank(txtTestSuite.getText())) {
            messages.add(StringConstants.DIA_MSG_PLS_SPECIFY_TEST_SUITE);
        }

        if (txtRemoteWebDriverURL.isEnabled() && isBlank(txtRemoteWebDriverURL.getText())) {
            messages.add(StringConstants.DIA_MSG_PLS_SPECIFY_REMOTE_WEB_DRIVER_URL);
        }

        if (comboMobileDevice.isEnabled() && comboMobileDevice.getSelectionIndex() == -1) {
            messages.add(StringConstants.DIA_MSG_PLS_SELECT_MOBILE_DEVICE);
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
