package com.kms.katalon.console.addons;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.internal.launching.VMDefinitionsContainer;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

@SuppressWarnings("restriction")
public class MacOSAddon {
    private static final String SWT_SMALL_FONT_SYSTEM_PROPERTIES = "org.eclipse.swt.internal.carbon.smallFonts";

    private static final String JRE = "jre";

    private static final String MAC_OSX_JRE = "MacOSX JRE";

    private static final String MAC_JRE_HOME_RELATIVE_PATH = JRE + File.separator + "Contents" + File.separator
            + "Home" + File.separator + JRE;

    private static final String UPDATED_PREF_VM_XML_KEY = "UPDATED_PREF_VM_XML_LOCATION";

    public static void initMacOSConfig() {
        if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
            return;
        }
        initSmallFonts();
        initDefaultJRE();
    }

    /**
     * Handle Eclipse Neon bugs that cannot read JVM argument -Dorg.eclipse.swt.internal.carbon.smallFonts
     */
    private static void initSmallFonts() {
        System.setProperty(SWT_SMALL_FONT_SYSTEM_PROPERTIES, "");
    }

    private static void initDefaultJRE() {
        ScopedPreferenceStore prefStore = getPreferenceStore(LaunchingPlugin.ID_PLUGIN);
        if (prefStore == null) {
            return;
        }

        try {
            String lastConfigurationLocation = prefStore.getString(UPDATED_PREF_VM_XML_KEY);
            String currentConfigurationLocation = getConfigurationFolder().getAbsolutePath();
            if (StringUtils.equals(lastConfigurationLocation, currentConfigurationLocation)) {
                return;
            }
            File jreFolder = getJREFolder();
            if (jreFolder == null || !jreFolder.exists()) {
                return;
            }
            prefStore.setValue(JavaRuntime.PREF_VM_XML, createXMLDefinitionForVM(jreFolder));
            prefStore.setValue(UPDATED_PREF_VM_XML_KEY, currentConfigurationLocation);
            prefStore.save();
        } catch (IOException | CoreException e) {
            LogUtil.logError(e);
        }
    }

    private static String createXMLDefinitionForVM(File jreFolder) throws CoreException {
        IVMInstallType standardVMType = JavaRuntime.getVMInstallType(StandardVMType.ID_STANDARD_VM_TYPE);
        if (standardVMType == null) {
            return null;
        }
        VMStandin vmStandin = new VMStandin(standardVMType, String.valueOf(System.currentTimeMillis()));
        vmStandin.setInstallLocation(jreFolder);
        vmStandin.setName(MAC_OSX_JRE);
        IVMInstall defaultJRE = vmStandin.convertToRealVM();
        // Create a VM definition container
        VMDefinitionsContainer vmContainer = new VMDefinitionsContainer();
        // Set the default VM Id on the container
        vmContainer.setDefaultVMInstallCompositeID(JavaRuntime.getCompositeIdFromVM(defaultJRE));
        // Set the VMs on the container
        vmContainer.addVM(defaultJRE);
        // Generate XML for the VM defs and save it as the new value of the VM preference
        return vmContainer.getAsXML();
    }

    private static File getJREFolder() throws IOException {
        boolean useSystemJRE = false;
        File jreFolder;
        Bundle currentBundle = FrameworkUtil.getBundle(MacOSAddon.class);
        File currentBundleFile = FileLocator.getBundleFile(currentBundle);
        if (currentBundleFile.isDirectory()) { // built by IDE
            File featureProjectDir = new File(currentBundleFile.getParentFile(), "com.kms.katalon.feature");
            jreFolder = new File(featureProjectDir, "resources" + File.separator + "macosx-x64" + File.separator + MAC_JRE_HOME_RELATIVE_PATH);
        } else {
            jreFolder = new File(getConfigurationFolder().getParentFile(), MAC_JRE_HOME_RELATIVE_PATH);
            if (!jreFolder.exists()) {
                jreFolder = new File(System.getProperty("java.home"));
                useSystemJRE = true;
            }
        }
        
        if (jreFolder != null && jreFolder.exists() && !useSystemJRE) {
            makeJREFilesExecutable(jreFolder);
        }
        return jreFolder;
    }

    private static File getConfigurationFolder() throws IOException {
        File configurationFolder = new File(FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile());
        return configurationFolder;
    }

    private static void makeJREFilesExecutable(File jreFolder) throws IOException {
        for (File file : jreFolder.listFiles()) {
            if (!file.isFile()) {
                if (file.isDirectory()) {
                    makeJREFilesExecutable(file);
                }
                continue;
            }
            Set<PosixFilePermission> perms = new HashSet<>();
            for (PosixFilePermission permission : PosixFilePermission.values()) {
                perms.add(permission);
            }
            Files.setPosixFilePermissions(file.toPath(), perms);
        }
    }
}
