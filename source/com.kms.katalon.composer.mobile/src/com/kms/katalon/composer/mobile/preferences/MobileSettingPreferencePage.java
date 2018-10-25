package com.kms.katalon.composer.mobile.preferences;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.core.appium.constants.AppiumLogLevel;
import com.kms.katalon.execution.mobile.constants.MobilePreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class MobileSettingPreferencePage extends FieldEditorPreferencePageWithHelp {
    public MobileSettingPreferencePage() {
        super();
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(MobilePreferenceConstants.MOBILE_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = createNewComposite(parent);
        addField(createBrowseForAppiumDirectoryField(createNewComposite(composite)));
        addField(createNewAppiumLogLevelField(createNewComposite(composite)));
        initialize();
        checkState();
        return composite;
    }

    private Composite createNewComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setFont(parent.getFont());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        composite.setLayoutData(gridData);
        return composite;
    }

    private ComboFieldEditor createNewAppiumLogLevelField(Composite composite) {
        return new ComboFieldEditor(MobilePreferenceConstants.MOBILE_APPIUM_LOG_LEVEL,
                StringConstants.PREF_LBL_APPIUM_LOG_LEVEL,
                new String[][] { { StringUtils.capitalize(AppiumLogLevel.INFO), AppiumLogLevel.INFO },
                        { StringUtils.capitalize(AppiumLogLevel.DEBUG), AppiumLogLevel.DEBUG } },
                composite);
    }

    private DirectoryFieldEditor createBrowseForAppiumDirectoryField(Composite composite) {
        return new DirectoryFieldEditor(MobilePreferenceConstants.MOBILE_APPIUM_DIRECTORY,
                StringConstants.PREF_LBL_APPIUM_DIRECTORY, composite);
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }

    @Override
    public boolean hasDocumentation() {
        String os = Platform.getOS();
        return StringUtils.equals(os, Platform.OS_MACOSX) || StringUtils.equals(os, Platform.OS_WIN32);
    }

    @Override
    public String getDocumentationUrl() {
        String os = Platform.getOS();
        if (StringUtils.equals(os, Platform.OS_MACOSX)) {
            return DocumentationMessageConstants.PREFERENCE_MOBILE_MACOS;
        }
        return DocumentationMessageConstants.PREFERENCE_MOBILE_WINDOWS;
    }
}
