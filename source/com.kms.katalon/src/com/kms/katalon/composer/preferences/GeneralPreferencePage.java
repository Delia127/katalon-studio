package com.kms.katalon.composer.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class GeneralPreferencePage extends PreferencePage {
    private Button radioAutoRestorePrevSession;

    private Button radioAutoCleanPrevSession;

    private Button chkCheckNewVersion;

    private Button chkShowHelpAtStartUp;

    private Composite parentComposite;

    @Override
    protected Control createContents(Composite parent) {
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parentComposite = new Composite(parent, SWT.NONE);
        parentComposite.setLayout(layout);

        Group prevSession = new Group(parentComposite, SWT.SHADOW_IN);
        prevSession.setText(StringConstants.PAGE_GRP_ON_NEXT_STARTING_APP);
        prevSession.setLayout(new GridLayout(1, false));
        prevSession.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        radioAutoRestorePrevSession = new Button(prevSession, SWT.RADIO);
        radioAutoRestorePrevSession.setText(StringConstants.PAGE_RADIO_AUTO_RESTORE_PREV_SESSION);

        radioAutoCleanPrevSession = new Button(prevSession, SWT.RADIO);
        radioAutoCleanPrevSession.setText(StringConstants.PAGE_RADIO_AUTO_CLEAN_PREV_SESSION);

        chkShowHelpAtStartUp = new Button(parentComposite, SWT.CHECK);
        chkShowHelpAtStartUp.setText(MessageConstants.PAGE_PREF_SHOW_HELP_AT_START_UP);

        chkCheckNewVersion = new Button(parentComposite, SWT.CHECK);
        chkCheckNewVersion.setText(MessageConstants.PAGE_PREF_AUTO_CHECK_NEW_VERSION_TITLE);
        
        initialize();

        return parentComposite;
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        // Use public workbench preferences
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }

    private void initialize() {
        IPreferenceStore prefStore = getPreferenceStore();
        boolean autoRestore = prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION);
        radioAutoRestorePrevSession.setSelection(autoRestore);
        radioAutoCleanPrevSession.setSelection(!autoRestore);

        boolean setCheckNewVersion = prefStore.contains(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION);
        chkCheckNewVersion.setSelection(
                setCheckNewVersion ? prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION) : true);
        if (!setCheckNewVersion) {
            prefStore.setDefault(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION, true);
        }

        if (!prefStore.contains(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP)) {
            prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
        }
        chkShowHelpAtStartUp.setSelection(prefStore.getBoolean(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP));
    }

    @Override
    protected void performDefaults() {
        if (parentComposite == null)
            return;
        getPreferenceStore().setToDefault(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION);
        getPreferenceStore().setToDefault(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION);
        getPreferenceStore().setToDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP);
        initialize();
        super.performDefaults();
    }

    @Override
    protected void performApply() {
        if (parentComposite == null)
            return;
        getPreferenceStore().setValue(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION,
                radioAutoRestorePrevSession.getSelection());
        getPreferenceStore().setValue(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION,
                chkCheckNewVersion.getSelection());
        getPreferenceStore().setValue(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP,
                chkShowHelpAtStartUp.getSelection());
    }

    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
}
