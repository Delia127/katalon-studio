package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.feature.KSEFeature;

public class WebUIExecutionSettingPage extends AbstractExecutionSettingPage {

    // Smart XPath-related functionality - supported only in commercial version
    @SuppressWarnings("unused")
    private static final String LBL_APPLY_NEIGHBOR_XPATHS = ExecutionMessageConstants.LBL_APPLY_NEIGHBOR_XPATHS;

    private WebUiExecutionSettingStore webUISettingStore;

    private ExecutionDefaultSettingStore defaultSettingStore;

    private Combo cbDefaultSmartWait;

    @SuppressWarnings("unused")
    private Button chckApplyNeighborXpaths;

    private Button chckEnableImageRecognition;

    private Text txtDefaultPageLoadTimeout, txtActionDelayInSecond, txtActionDelayInMilisecond, txtDefaultIEHangTimeout;

    private Button radioNotUsePageLoadTimeout, radioUsePageLoadTimeout, chckIgnorePageLoadTimeoutException,
            radioDelayBetweenActionsInSecond, radioDelayBetweenActionsInMilisecond;

    public WebUIExecutionSettingPage() {
        defaultSettingStore = ExecutionDefaultSettingStore.getStore();
        webUISettingStore = WebUiExecutionSettingStore.getStore();
    }

    @Override
    protected Composite createSettingsArea(Composite container) {
        createWebUISettingsComposite(container);
        return container;
    }

    private Composite createWebUISettingsComposite(Composite parent) {
        Composite settingsComposite = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.verticalSpacing = 10;
        glContainer.marginHeight = 0;
        glContainer.marginWidth = 0;
        settingsComposite.setLayout(glContainer);

        // Smart Wait
        Label lblDefaultSmartWait = new Label(settingsComposite, SWT.NONE);
        lblDefaultSmartWait.setText("Default Smart Wait");
        GridData gdLblDefaultSmartWait = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblDefaultSmartWait.setLayoutData(gdLblDefaultSmartWait);

        cbDefaultSmartWait = new Combo(settingsComposite, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
        GridData gdCbDefaultSmartWait = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCbDefaultSmartWait.widthHint = INPUT_WIDTH * 2;
        cbDefaultSmartWait.setLayoutData(gdCbDefaultSmartWait);

        // Image Recognition
        Group grpImageRecognition = new Group(settingsComposite, SWT.NONE);
        grpImageRecognition.setText("Image Recognition");
        GridLayout glGrpImageRecognition = new GridLayout(3, false);
        glGrpImageRecognition.marginLeft = 15;
        grpImageRecognition.setLayout(glGrpImageRecognition);
        grpImageRecognition.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        chckEnableImageRecognition = new Button(grpImageRecognition, SWT.CHECK);
        GridData gdChckEnableImageRecognition = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        chckEnableImageRecognition.setText("Enable Image Recognition");
        chckEnableImageRecognition.setLayoutData(gdChckEnableImageRecognition);

        /*
         * // Smart XPath's related functionality - only supported in commercial ver
         * Label lblApplyNeighborXpaths = new Label(comp, SWT.NONE);
         * lblApplyNeighborXpaths.setText(LBL_APPLY_NEIGHBOR_XPATHS);
         * GridData gdLblApplyNeighborXpaths = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
         * lblApplyNeighborXpaths.setLayoutData(gdLblApplyNeighborXpaths);
         * chckApplyNeighborXpaths= new Button(comp, SWT.CHECK);
         * GridData gdChckApplyNeighborXpaths = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
         * chckApplyNeighborXpaths.setLayoutData(gdChckApplyNeighborXpaths);
         */

        buildDefaultIEHangTimeoutComposite(settingsComposite);
        buildDefaultPageLoadTimeoutComposite(settingsComposite);
        buildDelayBetweenActionsComposite(settingsComposite);

        return settingsComposite;
    }

    private void buildDefaultIEHangTimeoutComposite(Composite parent) {
        Label lblDefaultIEHangTimeout = new Label(parent, SWT.NONE);
        lblDefaultIEHangTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT);
        lblDefaultIEHangTimeout.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtDefaultIEHangTimeout = new Text(parent, SWT.BORDER);
        GridData gdTxtDefaultIEHangTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtDefaultIEHangTimeout.widthHint = INPUT_WIDTH;
        txtDefaultIEHangTimeout.setLayoutData(gdTxtDefaultIEHangTimeout);
    }

