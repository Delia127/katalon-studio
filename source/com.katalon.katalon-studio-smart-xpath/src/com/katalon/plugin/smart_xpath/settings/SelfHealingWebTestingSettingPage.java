package com.katalon.plugin.smart_xpath.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;

public class SelfHealingWebTestingSettingPage extends PreferencePageWithHelp {
	
    private ExecutionDefaultSettingStore defaultSettingStore;
    
    private Composite container;

	public SelfHealingWebTestingSettingPage() {
        defaultSettingStore = ExecutionDefaultSettingStore.getStore();
	}

	@Override
	protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

		return container;
	}
}
