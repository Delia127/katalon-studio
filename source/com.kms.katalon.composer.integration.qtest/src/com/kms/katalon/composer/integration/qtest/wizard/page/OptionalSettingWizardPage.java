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
import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestReportFormatType;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class OptionalSettingWizardPage extends AbstractWizardPage implements QTestWizardPage {

    private Button chckAutoSubmitTestRun, chckSubmitTestRunToLatestVersion;

    private Group grpReportFormatOptions;

    private Composite includeAttachmentComposite;

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    @Override
    public String getStepIndexAsString() {
        return "4";
    }

    @Override
    public boolean isChild() {
        return false;
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

        for (QTestReportFormatType formatType : QTestReportFormatType.values()) {
            Button btnFormmatingType = new Button(grpReportFormatOptions, SWT.CHECK);
            btnFormmatingType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            btnFormmatingType.setText(formatType.toString());
            btnFormmatingType.setData(formatType);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void setInput(final Map<String, Object> sharedData) {
        Object isEnableAutoSubmit = sharedData.get(QTestSettingStore.AUTO_SUBMIT_RESULT_PROPERTY);
        if (isEnableAutoSubmit != null && isEnableAutoSubmit instanceof Boolean) {
            chckAutoSubmitTestRun.setSelection((boolean) isEnableAutoSubmit);
        }

        Object isSubmitToLatestVersion = sharedData.get(QTestSettingStore.SUBMIT_RESULT_TO_LATEST_VERSION);
        if (isSubmitToLatestVersion != null && isSubmitToLatestVersion instanceof Boolean) {
            chckSubmitTestRunToLatestVersion.setSelection((boolean) isSubmitToLatestVersion);
        }

        // set input for grpAttachment
        List<QTestAttachmentSendingType> selectedAttachmentSendingType = (List<QTestAttachmentSendingType>) sharedData
                .get(QTestSettingStore.SEND_ATTACHMENTS_PROPERTY);
        if (selectedAttachmentSendingType != null) {
            for (Control chckButton : grpReportFormatOptions.getChildren()) {
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

        List<QTestReportFormatType> reportFormatType = (List<QTestReportFormatType>) sharedData
                .get(QTestSettingStore.REPORT_FORMAT);
        if (reportFormatType != null) {
            for (Control chckButton : grpReportFormatOptions.getChildren()) {
                if (!(chckButton instanceof Button)) {
                    continue;
                }

                if (reportFormatType.contains(chckButton.getData())) {
                    ((Button) chckButton).setSelection(true);
                } else {
                    ((Button) chckButton).setSelection(false);
                }
            }
        }
        enableAttachmentsGroup();
    }

    private void enableAttachmentsGroup() {
        ControlUtils.recursiveSetEnabled(includeAttachmentComposite, chckAutoSubmitTestRun.getSelection());
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
        sharedData.put(QTestSettingStore.SUBMIT_RESULT_TO_LATEST_VERSION,
                chckSubmitTestRunToLatestVersion.getSelection());

        saveAttachmentSendingStatus(sharedData);
        saveReportFormat(sharedData);
        return sharedData;
    }

    private void saveAttachmentSendingStatus(Map<String, Object> sharedData) {
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
        sharedData.put(QTestSettingStore.SEND_ATTACHMENTS_PROPERTY, selectedAttachmentSendingType);
    }

    private void saveReportFormat(Map<String, Object> sharedData) {
        List<QTestReportFormatType> selectedResultSendingType = new ArrayList<QTestReportFormatType>();
        for (Control radioButtonControl : grpReportFormatOptions.getChildren()) {
            if (radioButtonControl instanceof Button) {
                Button sendingTypeRadioButton = (Button) radioButtonControl;
                if (sendingTypeRadioButton.getSelection()) {
                    QTestReportFormatType resultSendingType = (QTestReportFormatType) sendingTypeRadioButton.getData();
                    selectedResultSendingType.add(resultSendingType);
                }
            }
        }
        sharedData.put(QTestSettingStore.REPORT_FORMAT, selectedResultSendingType);
    }

    @Override
    public String getTitle() {
        return StringConstants.WZ_P_OPTIONAL_TITLE;
    }
}
