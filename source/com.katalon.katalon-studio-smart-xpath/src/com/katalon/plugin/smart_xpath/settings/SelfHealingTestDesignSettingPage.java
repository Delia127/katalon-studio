package com.katalon.plugin.smart_xpath.settings;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.katalon.plugin.smart_xpath.settings.composites.AttributesSelectionComposite;
import com.katalon.plugin.smart_xpath.settings.composites.DefaultWebLocatorSelectionComposite;
import com.katalon.plugin.smart_xpath.settings.composites.XPathsSelectionComposite;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;
import com.kms.katalon.util.collections.Pair;

public class SelfHealingTestDesignSettingPage extends AbstractSettingPage {

    @SuppressWarnings("serial")
    private final Map<String, SelectorMethod> defaultLocatorOptions = new LinkedHashMap<String, SelectorMethod>() {
        {
            put("XPath", SelectorMethod.XPATH);
            put("Attributes", SelectorMethod.BASIC);
        }
    };

    private DefaultWebLocatorSelectionComposite defaultWebLocatorComposite;

    private XPathsSelectionComposite XPathComposite;

    private AttributesSelectionComposite attributesComposite;

    @Override
    protected void createSettingsArea(Composite containter) {
        createDefaultLocatorSelectionArea(container);
        createLocatorPriorityTabs(container);
    }

    private Composite createDefaultLocatorSelectionArea(Composite parent) {
        if (defaultWebLocatorComposite == null) {
            defaultWebLocatorComposite = new DefaultWebLocatorSelectionComposite(parent, SWT.NONE);
            defaultWebLocatorComposite.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    handleInputChanged(defaultWebLocatorComposite, null);
                }
            });
        }
        return defaultWebLocatorComposite;
    }

    private Composite createLocatorPriorityTabs(Composite parent) {
        CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
        defaultLocatorOptions.forEach((label, selectorMethod) -> {
            CTabItem tabItem = new CTabItem(tabFolder, SWT.NULL);
            tabItem.setText(label);
            Control tabContent = getTabContentBySelectorMethod(selectorMethod, tabFolder);
            tabItem.setControl(tabContent);
        });
        tabFolder.setSelection(0);

        return tabFolder;
    }

    private Control getTabContentBySelectorMethod(SelectorMethod selectorMethod, Composite parent) {
        switch (selectorMethod) {
            case XPATH:
                return createXPathComposite(parent);
            case BASIC: // Attributes
                return createAttributesComposite(parent);
            default:
                break;
        }
        return null;
    }

    private Composite createXPathComposite(Composite parent) {
        if (XPathComposite == null) {
            XPathComposite = new XPathsSelectionComposite(parent, SWT.NONE);
            XPathComposite.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    handleInputChanged(XPathComposite, null);
                }
            });
        }
        return XPathComposite;
    }

    private Control createAttributesComposite(Composite parent) {
        if (attributesComposite == null) {
            attributesComposite = new AttributesSelectionComposite(parent, SWT.NONE);
            attributesComposite.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    handleInputChanged(attributesComposite, null);
                }
            });
        }
        return attributesComposite;
    }

    @Override
    protected void initialize() throws IOException {
        WebUiExecutionSettingStore store = WebUiExecutionSettingStore.getStore();

        defaultWebLocatorComposite.setInput(store.getCapturedTestObjectSelectorMethod());
        XPathComposite.setInput(store.getCapturedTestObjectXpathLocators());
        attributesComposite.setInput(store.getCapturedTestObjectAttributeLocators());
    }

    @Override
    protected void performDefaults() {
        IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();
        if (!featureService.canUse(KSEFeature.SELF_HEALING)) {
            KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.SELF_HEALING);
            return;
        }

        try {
            WebUiExecutionSettingStore store = WebUiExecutionSettingStore.getStore();

            store.setDefaultCapturedTestObjectSelectorMethods();
            store.setDefaultCapturedTestObjectXpathLocators();
            store.setDefaultCapturedTestObjectAttributeLocators();

            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        super.performDefaults();
    }

    @Override
    protected boolean saveSettings() {
        try {
            WebUiExecutionSettingStore store = WebUiExecutionSettingStore.getStore();

            store.setCapturedTestObjectSelectorMethod(defaultWebLocatorComposite.getInput());
            store.setCapturedTestObjectXpathLocators(XPathComposite.getInput());
            store.setCapturedTestObjectAttributeLocators(attributesComposite.getInput());
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

        WebUiExecutionSettingStore store = WebUiExecutionSettingStore.getStore();
        try {
            SelectorMethod originalSelectorMethod = store.getCapturedTestObjectSelectorMethod();
            boolean isSelectorMethodChanged = !defaultWebLocatorComposite.compareInput(originalSelectorMethod);
            if (isSelectorMethodChanged) return true;

            List<Pair<String, Boolean>> originalXPathLocatorsPriority = store.getCapturedTestObjectXpathLocators();
            boolean isXPathLocatorsChanged = !XPathComposite.compareInput(originalXPathLocatorsPriority);
            if (isXPathLocatorsChanged) return true;

            List<Pair<String, Boolean>> originalSelectedAttributes = store.getCapturedTestObjectAttributeLocators();
            boolean isSelectedAttributesChanged = !attributesComposite.compareInput(originalSelectedAttributes);
            if (isSelectedAttributesChanged) return true;
        } catch (IOException exception) {
            LoggerSingleton.logError(exception);
        }
        return false;
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_WEBLOCATORS;
    }
}
