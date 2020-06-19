package com.katalon.plugin.smart_xpath.settings;

import java.io.IOException;
import java.util.Collections;
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
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;
import com.kms.katalon.util.collections.Pair;

public class SelfHealingExecutionSettingPage extends AbstractSettingPage {

    private static final String LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD = SmartXPathMessageConstants.LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD;

    private Button checkboxEnableSelfHealing;

    private ExcludeObjectsUsedWithKeywordsComposite excludeObjectsUsedWithKeywordsComposite;

    private PrioritizeSelectionMethodsComposite prioritizeSelectionMethodsComposite;

    private final String documentationUrl = SmartXPathMessageConstants.SELF_HEALING_DOCUMENT_LINK;

    private List<Pair<SelectorMethod, Boolean>> methodsPriorityOrder = Collections.emptyList();

    private List<String> excludeKeywordNames = Collections.emptyList();

    private WebUiExecutionSettingStore preferenceStore;

    public SelfHealingExecutionSettingPage() {
        preferenceStore = WebUiExecutionSettingStore.getStore();
    }

    private void setInput() {
        try {
            excludeKeywordNames = preferenceStore.getExcludeKeywordList();
            methodsPriorityOrder = preferenceStore.getMethodsPriorityOrder();
        } catch (IOException exception) {
            LoggerSingleton.logError(exception);
        }

        excludeObjectsUsedWithKeywordsComposite.setInput(excludeKeywordNames);
        prioritizeSelectionMethodsComposite.setInput(methodsPriorityOrder);

        boolean isEnableSelfHealing = getEnableSelfHealingFromPluginPreference();
        checkboxEnableSelfHealing.setSelection(canUseSelfHealing() && isEnableSelfHealing);
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
                if (!canUseSelfHealing()) {
                    checkboxEnableSelfHealing.setSelection(false);
                    KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.SELF_HEALING);
                }

                handleInputChanged(checkboxEnableSelfHealing, null);
            }
        });
        new HelpComposite(parent, documentationUrl);
    }

    private List<Pair<SelectorMethod, Boolean>> createMethodsPriorityOrderComposite(Composite parent) {
        if (prioritizeSelectionMethodsComposite == null) {
            prioritizeSelectionMethodsComposite = new PrioritizeSelectionMethodsComposite(parent, SWT.NONE);
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

    private boolean getEnableSelfHealingFromPluginPreference() {
        return preferenceStore.getSelfHealingEnabled(canUseSelfHealing());
    }

    private List<Pair<SelectorMethod, Boolean>> getMethodsPriorityOrderFromPluginPreference() {
        List<Pair<SelectorMethod, Boolean>> value = null;
        try {
            value = preferenceStore.getMethodsPriorityOrder();
        } catch (IOException exception) {
            LoggerSingleton.logError(exception);
            return null;
        }
        return value;
    }

    private List<String> getExcludeKeywordsFromPluginPreference() {
        List<String> value = null;
        try {
            value = preferenceStore.getExcludeKeywordList();
        } catch (IOException exception) {
            LoggerSingleton.logError(exception);
        }
        return value;
    }

    @Override
    protected void initialize() throws IOException {
        setInput();
    }

    @Override
    protected void performDefaults() {
        try {
            preferenceStore.setEnableSelfHealing(canUseSelfHealing());
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
            preferenceStore.setExcludeKeywordList(excludeObjectsUsedWithKeywordsComposite.getInput());
            preferenceStore.setMethodsPritorityOrder(prioritizeSelectionMethodsComposite.getInput());

            if (canUseSelfHealing()) {
                preferenceStore.setEnableSelfHealing(checkboxEnableSelfHealing.getSelection());
            }
        } catch (IOException exception) {
            LoggerSingleton.logError(exception);
            return false;
        }
        return true;
    }

    @Override
    protected boolean hasChanged() {
        if (!isValid()) {
            return false;
        }

        boolean isEnableSelfHealing = checkboxEnableSelfHealing.getSelection();
        if (isEnableSelfHealing != this.getEnableSelfHealingFromPluginPreference()) {
            return true;
        }

        List<String> excludeKeywordNamesBeforeSetting = getExcludeKeywordsFromPluginPreference();
        if (!excludeObjectsUsedWithKeywordsComposite.compareInput(excludeKeywordNamesBeforeSetting)) {
            return true;
        }
        ;

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

    private boolean canUseSelfHealing() {
        IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();
        return featureService.canUse(KSEFeature.SELF_HEALING);
    }
}
