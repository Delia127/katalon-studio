package com.katalon.plugin.smart_xpath.settings;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.katalon.plugin.smart_xpath.settings.composites.ExcludeObjectsUsedWithKeywordsComposite;
import com.katalon.plugin.smart_xpath.settings.composites.PrioritizeSelectionMethodsComposite;
import com.kms.katalon.composer.components.controls.HelpComposite;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.util.collections.Pair;
import com.kms.katalon.core.testobject.SelectorMethod;

public class SelfHealingExecutionSettingPage extends AbstractSettingPage {

	private static final String LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD = SmartXPathMessageConstants.LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD;

	private Button checkboxEnableSelfHealing;

//	private SelfHealingSetting preferenceStore;

	private ExcludeObjectsUsedWithKeywordsComposite excludeObjectsUsedWithKeywordsComposite;

	private PrioritizeSelectionMethodsComposite prioritizeSelectionMethodsComposite;

	private final String documentationUrl = null;

	private boolean isEnableSelfHealing;

	private List<Pair<SelectorMethod, Boolean>> methodsPriorityOrder;

	private List<String> excludeKeywordNames;

    private WebUiExecutionSettingStore preferenceStore;

    public SelfHealingExecutionSettingPage() {
        preferenceStore = WebUiExecutionSettingStore.getStore();
    }

	private void setInput() {
        try {
            excludeKeywordNames = preferenceStore.getExcludeKeywordList();
            methodsPriorityOrder = preferenceStore.getMethodsPriorityOrder();
        } catch (IOException e) {
            e.printStackTrace();
        }

		excludeObjectsUsedWithKeywordsComposite.setInput(excludeKeywordNames);
		prioritizeSelectionMethodsComposite.setInput(methodsPriorityOrder);

		isEnableSelfHealing = getEnableSelfHealingFromPluginPreference();
		checkboxEnableSelfHealing.setSelection(isEnableSelfHealing);
	}

	@Override
	protected void createSettingsArea(Composite parent) {
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

	}

	private void createCheckboxEnableSelfHealing(Composite parent) {
		checkboxEnableSelfHealing = new Button(parent, SWT.CHECK | SWT.NONE);
		checkboxEnableSelfHealing.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		checkboxEnableSelfHealing.setText(LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD);
		checkboxEnableSelfHealing.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				isEnableSelfHealing = !isEnableSelfHealing;

				handleInputChanged(checkboxEnableSelfHealing, null);
			}
		});
		new HelpComposite(parent, documentationUrl);
	}

	private List<Pair<SelectorMethod, Boolean>> createMethodsPriorityOrderComposite(Composite parent) {
		if (prioritizeSelectionMethodsComposite == null) {
			prioritizeSelectionMethodsComposite = new PrioritizeSelectionMethodsComposite(parent, SWT.NONE,
					preferenceStore);
			prioritizeSelectionMethodsComposite.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					handleInputChanged(prioritizeSelectionMethodsComposite, null);
				}
			});
		}
		return methodsPriorityOrder;
	}

	private Composite createExcludeWithKeywordsComposite(Composite parent) {
		if (excludeObjectsUsedWithKeywordsComposite == null) {
			excludeObjectsUsedWithKeywordsComposite = new ExcludeObjectsUsedWithKeywordsComposite(parent, SWT.NONE,
					preferenceStore);
			excludeObjectsUsedWithKeywordsComposite.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					handleInputChanged(excludeObjectsUsedWithKeywordsComposite, null);
				}
			});
		}
		return excludeObjectsUsedWithKeywordsComposite;
	}

	private Boolean getEnableSelfHealingFromPluginPreference() {
		Boolean value;
        try {
            value = preferenceStore.isEnableSelfHealing();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
		return value;
	}

	private void setEnableSelfHealingIntoPluginPreference() throws IOException {
		preferenceStore.setEnableSelfHealing(isEnableSelfHealing);
	}

	public void setUpdatedMethodsPriorityOrderIntoPluginPreference() throws IOException {
		preferenceStore.setMethodsPritorityOrder(prioritizeSelectionMethodsComposite.getInput());
	}

	private List<Pair<SelectorMethod, Boolean>> getMethodsPriorityOrderFromPluginPreference() {
		List<Pair<SelectorMethod, Boolean>> value = null;
		try {
			value = preferenceStore.getMethodsPriorityOrder();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return value;
	}

	private List<String> getExcludeKeywordsFromPluginPreference() {
		List<String> value = null;
		try {
			value = preferenceStore.getExcludeKeywordList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setUpdatedExcludeKeywordsIntoPluginPreference() throws IOException {
		preferenceStore.setExcludeKeywordList(excludeObjectsUsedWithKeywordsComposite.getInput());
	}

    @Override
    protected void initialize() throws IOException {
        setInput();
    }

    @Override
    protected void performDefaults() {
        try {
            preferenceStore.setDefaultEnableSelfHealing();
            preferenceStore.setDefaultMethodsPriorityOrder();
            preferenceStore.setDefaultExcludeKeywordList();

            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        super.performDefaults();
    }

	@Override
	protected boolean saveSettings() {
		try {
			this.setUpdatedExcludeKeywordsIntoPluginPreference();
			this.setUpdatedMethodsPriorityOrderIntoPluginPreference();
			this.setEnableSelfHealingIntoPluginPreference();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected boolean hasChanged() {
		if (!isValid()) {
			return false;
		}

		if (isEnableSelfHealing != this.getEnableSelfHealingFromPluginPreference()) {
			return true;
		}

		List<String> excludeKeywordNamesBeforeSetting = getExcludeKeywordsFromPluginPreference();
		if (!excludeObjectsUsedWithKeywordsComposite.compareInput(excludeKeywordNamesBeforeSetting)) {
			return true;
		};

		List<Pair<SelectorMethod, Boolean>> methodsPriorityOrderSetting = getMethodsPriorityOrderFromPluginPreference();
		if (!prioritizeSelectionMethodsComposite.compareInput(methodsPriorityOrderSetting)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasDocumentation() {
		return false;
	}
}
