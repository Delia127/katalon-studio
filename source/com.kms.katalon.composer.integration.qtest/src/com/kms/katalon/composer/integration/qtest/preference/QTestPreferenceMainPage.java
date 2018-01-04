package com.kms.katalon.composer.integration.qtest.preference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;
import com.kms.katalon.composer.integration.qtest.constant.EventConstants;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.GenerateNewTokenDialog;
import com.kms.katalon.composer.integration.qtest.wizard.SetupWizardDialog;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.ReportFormatType;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.credential.QTestTokenManager;
import com.kms.katalon.integration.qtest.credential.impl.QTestCredentialImpl;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

public class QTestPreferenceMainPage extends PreferencePageWithHelp {

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;

    @Inject
    private IEventBroker eventBroker;

    // Controls
    private Text txtToken;

    private Button chckAutoSubmitTestRun, chckEnableIntegration, chckSubmitTestRunToLatestVersion;

    private Button btnOpenGenerateTokenDialog;

    private Composite container, mainComposite, includeAttachmentComposite;

    private GridData gdTxtToken;

    private Group grpReportFormatOptions;

    private Link setupLink;

    private Combo cbbQTestVersion;

    // Fields
    private String projectDir;

    private QTestCredentialImpl fCredential;

    public QTestPreferenceMainPage() {
        projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setLayout(new GridLayout(1, false));

        Composite enablerComposite = new Composite(container, SWT.NONE);
        enablerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        enablerComposite.setLayout(new GridLayout(2, false));

        chckEnableIntegration = new Button(enablerComposite, SWT.CHECK);
        chckEnableIntegration.setText(StringConstants.DIA_TITLE_ENABLE_INTEGRATION);

        setupLink = new Link(enablerComposite, SWT.NONE);
        setupLink.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        setupLink.setText(StringConstants.DIA_INFO_QUICK_SETUP);

        mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glMainComposite = new GridLayout(1, false);
        glMainComposite.marginWidth = 0;
        glMainComposite.marginHeight = 0;
        glMainComposite.verticalSpacing = 10;
        mainComposite.setLayout(glMainComposite);

        createAuthenticationGroup();

        createTestResultGroup();

        addToolItemListeners();
        initialize();

        return container;
    }

    private void createTestResultGroup() {
        Group testResultGroup = new Group(mainComposite, SWT.NONE);
        testResultGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        testResultGroup.setText(ComposerIntegrationQtestMessageConstants.CM_TEST_RESULT);
        GridLayout gdTestResult = new GridLayout(1, false);
        gdTestResult.verticalSpacing = 10;
        testResultGroup.setLayout(gdTestResult);

        chckSubmitTestRunToLatestVersion = new Button(testResultGroup, SWT.CHECK);
        chckSubmitTestRunToLatestVersion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        chckSubmitTestRunToLatestVersion.setText(StringConstants.DIA_TITLE_SUBMIT_TEST_RESULT_TO_LATEST_VERSION);

        Composite submitTestRunComposite = new Composite(testResultGroup, SWT.NONE);
        submitTestRunComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout gdSubmitTestRun = new GridLayout(1, false);
        gdSubmitTestRun.marginHeight = 0;
        gdSubmitTestRun.marginWidth = 0;
        submitTestRunComposite.setLayout(gdSubmitTestRun);

        chckAutoSubmitTestRun = new Button(submitTestRunComposite, SWT.CHECK);
        chckAutoSubmitTestRun.setText(StringConstants.DIA_TITLE_AUTO_SUBMIT_TEST_RESULT);

        includeAttachmentComposite = new Composite(submitTestRunComposite, SWT.NONE);
        includeAttachmentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout attachmentLayout = new GridLayout(1, true);
        attachmentLayout.marginLeft = 10;
        attachmentLayout.marginRight = 0;
        attachmentLayout.marginHeight = 0;
        includeAttachmentComposite.setLayout(attachmentLayout);

        for (QTestAttachmentSendingType sendingType : QTestAttachmentSendingType.values()) {
            Button btnSendingType = new Button(includeAttachmentComposite, SWT.CHECK);
            btnSendingType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

            switch (sendingType) {
                case SEND_IF_FAILS:
                    btnSendingType.setText(ComposerIntegrationQtestMessageConstants.DIA_LABEL_INCLUDE_ATTACH_IF_FAILS);
                    break;
                case SEND_IF_PASSES:
                    btnSendingType.setText(ComposerIntegrationQtestMessageConstants.DIA_LABEL_INCLUDE_ATTACH_IF_PASSES);
                    break;
                default:
                    break;
            }

            btnSendingType.setData(sendingType);
        }

        grpReportFormatOptions = new Group(mainComposite, SWT.NONE);
        grpReportFormatOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        grpReportFormatOptions.setText(StringConstants.DIA_TITLE_REPORT_FORMAT);

        GridLayout attachmentOptionsLayout = new GridLayout(1, true);
        attachmentOptionsLayout.marginLeft = 0;
        attachmentOptionsLayout.marginRight = 0;
        attachmentOptionsLayout.marginHeight = 5;
        grpReportFormatOptions.setLayout(attachmentOptionsLayout);

        for (ReportFormatType formatType : ReportFormatType.values()) {
            Button btnFormmatingType = new Button(grpReportFormatOptions, SWT.CHECK);
            btnFormmatingType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            btnFormmatingType.setText(formatType.toString());
            btnFormmatingType.setData(formatType);
        }
    }