    private void buildDelayBetweenActionsComposite(Composite parent) {
        Label lblActionDelay = new Label(parent, SWT.NONE);
        lblActionDelay.setText(ComposerExecutionMessageConstants.LBL_ACTION_DELAY);
        GridData gdLblActionDelay = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
        lblActionDelay.setLayoutData(gdLblActionDelay);

        Composite compPageLoad = new Composite(parent, SWT.NONE);
        GridLayout glCompPageLoad = new GridLayout(2, false);
        glCompPageLoad.marginWidth = 0;
        glCompPageLoad.marginHeight = 0;
        glCompPageLoad.marginLeft = 15;
        compPageLoad.setLayout(glCompPageLoad);
        compPageLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        radioDelayBetweenActionsInSecond = new Button(compPageLoad, SWT.RADIO);
        radioDelayBetweenActionsInSecond.setText(ComposerExecutionMessageConstants.PREF_LBL_ACTION_DELAY_IN_SECONDS);
        radioDelayBetweenActionsInSecond.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtActionDelayInSecond = new Text(compPageLoad, SWT.BORDER);
        GridData ldActionDelayInSecond = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        ldActionDelayInSecond.widthHint = INPUT_WIDTH;
        txtActionDelayInSecond.setLayoutData(ldActionDelayInSecond);

        radioDelayBetweenActionsInMilisecond = new Button(compPageLoad, SWT.RADIO);
        radioDelayBetweenActionsInMilisecond
                .setText(ComposerExecutionMessageConstants.PREF_LBL_ACTION_DELAY_IN_MILISECONDS);
        radioDelayBetweenActionsInMilisecond.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtActionDelayInMilisecond = new Text(compPageLoad, SWT.BORDER);
        GridData ldActionDelayInMiliisecond = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        ldActionDelayInMiliisecond.widthHint = INPUT_WIDTH;
        txtActionDelayInMilisecond.setLayoutData(ldActionDelayInMiliisecond);
    }

