package com.katalon.plugin.smart_xpath.settings;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.katalon.plugin.smart_xpath.settings.composites.AttributesSelectionComposite;
import com.katalon.plugin.smart_xpath.settings.composites.DefaultWebLocatorSelectionComposite;
import com.katalon.plugin.smart_xpath.settings.composites.XPathsSelectionComposite;
import com.kms.katalon.composer.components.impl.control.DragableCTabFolder;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.util.collections.Pair;

public class SelfHealingTestDesignSettingPage extends AbstractSettingPage {

    @SuppressWarnings("serial")
    private final Map<String, SelectorMethod> defaultLocatorOptions = new LinkedHashMap<String, SelectorMethod>() {
        {
            put("XPath", SelectorMethod.XPATH);
            put("Attributes", SelectorMethod.BASIC);
            put("CSS", SelectorMethod.CSS);
            put("Image", SelectorMethod.IMAGE);
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
        DragableCTabFolder tabFolder = new DragableCTabFolder(parent, SWT.BORDER);
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
        Text text = new Text(parent, SWT.BORDER);
        text.setText("This is page " + selectorMethod.name());
        return text;
    }

    private Composite createXPathComposite(Composite parent) {
        if (XPathComposite == null) {
            XPathComposite = new XPathsSelectionComposite(parent, SWT.NONE);
        }
        return XPathComposite;
    }

    private Control createAttributesComposite(Composite parent) {
        if (attributesComposite == null) {
            attributesComposite = new AttributesSelectionComposite(parent, SWT.NONE);
        }
        return attributesComposite;
    }

    @Override
    protected void initialize() throws IOException {
        defaultWebLocatorComposite.setInput(SelectorMethod.XPATH);
        XPathComposite.setInput(WebUiExecutionSettingStore.getStore().getCapturedTestObjectXpathLocators());
        attributesComposite.setInput(WebUiExecutionSettingStore.getStore().getCapturedTestObjectAttributeLocators());
    }

    @Override
    protected void performDefaults() {
        try {
            WebUiExecutionSettingStore store = WebUiExecutionSettingStore.getStore();
            store.setDefaultCapturedTestObjectAttributeLocators();
            store.setDefaultCapturedTestObjectXpathLocators();
            store.setDefaultCapturedTestObjectSelectorMethods();

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

            store.setCapturedTestObjectAttributeLocators(attributesComposite.getInput());
            store.setCapturedTestObjectXpathLocators(XPathComposite.getInput());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
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

        return true;
    }
}
