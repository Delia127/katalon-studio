package com.kms.katalon.composer.execution.preferences;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.application.utils.ApplicationProxyUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class SystemProxyConfigurationPreferencesPage extends AbstractProxyConfigurationPreferencesPage {

    private Button chkAutoApplyToDesiredCapabilities;

    @Override
    protected String getGuideMessage() {
        return MessageConstants.LBL_SYSTEM_PROXY_GUIDE_MESSAGE;
    }

    @Override
    protected void createFooterComposite(Composite parent) {
        chkAutoApplyToDesiredCapabilities = new Button(parent, SWT.CHECK);
        chkAutoApplyToDesiredCapabilities.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        chkAutoApplyToDesiredCapabilities.setText(MessageConstants.CHK_TEXT_AUTO_APPLY_TO_DESIRED_CAPABILITIES);
        chkAutoApplyToDesiredCapabilities.setSelection(getProxyInfo().isApplyToDesiredCapabilities());
    }

    @Override
    protected void selectNoProxyOption() {
        super.selectNoProxyOption();
        chkAutoApplyToDesiredCapabilities.setEnabled(false);
    }

    @Override
    protected void selectManualConfigProxyOption() {
        super.selectManualConfigProxyOption();
        chkAutoApplyToDesiredCapabilities.setEnabled(true);
    }

    @Override
    protected void selectSystemProxyOption() {
        super.selectSystemProxyOption();
        chkAutoApplyToDesiredCapabilities.setEnabled(true);
    }

    @Override
    protected ProxyInformation getProxyInfo() {
        return ProxyPreferences.isSystemProxyPreferencesSet()
                ? ProxyPreferences.getSystemProxyInformation()
                : ApplicationProxyUtil.getSystemProxyInformation();
    }

    @Override
    protected void saveProxyInfo(ProxyInformation proxyInfo) throws IOException {
        proxyInfo.setApplyToDesiredCapabilities(chkAutoApplyToDesiredCapabilities.getSelection());
        ProxyPreferences.saveSystemProxyInformation(proxyInfo);
    }
}
