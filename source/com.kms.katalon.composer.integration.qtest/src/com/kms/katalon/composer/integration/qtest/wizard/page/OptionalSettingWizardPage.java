package com.kms.katalon.composer.integration.qtest.wizard.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.wizard.AbstractWizardPage;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestResultSendingType;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class OptionalSettingWizardPage extends AbstractWizardPage {

    private Button chckAutoSubmitTestRun;
    private Composite compositeOptions;
    private Group grpResultOptions;
    private Group grpAttachmentOptions;

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createStepArea(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));

        Composite headerComposite = new Composite(mainComposite, SWT.NONE);
        headerComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblNewLabel = new Label(headerComposite, SWT.NONE);
        lblNewLabel.setText(StringConstants.WZ_P_OPTIONAL_INFO);

        Composite settingComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout gl_settingComposite = new GridLayout(1, false);
        gl_settingComposite.verticalSpacing = 10;
        settingComposite.setLayout(gl_settingComposite);
        settingComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        chckAutoSubmitTestRun = new Button(settingComposite, SWT.CHECK);
        chckAutoSubmitTestRun.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        chckAutoSubmitTestRun.setText(StringConstants.DIA_TITLE_AUTO_SUBMIT_TEST_RESULT);

        compositeOptions = new Composite(mainComposite, SWT.NONE);
        GridData gd_compositeOptions = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_compositeOptions.verticalIndent = -5;
        compositeOptions.setLayoutData(gd_compositeOptions);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginLeft = 25;
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

    }

    @SuppressWarnings("unchecked")
    @Override
    public void setInput(final Map<String, Object> sharedData) {
        Object isEnableAutoSubmit = sharedData.get(QTestSettingStore.AUTO_SUBMIT_RESULT_PROPERTY);
        if (isEnableAutoSubmit != null && isEnableAutoSubmit instanceof Boolean) {
            chckAutoSubmitTestRun.setSelection((boolean) isEnableAutoSubmit);
        }

        // set input for grpResults
        List<QTestResultSendingType> selectedResultSendingTypes = (List<QTestResultSendingType>) sharedData
                .get(QTestSettingStore.SEND_RESULT_PROPERTY);
        if (selectedResultSendingTypes != null) {
            for (Control chckButton : grpResultOptions.getChildren()) {
                if (!(chckButton instanceof Button)) {
                    continue;
                }

                if (selectedResultSendingTypes.contains(chckButton.getData())) {
                    ((Button) chckButton).setSelection(true);
                } else {
                    ((Button) chckButton).setSelection(false);
                }
            }
        }

        // set input for grpAttachment
        List<QTestAttachmentSendingType> selectedAttachmentSendingType = (List<QTestAttachmentSendingType>) sharedData
                .get(QTestSettingStore.SEND_ATTACHMENTS_PROPERTY);
        if (selectedAttachmentSendingType != null) {
            for (Control chckButton : grpAttachmentOptions.getChildren()) {
                if (!(chckButton instanceof Button)) {
                    continue;
                }

                if (selectedAttachmentSendingType.contains(chckButton.getData())) {
                    ((Button) chckButton).setSelection(true);
                } else {
                    ((Button) chckButton).setSelection(false);
                }
            }
        }
        enableAttachmentsGroup();
    }

    private void enableAttachmentsGroup() {
        if (chckAutoSubmitTestRun.getSelection()) {
            ControlUtils.recursiveSetEnabled(compositeOptions, true);
            compositeOptions.setEnabled(true);
        } else {
            ControlUtils.recursiveSetEnabled(compositeOptions, false);
            compositeOptions.setEnabled(false);
        }
    }

    @Override
    public void registerControlModifyListeners() {
        chckAutoSubmitTestRun.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                enableAttachmentsGroup();
            }
        });
    }

    @Override
    public Map<String, Object> storeControlStates() {
        Map<String, Object> sharedData = new HashMap<String, Object>();
        sharedData.put(QTestSettingStore.AUTO_SUBMIT_RESULT_PROPERTY, chckAutoSubmitTestRun.getSelection());

        saveAttachmentSendingStatus(sharedData);
        saveResultSendingStatus(sharedData);

        return sharedData;
    }

    private void saveAttachmentSendingStatus(Map<String, Object> sharedData) {
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
        sharedData.put(QTestSettingStore.SEND_ATTACHMENTS_PROPERTY, selectedAttachmentSendingType);
    }

    private void saveResultSendingStatus(Map<String, Object> sharedData) {
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
        sharedData.put(QTestSettingStore.SEND_RESULT_PROPERTY, selectedResultSendingType);
    }

    @Override
    public String getTitle() {
        return StringConstants.WZ_P_OPTIONAL_TITLE;
    }
}
