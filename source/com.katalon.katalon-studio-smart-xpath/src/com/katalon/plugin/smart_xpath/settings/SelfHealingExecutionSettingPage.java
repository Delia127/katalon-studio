package com.katalon.plugin.smart_xpath.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.katalon.platform.api.exception.InvalidDataTypeFormatException;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.Entity;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.katalon.plugin.smart_xpath.settings.composites.ExcludeObjectsUsedWithKeywordsComposite;
import com.katalon.plugin.smart_xpath.settings.composites.PrioritizeSelectionMethodsComposite;
import com.kms.katalon.composer.components.controls.HelpComposite;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;

public class SelfHealingExecutionSettingPage extends PreferencePageWithHelp {

	private static final String LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD = SmartXPathMessageConstants.LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD;

	private Button checkboxEnableSelfHealing;

	private Composite container;

	private SelfHealingSetting preferenceStore;

	private ExcludeObjectsUsedWithKeywordsComposite excludeObjectsUsedWithKeywordsComposite;

	private PrioritizeSelectionMethodsComposite prioritizeSelectionMethodsComposite;

	private final String documentationUrl = null;
	
	private boolean isEnableSelfHealing;

	public SelfHealingExecutionSettingPage() {
		generatePreferenceStore();
		getEnableSelfHealingFromPluginPreference();
	}

	private void generatePreferenceStore() {
		Entity currentProject = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
		preferenceStore = SelfHealingSetting.getStore(currentProject);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite mainContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		layout.verticalSpacing = 10;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainContainer.setLayout(layout);

		createCheckboxEnableSelfHealing(mainContainer);
		createMethodsPriorityOrderComposite(mainContainer);
		createExcludeWithKeywordsComposite(mainContainer);
		return container;
	}

	private void createCheckboxEnableSelfHealing(Composite parent) {
		checkboxEnableSelfHealing = new Button(parent, SWT.CHECK | SWT.NONE);
		checkboxEnableSelfHealing.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		checkboxEnableSelfHealing.setText(LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD);
		checkboxEnableSelfHealing.setSelection(isEnableSelfHealing);
		checkboxEnableSelfHealing.addSelectionListener(new SelectionAdapter() {

	        @Override
	        public void widgetSelected(SelectionEvent event) {
	        	isEnableSelfHealing = !isEnableSelfHealing;
	        }
	    });
		new HelpComposite(parent, documentationUrl);
	}

	private void createMethodsPriorityOrderComposite(Composite parent) {
		prioritizeSelectionMethodsComposite = new PrioritizeSelectionMethodsComposite(parent, SWT.NONE, preferenceStore);
	}

	private void createExcludeWithKeywordsComposite(Composite parent) {
		excludeObjectsUsedWithKeywordsComposite = new ExcludeObjectsUsedWithKeywordsComposite(parent, SWT.NONE, preferenceStore);
	}

	private void getEnableSelfHealingFromPluginPreference() {
		try {
			isEnableSelfHealing = preferenceStore.isEnableSelfHHealing();
		} catch (InvalidDataTypeFormatException | ResourceException e) {
			e.printStackTrace();
		}
	}

	private void setEnableSelfHealingIntoPluginPreference() {
		try {
			preferenceStore.setEnableSelfHealing(isEnableSelfHealing);
		} catch (ResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void performApply() {
		excludeObjectsUsedWithKeywordsComposite.setUpdatedExcludeKeywordsIntoPluginPreference();
		prioritizeSelectionMethodsComposite.setUpdatedMethodsPriorityOrderIntoPluginPreference();
		this.setEnableSelfHealingIntoPluginPreference();
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
}