    private void buildDefaultPageLoadTimeoutComposite(Composite parent) {
        Label lblDefaultPageLoadTimeout = new Label(parent, SWT.NONE);
        lblDefaultPageLoadTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT);
        lblDefaultPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Composite compPageLoad = new Composite(parent, SWT.NONE);
        GridLayout glCompPageLoad = new GridLayout(2, false);
        glCompPageLoad.marginWidth = 0;
        glCompPageLoad.marginHeight = 0;
        glCompPageLoad.marginLeft = 15;
        compPageLoad.setLayout(glCompPageLoad);
        compPageLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        radioNotUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioNotUsePageLoadTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_ENABLE_DEFAULT_PAGE_LOAD_TIMEOUT);
        radioNotUsePageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        radioUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioUsePageLoadTimeout.setText(ComposerExecutionMessageConstants.PREF_LBL_CUSTOM_PAGE_LOAD_TIMEOUT);
        GridData gdRadioPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        radioUsePageLoadTimeout.setLayoutData(gdRadioPageLoadTimeout);

        txtDefaultPageLoadTimeout = new Text(compPageLoad, SWT.BORDER);
        GridData gdDefaultPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdDefaultPageLoadTimeout.widthHint = INPUT_WIDTH;
        txtDefaultPageLoadTimeout.setLayoutData(gdDefaultPageLoadTimeout);

        new Label(compPageLoad, SWT.NONE);
        chckIgnorePageLoadTimeoutException = new Button(compPageLoad, SWT.CHECK);
        chckIgnorePageLoadTimeoutException
                .setText(ComposerExecutionMessageConstants.PREF_LBL_IGNORE_DEFAULT_PAGE_LOAD_TIMEOUT_EXCEPTION);
        chckIgnorePageLoadTimeoutException.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    @Override
    protected void registerListeners() {
        chckEnableImageRecognition.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (LicenseUtil.isFreeLicense()) {
                    KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.IMAGE_BASED_OBJECT_DETECTION);
                    chckEnableImageRecognition.setSelection(false);
                }
            }
        });

        radioUsePageLoadTimeout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean usePageLoadTimeout = radioUsePageLoadTimeout.getSelection();
                txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
                chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
            }
        });

        radioDelayBetweenActionsInMilisecond.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtActionDelayInMilisecond.setEnabled(true);
                txtActionDelayInSecond.setEnabled(false);
            }
        });

        radioDelayBetweenActionsInSecond.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtActionDelayInSecond.setEnabled(true);
                txtActionDelayInMilisecond.setEnabled(false);
            }
        });

        addNumberVerification(txtActionDelayInSecond, TIMEOUT_MIN_VALUE_IN_SEC, TIMEOUT_MAX_VALUE_IN_SEC);
        addNumberVerification(txtActionDelayInMilisecond, TIMEOUT_MIN_VALUE_IN_MILISEC, TIMEOUT_MAX_VALUE_IN_MILISEC);
        addNumberVerification(txtDefaultIEHangTimeout, TIMEOUT_MIN_VALUE_IN_SEC, TIMEOUT_MAX_VALUE_IN_SEC);
        addNumberVerification(txtDefaultPageLoadTimeout, TIMEOUT_MIN_VALUE_IN_SEC, TIMEOUT_MAX_VALUE_IN_SEC);
    }

    @Override
    protected void initialize() throws IOException {
        Boolean selectedSmartWaitMode = defaultSettingStore.getDefaultSmartWaitMode();
        cbDefaultSmartWait.setItems(new String[] { "Enable", "Disable" });
        cbDefaultSmartWait.select(selectedSmartWaitMode.booleanValue() ? 0 : 1);

        /*
         * Smart XPath-related functionality - only supported in commercialized version
         * chckApplyNeighborXpaths.setSelection(defaultSettingStore.isAutoApplyNeighborXpathsEnabled());
         */

        if (chckEnableImageRecognition != null) {
            if (LicenseUtil.isNotFreeLicense()) {
                chckEnableImageRecognition.setSelection(webUISettingStore.getImageRecognitionEnabled());
            } else {
                chckEnableImageRecognition.setSelection(false);
            }
        }

        Boolean usePageLoadTimeout = webUISettingStore.getEnablePageLoadTimeout();
        radioUsePageLoadTimeout.setSelection(usePageLoadTimeout);
        radioNotUsePageLoadTimeout.setSelection(!usePageLoadTimeout);
        txtDefaultPageLoadTimeout.setText(String.valueOf(webUISettingStore.getPageLoadTimeout()));
        txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
        chckIgnorePageLoadTimeoutException.setSelection(webUISettingStore.getIgnorePageLoadTimeout());
        chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);

        radioDelayBetweenActionsInSecond
                .setSelection(webUISettingStore.getUseDelayActionTimeUnit().equals(TimeUnit.SECONDS));
        radioDelayBetweenActionsInMilisecond
                .setSelection(webUISettingStore.getUseDelayActionTimeUnit().equals(TimeUnit.MILLISECONDS));
        if (radioDelayBetweenActionsInSecond.getSelection()) {
            txtActionDelayInSecond.setText(String.valueOf(webUISettingStore.getActionDelay()));
            txtActionDelayInMilisecond.setEnabled(false);
        } else {
            txtActionDelayInMilisecond.setText(String.valueOf(webUISettingStore.getActionDelay()));
            txtActionDelayInSecond.setEnabled(false);
        }
        txtActionDelayInSecond.setMessage("Ex: 1");
        txtActionDelayInMilisecond.setMessage("Ex: 100");

        txtDefaultIEHangTimeout.setText(Integer.toString(webUISettingStore.getIEHangTimeout()));

        // if (!LicenseUtil.isNotFreeLicense()) {
        // gdCbLogTestSteps.heightHint = 0;
        // container.layout(true);
        // }
    }

    @Override
    protected void performDefaults() {
        if (container == null) {
            return;
        }

        if (chckEnableImageRecognition != null) {
            chckEnableImageRecognition
                    .setSelection(WebUiExecutionSettingStore.EXECUTION_DEFAULT_IMAGE_RECOGNITION_ENABLED);
        }

        cbDefaultSmartWait.setItems(new String[] { "Enable", "Disable" });
        cbDefaultSmartWait.select(0);

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
        txtActionDelayInSecond.setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY));
        txtDefaultIEHangTimeout
                .setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING));

        radioDelayBetweenActionsInSecond.setSelection(true);
        radioDelayBetweenActionsInMilisecond.setSelection(false);
        if (radioDelayBetweenActionsInSecond.getSelection()) {
            txtActionDelayInSecond.setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY));
        } else {
            txtActionDelayInMilisecond
                    .setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY));
        }

        try {
            webUISettingStore.setDefaultCapturedTestObjectAttributeLocators();
            webUISettingStore.setDefaultCapturedTestObjectXpathLocators();
            webUISettingStore.setDefaultCapturedTestObjectSelectorMethods();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected boolean saveSettings() {
        try {
            if (chckEnableImageRecognition != null) {
                webUISettingStore.setDefaultImageRecognitionEnabled(chckEnableImageRecognition.getSelection());
            }

            if (cbDefaultSmartWait != null) {
                defaultSettingStore.setDefaultSmartWaitMode(
                        cbDefaultSmartWait.getSelectionIndex() == 0 ? Boolean.valueOf(true) : Boolean.valueOf(false));
            }

            /*
             * if (chckApplyNeighborXpaths != null) {
             * defaultSettingStore.setApplyNeighborXpathsEnabled(chckApplyNeighborXpaths.getSelection());
             * }
             */

            if (radioUsePageLoadTimeout != null) {
                webUISettingStore.setEnablePageLoadTimeout(radioUsePageLoadTimeout.getSelection());
            }

            if (txtDefaultPageLoadTimeout != null) {
                webUISettingStore.setPageLoadTimeout(Integer.parseInt(txtDefaultPageLoadTimeout.getText()));
            }

            if (chckIgnorePageLoadTimeoutException != null) {
                webUISettingStore.setIgnorePageLoadTimeout(chckIgnorePageLoadTimeoutException.getSelection());
            }

            if (txtActionDelayInSecond != null) {
                TimeUnit chosenTimeUnit = null;
                String secText = txtActionDelayInSecond.getText();
                String milisecText = txtActionDelayInMilisecond.getText();
                secText = (StringUtils.EMPTY.equals(secText))
                        ? String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY) : secText;
                milisecText = (StringUtils.EMPTY.equals(milisecText))
                        ? String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY) : milisecText;

                if (radioDelayBetweenActionsInSecond != null) {
                    if (radioDelayBetweenActionsInSecond.getSelection()) {
                        chosenTimeUnit = TimeUnit.SECONDS;
                    }
                }
                if (radioDelayBetweenActionsInMilisecond != null) {
                    if (radioDelayBetweenActionsInMilisecond.getSelection()) {
                        chosenTimeUnit = TimeUnit.MILLISECONDS;
                    }
                }
                webUISettingStore.setUseDelayActionTimeUnit(chosenTimeUnit);
                webUISettingStore.setActionDelay(
                        Integer.parseInt(chosenTimeUnit.equals(TimeUnit.SECONDS) ? secText : milisecText));
            }

            if (txtDefaultIEHangTimeout != null) {
                webUISettingStore.setIEHangTimeout(Integer.parseInt(txtDefaultIEHangTimeout.getText()));
            }
        } catch (IOException error) {
            LoggerSingleton.logError(error);
            return false;
        }

        return true;
    }
}
