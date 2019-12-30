package com.kms.katalon.composer.execution.settings;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;

public class LaunchArgumentsSettingPage extends PreferencePageWithHelp {
    
    private ExecutionDefaultSettingStore settingStore = ExecutionDefaultSettingStore.getStore();

    private Composite container;
    
    private Text txtVmArgs;
    
    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        
        if (LicenseUtil.isNotFreeLicense()) {
            Label lblVmArgs = new Label(container, SWT.NONE);
            lblVmArgs.setText("VM Arguments");
            
            txtVmArgs = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
            GridData gdVmArgs = new GridData(SWT.FILL, SWT.FILL, true, false);
            gdVmArgs.heightHint = 75;
            txtVmArgs.setLayoutData(gdVmArgs);
            txtVmArgs.setText(settingStore.getVmArgs());
        }
        
        return container;
    }
    
    @Override
    protected void performApply() {
        if (container == null) {
            return;
        }
        
        try {
            if (txtVmArgs != null) {
                String vmArgs = txtVmArgs.getText();
                settingStore.setVmArgs(vmArgs.trim());
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        if (super.performOk() && isValid()) {
            performApply();
        }
        return true;
    }
    
    @Override
    public boolean hasDocumentation() {
        return false;
    }
    
    @Override
    public String getDocumentationUrl() {
        return StringUtils.EMPTY;
    }
}
