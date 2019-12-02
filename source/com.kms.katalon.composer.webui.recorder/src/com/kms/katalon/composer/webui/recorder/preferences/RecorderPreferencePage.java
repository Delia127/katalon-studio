package com.kms.katalon.composer.webui.recorder.preferences;

import static com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_BROWSER;
import static com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants.WEBUI_RECORDER_PIN_WINDOW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webui.recorder.constants.ComposerWebuiRecorderMessageConstants;
import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.UtilitiesAddonPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class RecorderPreferencePage extends FieldEditorPreferencePageWithHelp {

    private Combo cbbDefaultBrowser;

    private Composite mainComposite;

    private Button chckPinRecorder;

    private static final int MIN_PORT_NUMBER = 1;

    private static final int MAX_PORT_NUMBER = 65534;

    public RecorderPreferencePage() {
        setPreferenceStore(
                PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER));
        setPreferenceStore(
                PreferenceStoreManager.getPreferenceStore(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        mainComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 15;
        mainComposite.setLayout(layout);
        mainComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        chckPinRecorder = new Button(mainComposite, SWT.CHECK);
        chckPinRecorder.setText(ComposerWebuiRecorderMessageConstants.PREF_LBL_PIN_RECORDER_WINDOW);
        GridData ldPinRecorder = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        ldPinRecorder.horizontalSpan = 2;
        chckPinRecorder.setLayoutData(ldPinRecorder);

        Label lblDefaultBrowser = new Label(mainComposite, SWT.NONE);
        lblDefaultBrowser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblDefaultBrowser.setText(ComposerWebuiRecorderMessageConstants.PREF_LBL_DEFAULT_BROWSER);

        cbbDefaultBrowser = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        cbbDefaultBrowser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        cbbDefaultBrowser.setText(StringConstants.PREF_LBL_DEFAULT_BROWSER);

        setInput();

        IntegerFieldEditor portEditor = new IntegerFieldEditor(
                UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_PORT,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT, mainComposite);
        portEditor.setValidRange(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
        addField(portEditor);
        addCheckboxField(mainComposite);

        initialize();
        checkState();

        return mainComposite;
    }

    private void setInput() {
        IPreferenceStore preferenceStore = getPreferenceStore();

        List<String> browsers = getDefaultBrowserOptions();
        String selectedBrowser = preferenceStore.getString(WEBUI_RECORDER_DEFAULT_BROWSER);
        int index = browsers.indexOf(selectedBrowser);
        cbbDefaultBrowser.setItems(browsers.toArray(new String[browsers.size()]));
        cbbDefaultBrowser.select(Math.max(0, index));

        chckPinRecorder.setSelection(preferenceStore.getBoolean(WEBUI_RECORDER_PIN_WINDOW));
    }

    private List<String> getDefaultBrowserOptions() {
        List<String> browsers = new ArrayList<>(
                Arrays.asList(RecorderPreferenceDefaultValueInitializer.SUPPORTED_BROWSERS));
        if (!Platform.OS_WIN32.equals(Platform.getOS())) {
            browsers.remove(WebUIDriverType.IE_DRIVER.toString());
        }
        return browsers;
    }

    @Override
    public boolean performOk() {
        if (mainComposite == null || mainComposite.isDisposed()) {
            return true;
        }
        ScopedPreferenceStore store = (ScopedPreferenceStore) getPreferenceStore();
        store.setValue(WEBUI_RECORDER_DEFAULT_BROWSER, cbbDefaultBrowser.getText());
        store.setValue(WEBUI_RECORDER_PIN_WINDOW, chckPinRecorder.getSelection());
        try {
            store.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return super.performOk();
    }

    private void addCheckboxField(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        addField(new BooleanFieldEditor(
                UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT_DO_NOT_SHOW_WARNING_DIALOG, composite));
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.PREFERENCE_UTILITY_ADDON;
    }

    @Override
    protected void createFieldEditors() {

    }
}