package com.kms.katalon.composer.report.preference;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.constants.ComposerReportMessageConstants;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.helper.screenrecorder.VideoFileFormat;
import com.kms.katalon.core.helper.screenrecorder.VideoQuality;
import com.kms.katalon.execution.setting.ExecutionSettingStore;
import com.kms.katalon.core.setting.VideoRecorderSetting;

public class ReportSettingPage extends PreferencePage {

    private static final String[] VIDEO_FORMAT_ITEMS;

    private static final String[] VIDEO_QUALITY_ITEMS;

    static {
        VIDEO_FORMAT_ITEMS = Arrays.asList(VideoFileFormat.values())
                .stream()
                .map(format -> format.toString())
                .collect(Collectors.toList())
                .toArray(new String[0]);

        VIDEO_QUALITY_ITEMS = Arrays.asList(VideoQuality.values())
                .stream()
                .map(format -> format.getReadableName())
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    private Composite container;

    private Button btnCheckButton, btnEnableVideoRecorder;

    private Combo cbbVideoFormat, cbbVideoQuality;

    private Button btnRecordIfPassed, btnRecordIfFailed;

    private Composite videoRecorderComposite;

    private ExecutionSettingStore store;

    public ReportSettingPage() {
        store = new ExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setLayout(new GridLayout(1, false));

        btnCheckButton = new Button(container, SWT.CHECK);
        btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnCheckButton.setText(StringConstants.PAGE_TXT_ENABLE_TAKE_SCREENSHOT);

        Group grpVideoRecorder = new Group(container, SWT.NONE);
        GridLayout glGrpVideoRecorder = new GridLayout();
        grpVideoRecorder.setLayout(glGrpVideoRecorder);
        grpVideoRecorder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpVideoRecorder.setText(ComposerReportMessageConstants.PREF_TITLE_VIDEO_RECORDER);

        btnEnableVideoRecorder = new Button(grpVideoRecorder, SWT.CHECK);
        btnEnableVideoRecorder.setText(ComposerReportMessageConstants.PREF_CHCK_ENABLE_VIDEO_RECORDER);

        videoRecorderComposite = new Composite(grpVideoRecorder, SWT.NONE);
        videoRecorderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout videoRecorderLayout = new GridLayout(2, false);
        videoRecorderLayout.horizontalSpacing = 15;
        videoRecorderLayout.marginLeft = 5;
        videoRecorderComposite.setLayout(videoRecorderLayout);

        btnRecordIfPassed = new Button(videoRecorderComposite, SWT.CHECK);
        btnRecordIfPassed.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        btnRecordIfPassed.setText(ComposerReportMessageConstants.PREF_CHCK_RECORD_IF_PASSED);

        btnRecordIfFailed = new Button(videoRecorderComposite, SWT.CHECK);
        btnRecordIfFailed.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        btnRecordIfFailed.setText(ComposerReportMessageConstants.PREF_CHCK_RECORD_IF_FAILED);

        Label lblFormat = new Label(videoRecorderComposite, SWT.NONE);
        lblFormat.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblFormat.setText(ComposerReportMessageConstants.PREF_LBL_VIDEO_FORMAT);

        cbbVideoFormat = new Combo(videoRecorderComposite, SWT.READ_ONLY);
        GridData gdCbbVideoFormat = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbbVideoFormat.widthHint = 200;
        cbbVideoFormat.setLayoutData(gdCbbVideoFormat);
        cbbVideoFormat.setItems(VIDEO_FORMAT_ITEMS);

        Label lblQuality = new Label(videoRecorderComposite, SWT.NONE);
        lblQuality.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblQuality.setText(ComposerReportMessageConstants.PREF_LBL_VIDEO_QUALITY);

        cbbVideoQuality = new Combo(videoRecorderComposite, SWT.READ_ONLY);
        GridData gdCbbVideoQuality = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbbVideoQuality.widthHint = 200;
        cbbVideoQuality.setLayoutData(gdCbbVideoQuality);
        cbbVideoQuality.setItems(VIDEO_QUALITY_ITEMS);

        registerControlModifyListeners();

        updateInput();

        return container;
    }

    private void registerControlModifyListeners() {
        btnEnableVideoRecorder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ControlUtils.recursiveSetEnabled(videoRecorderComposite, btnEnableVideoRecorder.getSelection());
            }
        });
    }

    private void updateInput() {
        try {
            btnCheckButton.setSelection(store.getScreenCaptureOption());

            VideoRecorderSetting videoSetting = store.getVideoRecorderSetting();
            btnEnableVideoRecorder.setSelection(videoSetting.isEnable());

            btnRecordIfPassed.setSelection(videoSetting.isAllowedRecordIfPassed());
            btnRecordIfFailed.setSelection(videoSetting.isAllowedRecordIfFailed());

            cbbVideoFormat.select(videoSetting.getVideoFormat().ordinal());
            cbbVideoQuality.select(videoSetting.getVideoQuality().ordinal());

            ControlUtils.recursiveSetEnabled(videoRecorderComposite, btnEnableVideoRecorder.getSelection());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_READ_SETTINGS,
                    e.getMessage());
        }
    }

    protected void performDefaults() {
        updateInput();
        super.performApply();
    }

    @Override
    public boolean performOk() {
        if (container == null || container.isDisposed()) {
            return true;
        }
        try {
            store.setScreenCaptureOption(btnCheckButton.getSelection());
            VideoRecorderSetting videoSetting = new VideoRecorderSetting();
            videoSetting.setEnable(btnEnableVideoRecorder.getSelection());
            videoSetting.setAllowedRecordIfPassed(btnRecordIfPassed.getSelection());
            videoSetting.setAllowedRecordIfFailed(btnRecordIfFailed.getSelection());
            videoSetting.setVideoFormat(VideoFileFormat.values()[cbbVideoFormat.getSelectionIndex()]);
            videoSetting.setVideoQuality(VideoQuality.values()[cbbVideoQuality.getSelectionIndex()]);

            store.setVideoRecorderSetting(videoSetting);
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_UPDATE_SETTINGS,
                    e.getMessage());
            return false;
        }
    }
}
