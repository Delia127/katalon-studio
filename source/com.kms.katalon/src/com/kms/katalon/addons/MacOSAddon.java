package com.kms.katalon.addons;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;
import static java.text.MessageFormat.format;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.logging.LogManager;
import com.kms.katalon.logging.LogMode;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class MacOSAddon {

    private static final String ORG_ECLIPSE_JDT_LAUNCHING_PLUGIN_ID = "org.eclipse.jdt.launching";

    private static final String PREF_VM_XML_KEY = "org.eclipse.jdt.launching.PREF_VM_XML";

    private static final String UPDATED_PREF_VM_XML_KEY = "UPDATED_PREF_VM_XML";

    private static final String PREF_VM_XML_VALUE_TPL = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<vmSettings defaultVM=\"57,org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType13,{0}\">\n<vmType id=\"org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType\">\n<vm id=\"{0}\" name=\"{1}\" path=\"{2}\"/>\n</vmType>\n</vmSettings>\n";

    @PostConstruct
    public void init(IEclipseContext eclipseContext) {
        if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
            return;
        }

        useJavaHomeAsDefaultInstalledJRE();
    }

    private void useJavaHomeAsDefaultInstalledJRE() {
        ScopedPreferenceStore prefStore = getPreferenceStore(ORG_ECLIPSE_JDT_LAUNCHING_PLUGIN_ID);
        if (prefStore.getBoolean(UPDATED_PREF_VM_XML_KEY)) {
            return;
        }

        String prefVmXmlValue = format(PREF_VM_XML_VALUE_TPL, System.currentTimeMillis(),
                System.getProperty("java.version"), System.getProperty("java.home"));
        prefStore.setValue(PREF_VM_XML_KEY, prefVmXmlValue);
        prefStore.setValue(UPDATED_PREF_VM_XML_KEY, true);
        try {
            prefStore.save();
        } catch (IOException e) {
            LogUtil.println(LogManager.getOutputLogger(), e.getMessage(), LogMode.LOG);
        }
    }
}
