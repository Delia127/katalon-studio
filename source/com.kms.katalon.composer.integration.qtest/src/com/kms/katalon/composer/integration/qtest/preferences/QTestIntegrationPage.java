package com.kms.katalon.composer.integration.qtest.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.GenerateNewTokenDialog;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestResultSendingType;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationPage extends PreferencePage {
    private Text txtToken;
    private Button chckAutoSubmitTestRun;
    private Button chckEnableIntegration;
    private String projectDir;

    private Button btnOpenGenerateTokenDialog;
    private Composite container;
    private Composite mainComposite;
    private Composite projectComposite;
    private Group grpAuthentication;
    private GridData gdTxtToken;
    private Button chckEnableCheckBeforeUploading;
    private Group grpAttachmentOptions;
    private Group grpResultOptions;
    private Composite compositeOptions;

    public QTestIntegrationPage() {
        projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setLayout(new GridLayout(1, false));

        chckEnableIntegration = new Button(container, SWT.CHECK);
        chckEnableIntegration.setText(StringConstants.DIA_TITLE_ENABLE_INTEGRATION);

        mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glMainComposite = new GridLayout(1, false);
        glMainComposite.marginWidth = 0;
        glMainComposite.marginHeight = 0;
        mainComposite.setLayout(glMainComposite);

        grpAuthentication = new Group(mainComposite, SWT.NONE);
        grpAuthentication.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        grpAuthentication.setLayout(new GridLayout(4, false));
        grpAuthentication.setText(StringConstants.CM_AUTHENTICATION);

        Label lblToken = new Label(grpAuthentication, SWT.NONE);
        GridData gd_lblToken = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gd_lblToken.widthHint = 100;
        gd_lblToken.verticalIndent = 5;
        lblToken.setLayoutData(gd_lblToken);
        lblToken.setText(StringConstants.CM_TOKEN);

        txtToken = new Text(grpAuthentication, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        gdTxtToken = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        gdTxtToken.heightHint = 60;
        txtToken.setLayoutData(gdTxtToken);

        btnOpenGenerateTokenDialog = new Button(grpAuthentication, SWT.NONE);
        btnOpenGenerateTokenDialog.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        btnOpenGenerateTokenDialog.setText(StringConstants.DIA_TITLE_GENERATE);

        projectComposite = new Composite(mainComposite, SWT.NONE);
        projectComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glProjectComposite = new GridLayout(3, false);
        glProjectComposite.verticalSpacing = 10;
        projectComposite.setLayout(glProjectComposite);

        chckEnableCheckBeforeUploading = new Button(projectComposite, SWT.CHECK);
        chckEnableCheckBeforeUploading.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        chckEnableCheckBeforeUploading.setText(StringConstants.DIA_TITLE_CHECK_DUPLICATES_TEST_CASE);

        chckAutoSubmitTestRun = new Button(projectComposite, SWT.CHECK);
        chckAutoSubmitTestRun.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        chckAutoSubmitTestRun.setText(StringConstants.DIA_TITLE_AUTO_SUBMIT_TEST_RESULT);

        compositeOptions = new Composite(mainComposite, SWT.NONE);
        GridData gd_compositeOptions = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        gd_compositeOptions.verticalIndent = -5;
        compositeOptions.setLayoutData(gd_compositeOptions);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginLeft = 5;
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        compositeOptions.setLayout(glComposite);

        grpResultOptions = new Group(compositeOptions, SWT.NONE);
        grpResultOptions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        grpResultOptions.setText(StringConstants.DIA_TITLE_SEND_RESULT);
        grpResultOptions.setLayout(new GridLayout(4, false));

        for (QTestResultSendingType sendingType : QTestResultSendingType.values()) {
            Button btnSendingType = new Button(grpResultOptions, SWT.CHECK);
            btnSendingType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            btnSendingType.setText(sendingType.toString());
            btnSendingType.setData(sendingType);
        }

        grpAttachmentOptions = new Group(compositeOptions, SWT.NONE);
        grpAttachmentOptions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        grpAttachmentOptions.setText(StringConstants.DIA_TITLE_SEND_ATTACHMENT);
        grpAttachmentOptions.setLayout(new GridLayout(4, false));

        for (QTestAttachmentSendingType sendingType : QTestAttachmentSendingType.values()) {
            Button btnSendingType = new Button(grpAttachmentOptions, SWT.CHECK);
            btnSendingType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            btnSendingType.setText(sendingType.toString());
            btnSendingType.setData(sendingType);
        }

        addToolItemListeners();
        initilize();

        return container;
    }

    private void addToolItemListeners() {

        btnOpenGenerateTokenDialog.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                try {
                    GenerateNewTokenDialog dialog = new GenerateNewTokenDialog(btnOpenGenerateTokenDialog.getDisplay()
                            .getActiveShell(), QTestSettingStore.getServerUrl(projectDir), QTestSettingStore
                            .getUsername(projectDir), QTestSettingStore.getPassword(projectDir));
                    if (dialog.open() == Dialog.OK) {
                        txtToken.setText(dialog.getNewToken());
                    }
                } catch (Exception ex) {
                    MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.DIA_MSG_UNABLE_TO_UPDATE_PROJECT, ex
                            .getClass().getSimpleName());
                }
            }
        });

        chckEnableIntegration.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableMainComposite();
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
    }

    private void enableAttachmentsGroup() {
        if (chckEnableIntegration.getSelection() && chckAutoSubmitTestRun.getSelection()) {
            ControlUtil.recursiveSetEnabled(compositeOptions, true);
            compositeOptions.setEnabled(true);
        } else {
            ControlUtil.recursiveSetEnabled(compositeOptions, false);
            compositeOptions.setEnabled(false);
        }
    }

    private void enableMainComposite() {
        if (chckEnableIntegration.getSelection()) {
            ControlUtil.recursiveSetEnabled(mainComposite, true);
        } else {
            ControlUtil.recursiveSetEnabled(mainComposite, false);
        }
        enableAttachmentsGroup();
    }

    private void initilize() {
        String token = QTestSettingStore.getToken(projectDir);
        boolean autoSubmitResult = QTestSettingStore.isAutoSubmitResultActive(projectDir);
        boolean isIntegrationActive = QTestSettingStore.isIntegrationActive(projectDir);
        boolean isEnableCheckBeforeUploading = QTestSettingStore.isEnableCheckBeforeUploading(projectDir);

        txtToken.setText(token != null ? token : "");

        chckAutoSubmitTestRun.setSelection(autoSubmitResult);
        chckEnableIntegration.setSelection(isIntegrationActive);
        chckEnableCheckBeforeUploading.setSelection(isEnableCheckBeforeUploading);

        // set input for grpResults
        List<QTestResultSendingType> selectedResultSendingTypes = QTestSettingStore.getResultSendingTypes(projectDir);
        for (Control chckButton : grpResultOptions.getChildren()) {
            if (chckButton instanceof Button) {
                if (selectedResultSendingTypes.contains(chckButton.getData())) {
                    ((Button) chckButton).setSelection(true);
                } else {
                    ((Button) chckButton).setSelection(false);
                }
            }
        }

        // set input for grpAttachments
        List<QTestAttachmentSendingType> selectedAttachmentSendingTypes = QTestSettingStore
                .getAttachmentSendingTypes(projectDir);
        for (Control chckButton : grpAttachmentOptions.getChildren()) {
            if (chckButton instanceof Button) {
                if (selectedAttachmentSendingTypes.contains(chckButton.getData())) {
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
        if (container == null) return true;
        try {
            QTestSettingStore.saveAutoSubmit(chckAutoSubmitTestRun.getSelection(), projectDir);
            QTestSettingStore.saveToken(txtToken.getText(), projectDir);
            QTestSettingStore.saveEnableIntegration(chckEnableIntegration.getSelection(), projectDir);
            QTestSettingStore.saveEnableCheckBeforeUploading(chckEnableCheckBeforeUploading.getSelection(), projectDir);

            saveAttachmentSendingStatus();

            saveResultSendingStatus();

            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR,
                    StringConstants.DIA_MSG_UNABLE_TO_SAVE_SETTING_PAGE);
            return false;
        }
    }

    private void saveAttachmentSendingStatus() {
        List<QTestAttachmentSendingType> selectedAttachmentSendingType = new ArrayList<QTestAttachmentSendingType>();
        for (Control radioButtonControl : grpAttachmentOptions.getChildren()) {
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

    private void saveResultSendingStatus() {
        List<QTestResultSendingType> selectedResultSendingType = new ArrayList<QTestResultSendingType>();
        for (Control radioButtonControl : grpResultOptions.getChildren()) {
            if (radioButtonControl instanceof Button) {
                Button sendingTypeRadioButton = (Button) radioButtonControl;
                if (sendingTypeRadioButton.getSelection()) {
                    QTestResultSendingType resultSendingType = (QTestResultSendingType) sendingTypeRadioButton
                            .getData();
                    selectedResultSendingType.add(resultSendingType);
                }
            }
        }
        QTestSettingStore.saveResultSendingType(selectedResultSendingType, projectDir);
    }

    @Override
    protected void performDefaults() {
        initilize();
    }
}
