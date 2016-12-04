package com.kms.katalon.objectspy.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ObjectSpyPreferencePage extends FieldEditorPreferencePage {
    public ObjectSpyPreferencePage() {
        setPreferenceStore(
                PreferenceStoreManager.getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addField(new ComboFieldEditor(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER,
                StringConstants.PREF_LBL_DEFAULT_BROWSER, getDefaultBrowserOptions(), composite));
        addField(new IntegerFieldEditor(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_ACTIVE_BROWSER_PORT,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT, composite));
        addCheckboxField(composite);

        initialize();
        checkState();
        return composite;
    }

    private String[][] getDefaultBrowserOptions() {
        List<String> browsers = new ArrayList<>(
                Arrays.asList(ObjectSpyPreferenceDefaultValueInitializer.SUPPORTED_BROWSERS));
        if (!Platform.OS_WIN32.equals(Platform.getOS())) {
            browsers.remove(WebUIDriverType.IE_DRIVER.toString());
        }
        List<String[]> options = new ArrayList<>();
        browsers.forEach(browser -> options.add(new String[] { browser, browser }));
        return options.toArray(new String[][] {});
    }

    private void addCheckboxField(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        addField(new BooleanFieldEditor(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT_DO_NOT_SHOW_WARNING_DIALOG, composite));
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }
}
