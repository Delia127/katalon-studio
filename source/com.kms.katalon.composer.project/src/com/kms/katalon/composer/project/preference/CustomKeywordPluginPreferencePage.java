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
import org.eclipse.swt.widgets.Button;
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

    private Map<String, Pair<SettingPageComponent, Control>> componentCollection = new HashMap<>();

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
            if (!"text".equals(type) && !"secret".equals(type) && !"label".equals(type) && !"checkbox".equals(type)) {
                continue;
            }

            switch (type) {
                case "text": {
                    Label lblComponentLabel = new Label(container, SWT.NONE);
                    lblComponentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                    lblComponentLabel.setText(label);

                    Text txtComponentTextEntry = new Text(container, SWT.BORDER);
                    txtComponentTextEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

                    componentCollection.put(key, Pair.of(entry, txtComponentTextEntry));
                    break;
                }
                case "secret": {
                    Label lblComponentLabel = new Label(container, SWT.NONE);
                    lblComponentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                    lblComponentLabel.setText(label);

                    Text txtComponentSecretEntry = new Text(container, SWT.BORDER);
                    txtComponentSecretEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                    txtComponentSecretEntry.setEchoChar(GlobalStringConstants.CR_ECO_PASSWORD.charAt(0));
                    componentCollection.put(key, Pair.of(entry, txtComponentSecretEntry));
                    break;
                }
                case "checkbox": {
                    Button chckComponentCheckboxEntry = new Button(container, SWT.CHECK);
                    chckComponentCheckboxEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
                    chckComponentCheckboxEntry.setText(label);
                    componentCollection.put(key, Pair.of(entry, chckComponentCheckboxEntry));
                    break;
                }
                case "label": {
                    Label lblComponentLabelEntry = new Label(container, SWT.NONE);
                    lblComponentLabelEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
                    lblComponentLabelEntry.setText(label);
                    componentCollection.put(key, Pair.of(entry, lblComponentLabelEntry));
                    break;
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
        for (Entry<String, Pair<SettingPageComponent, Control>> componentEntry : componentCollection.entrySet()) {
            try {
                String key = componentEntry.getKey();
                Control control = componentEntry.getValue().getRight();

                String defaultString = StringUtils.defaultString(componentEntry.getValue().getLeft().getDefaultValue());
                if (control instanceof Text) {
                    String storedValue = settingStore.getString(key, defaultString);
                    if (StringUtils.isEmpty(storedValue)) {
                        continue;
                    }
                    ((Text) control).setText(storedValue);
                }
                if (control instanceof Button) {
                    if ((control.getStyle() & SWT.CHECK) != 0) {
                        Boolean storedValue = settingStore.getBoolean(key,
                                Boolean.parseBoolean(defaultString));
                        ((Button) control).setSelection(storedValue);
                    }
                }
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
        for (Entry<String, Pair<SettingPageComponent, Control>> componentEntry : componentCollection.entrySet()) {
            try {
                Control control = componentEntry.getValue().getRight();
                if (control instanceof Text) {
                    settingStore.setProperty(componentEntry.getKey(), ((Text) control).getText());
                }
                if (control instanceof Button) {
                    if ((control.getStyle() & SWT.CHECK) != 0) {
                        settingStore.setProperty(componentEntry.getKey(), ((Button) control).getSelection());
                    }
                }
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }

        return true;
    }

}
