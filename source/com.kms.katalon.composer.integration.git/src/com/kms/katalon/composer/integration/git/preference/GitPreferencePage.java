package com.kms.katalon.composer.integration.git.preference;

import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.composer.integration.git.constants.GitPreferenceConstants;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

@SuppressWarnings("restriction")
public class GitPreferencePage extends FieldEditorPreferencePageWithHelp {
    private Composite remoteTimeoutParentComposite;

    private IntegerFieldEditor remoteTimeoutFieldEditor;

    private BooleanFieldEditor gitEnableFieldEditor;

    public GitPreferencePage() {
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(GitPreferencePage.class));
    }

    @Override
    protected void createFieldEditors() {
        gitEnableFieldEditor = new BooleanFieldEditor(GitPreferenceConstants.GIT_INTERGRATION_ENABLE,
                GitStringConstants.ENABLE_GIT_CHECK_LABEL, getFieldEditorParent());
        addField(gitEnableFieldEditor);
        remoteTimeoutParentComposite = getFieldEditorParent();
        remoteTimeoutFieldEditor = new IntegerFieldEditor(UIPreferences.REMOTE_CONNECTION_TIMEOUT,
                UIText.RemoteConnectionPreferencePage_TimeoutLabel, remoteTimeoutParentComposite);
        addField(remoteTimeoutFieldEditor);
    }

    @Override
    protected void initialize() {
        super.initialize();
        enableEditors(gitEnableFieldEditor.getBooleanValue());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() == gitEnableFieldEditor) {
            enableEditors(gitEnableFieldEditor.getBooleanValue());
        }
        super.propertyChange(event);
    }

    private void enableEditors(boolean isEnable) {
        remoteTimeoutFieldEditor.setEnabled(isEnable, remoteTimeoutParentComposite);
    }

    @Override
    public boolean performOk() {
        if (remoteTimeoutFieldEditor != null) {
            Activator.getDefault().getPreferenceStore().setValue(UIPreferences.REMOTE_CONNECTION_TIMEOUT,
                    remoteTimeoutFieldEditor.getIntValue());
        }
        return super.performOk();
    }

    @Override
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
        return DocumentationMessageConstants.PREFERENCE_GIT;
    }
}
