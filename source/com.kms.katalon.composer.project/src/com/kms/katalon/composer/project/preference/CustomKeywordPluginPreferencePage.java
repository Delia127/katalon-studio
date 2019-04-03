package com.kms.katalon.composer.project.preference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.custom.keyword.CustomKeywordSettingPage.SettingPageComponent;
import com.kms.katalon.custom.keyword.KeywordsManifest;

public class CustomKeywordPluginPreferencePage extends PreferencePage {

    private final KeywordsManifest keywordsManifest;

    private Map<String, Pair<SettingPageComponent, Text>> componentCollection = new HashMap<>();

    public CustomKeywordPluginPreferencePage(KeywordsManifest keywordsManifest) {
        this.keywordsManifest = keywordsManifest;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.horizontalSpacing = 15;
        container.setLayout(gridLayout);

        for (SettingPageComponent entry : keywordsManifest.getConfiguration().getSettingPage().getComponents()) {
            String key = entry.getKey();
            String type = entry.getType();
            String label = entry.getLabel();
            if (!"text".equals(type) && !"secret".equals(type)) {
                continue;
            }

            Label lblComponentLabel = new Label(container, SWT.NONE);
            lblComponentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblComponentLabel.setText(label);

            switch (type) {
                case "text": {
                    Text txtComponentText = new Text(container, SWT.BORDER);
                    txtComponentText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

                    componentCollection.put(key, Pair.of(entry, txtComponentText));
                    break;
                }
                case "secret": {
                    Text txtComponentSecret = new Text(container, SWT.BORDER);
                    txtComponentSecret.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                    txtComponentSecret.setEchoChar(GlobalStringConstants.CR_ECO_PASSWORD.charAt(0));
                    componentCollection.put(key, Pair.of(entry, txtComponentSecret));
                }
            }
        }

        setInput();
        return container;
    }

    private BundleSettingStore getSettingStore() {
        return new BundleSettingStore(ProjectController.getInstance().getCurrentProject().getFolderLocation(),
                keywordsManifest.getConfiguration().getSettingId(), true);
    }

    private void setInput() {
        BundleSettingStore settingStore = getSettingStore();
        for (Entry<String, Pair<SettingPageComponent, Text>> componentEntry : componentCollection.entrySet()) {
            try {
                String key = componentEntry.getKey();
                String storedValue = settingStore.getString(key, 
                        StringUtils.defaultString(componentEntry.getValue().getLeft().getDefaultValue()));
                if (StringUtils.isEmpty(storedValue)) {
                    continue;
                }
                componentEntry.getValue().getRight().setText(storedValue);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    @Override
    public boolean performOk() {
        if (!isControlCreated()) {
            return true;
        }

        BundleSettingStore settingStore = getSettingStore();
        for (Entry<String, Pair<SettingPageComponent, Text>> componentEntry : componentCollection.entrySet()) {
            try {
                settingStore.setProperty(componentEntry.getKey(), componentEntry.getValue().getRight().getText());
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }

        return true;
    }

}