    private void createAuthenticationGroup() {
        Group grpAuthentication = new Group(mainComposite, SWT.NONE);
        grpAuthentication.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        grpAuthentication.setLayout(new GridLayout(4, false));
        grpAuthentication.setText(StringConstants.CM_AUTHENTICATION);

        Composite versioningComposite = new Composite(grpAuthentication, SWT.NONE);
        versioningComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
        GridLayout glVersioningComposite = new GridLayout(2, false);
        glVersioningComposite.marginHeight = 0;
        glVersioningComposite.marginWidth = 0;
        versioningComposite.setLayout(glVersioningComposite);

        Label lblQTestVersion = new Label(versioningComposite, SWT.NONE);
        GridData gd_lblQTestVersion = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblQTestVersion.widthHint = 100;
        lblQTestVersion.setLayoutData(gd_lblQTestVersion);
        lblQTestVersion.setText(StringConstants.DIA_TITLE_VERSION);

        cbbQTestVersion = new Combo(versioningComposite, SWT.READ_ONLY | SWT.FLAT);
        cbbQTestVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        cbbQTestVersion.setItems(QTestVersion.valuesAsStrings());

        Label lblToken = new Label(grpAuthentication, SWT.NONE);
        GridData gdLblToken = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gdLblToken.widthHint = 100;
        gdLblToken.verticalIndent = 5;
        lblToken.setLayoutData(gdLblToken);
        lblToken.setText(StringConstants.CM_TOKEN);

        txtToken = new Text(grpAuthentication, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        gdTxtToken = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        gdTxtToken.heightHint = 60;
        txtToken.setLayoutData(gdTxtToken);

        btnOpenGenerateTokenDialog = new Button(grpAuthentication, SWT.NONE);
        btnOpenGenerateTokenDialog.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        btnOpenGenerateTokenDialog.setText(StringConstants.DIA_TITLE_GENERATE);
    }

    private void addToolItemListeners() {
        btnOpenGenerateTokenDialog.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                GenerateNewTokenDialog dialog = new GenerateNewTokenDialog(
                        btnOpenGenerateTokenDialog.getDisplay().getActiveShell(), fCredential);
                if (dialog.open() == Dialog.OK) {
                    fCredential = getNewCredential(dialog.getNewCredential());
                    txtToken.setText(fCredential.getToken().getRawToken());
                    MessageDialog.openInformation(shell, StringConstants.INFO,
                            StringConstants.DIA_MSG_GENERATE_TOKEN_SUCESSFULLY);
                }
            }
        });

        chckEnableIntegration.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableMainComposite();

                if (!chckEnableIntegration.getSelection()) {
                    return;
                }
                IntegratedEntity qTestProjectIntegratedEntity = QTestIntegrationUtil
                        .getIntegratedEntity(ProjectController.getInstance().getCurrentProject());
                if (QTestSettingStore.isTheFirstTime(projectDir) && (qTestProjectIntegratedEntity == null)) {
                    QTestSettingStore.usedSetupWizard(projectDir);

                    if (!MessageDialog.openQuestion(null, StringConstants.INFO,
                            StringConstants.DIA_TITLE_ASK_USE_SETUP)) {
                        return;
                    }
                    performWizardSetup();
                }
            }
        });

        txtToken.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                gdTxtToken.widthHint = Math.max(300, txtToken.getSize().x);
                container.setSize(container.getParent().getSize().x, container.getSize().y);
                container.layout(true, true);
            }
        });

        chckAutoSubmitTestRun.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                enableAttachmentsGroup();
            }
        });

        setupLink.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                performWizardSetup();
            }
        });

        cbbQTestVersion.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                fCredential.setVersion(QTestVersion.valueOf(cbbQTestVersion.getSelectionIndex()));
            }
        });

        txtToken.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                try {
                    fCredential.setToken(QTestTokenManager.getToken(txtToken.getText()));
                } catch (QTestInvalidFormatException ex) {}
            }
        });
    }

    private void performWizardSetup() {
        SetupWizardDialog wizard = new SetupWizardDialog(getShell());
        if (wizard.open() == Dialog.OK) {
            eventBroker.post(EventConstants.SETUP_FINISHED, null);
            initialize();
        }
    }

    private void enableAttachmentsGroup() {
        ControlUtils.recursiveSetEnabled(includeAttachmentComposite,
                chckEnableIntegration.getSelection() && chckAutoSubmitTestRun.getSelection());
    }

    private void enableMainComposite() {
        ControlUtils.recursiveSetEnabled(mainComposite, chckEnableIntegration.getSelection());
    }

    private void initialize() {
        boolean autoSubmitResult = QTestSettingStore.isAutoSubmitResultActive(projectDir);
        boolean isIntegrationActive = QTestSettingStore.isIntegrationActive(projectDir);
        boolean submitResultToLatestVersion = QTestSettingStore.isSubmitResultToLatestVersionActive(projectDir);

        chckAutoSubmitTestRun.setSelection(autoSubmitResult);
        chckEnableIntegration.setSelection(isIntegrationActive);
        chckSubmitTestRunToLatestVersion.setSelection(submitResultToLatestVersion);

        fCredential = getNewCredential(QTestSettingCredential.getCredential(projectDir));
        QTestVersion version = QTestVersion.getLastest();
        String token = "";
        if (fCredential != null) {
            if (fCredential.getToken() != null) {
                token = fCredential.getToken().getRawToken();
            }
            version = fCredential.getVersion();
        }
        txtToken.setText(token != null ? token : "");
        cbbQTestVersion.select(version.ordinal());

        // set input for grpAttachments
        List<QTestAttachmentSendingType> selectedAttachmentSendingTypes = QTestSettingStore
                .getAttachmentSendingTypes(projectDir);
        for (Control chckButton : includeAttachmentComposite.getChildren()) {
            if (chckButton instanceof Button) {
                if (selectedAttachmentSendingTypes.contains(chckButton.getData())) {
                    ((Button) chckButton).setSelection(true);
                } else {
                    ((Button) chckButton).setSelection(false);
                }
            }
        }

        // set input for grpFormattedOptions
        List<ReportFormatType> selectedFormattedReportTypes = QTestSettingStore.getFormatReportTypes(projectDir);
        for (Control chckButton : grpReportFormatOptions.getChildren()) {
            if (chckButton instanceof Button) {
                if (selectedFormattedReportTypes.contains(chckButton.getData())) {
                    ((Button) chckButton).setSelection(true);
                } else {
                    ((Button) chckButton).setSelection(false);
                }
            }
        }

        enableMainComposite();
        enableAttachmentsGroup();
        container.pack();
    }

    @Override
    public boolean performOk() {
        if (container == null) {
            return true;
        }

        try {
            QTestSettingStore.saveEnableIntegration(chckEnableIntegration.getSelection(), projectDir);

            QTestSettingStore.saveUserProfile(fCredential, projectDir);

            QTestSettingStore.saveAutoSubmit(chckAutoSubmitTestRun.getSelection(), projectDir);

            QTestSettingStore.saveSubmitToLatestVersion(chckSubmitTestRunToLatestVersion.getSelection(), projectDir);

            // Save sending result options
            saveAttachmentSendingStatus();
            saveFormatOptions();

            eventBroker.post(com.kms.katalon.constants.EventConstants.EXPLORER_REFRESH, null);
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR,
                    StringConstants.DIA_MSG_UNABLE_TO_SAVE_SETTING_PAGE);
            return false;
        }
    }

    private QTestCredentialImpl getNewCredential(IQTestCredential credential) {
        QTestCredentialImpl newCredential = new QTestCredentialImpl();
        if (credential != null) {
            newCredential.setServerUrl(credential.getServerUrl())
                    .setUsername(credential.getUsername())
                    .setPassword(credential.getPassword())
                    .setToken(credential.getToken())
                    .setVersion(credential.getVersion());
        }
        return newCredential;
    }

    private void saveAttachmentSendingStatus() {
        List<QTestAttachmentSendingType> selectedAttachmentSendingType = new ArrayList<QTestAttachmentSendingType>();
        for (Control radioButtonControl : includeAttachmentComposite.getChildren()) {
            if (radioButtonControl instanceof Button) {
                Button sendingTypeRadioButton = (Button) radioButtonControl;
                if (sendingTypeRadioButton.getSelection()) {
                    QTestAttachmentSendingType attachmentSendingType = (QTestAttachmentSendingType) sendingTypeRadioButton
                            .getData();
                    selectedAttachmentSendingType.add(attachmentSendingType);
                }
            }
        }
        QTestSettingStore.saveAttachmentSendingType(selectedAttachmentSendingType, projectDir);
    }

    private void saveFormatOptions() {
        List<ReportFormatType> selectedFormat = new ArrayList<ReportFormatType>();
        for (Control radioButtonControl : grpReportFormatOptions.getChildren()) {
            if (radioButtonControl instanceof Button) {
                Button formateTypeRadioButton = (Button) radioButtonControl;
                if (formateTypeRadioButton.getSelection()) {
                    ReportFormatType resultSendingType = (ReportFormatType) formateTypeRadioButton.getData();
                    selectedFormat.add(resultSendingType);
                }
            }
        }
        QTestSettingStore.saveFormatReportTypes(selectedFormat, projectDir);
    }

    @Override
    protected void performDefaults() {
        initialize();
    }

    @Override
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_QTEST;
    }
}
