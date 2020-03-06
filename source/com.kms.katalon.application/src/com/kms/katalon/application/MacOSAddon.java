package com.kms.katalon.application;

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

import com.kms.katalon.application.utils.ProcessUtil;
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
            System.out.println("> MacOSAddon > Not detect MacOSX");
            return;
        }
        if (!ProcessUtil.isKRE()) {
            System.out.println("> MacOSAddon > initSmallFonts()");
            initSmallFonts();
        }
        System.out.println("> MacOSAddon > initDefaultJRE()");
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
            System.out.println("> MacOSAddon > Cannot get Preference Store");
            return;
        }

        try {
            String lastConfigurationLocation = prefStore.getString(UPDATED_PREF_VM_XML_KEY);
            System.out.println("> MacOSAddon > Settings - lastConfigurationLocation: " + lastConfigurationLocation);
            String currentConfigurationLocation = getConfigurationFolder().getAbsolutePath();
            System.out.println("> MacOSAddon > Settings - currentConfigurationLocation: " + currentConfigurationLocation);
            if (StringUtils.equals(lastConfigurationLocation, currentConfigurationLocation)) {
                System.out.println("> MacOSAddon > lastConfigurationLocation == currentConfigurationLocation");
                return;
            }
            File jreFolder = getJREFolder();
            System.out.println("> MacOSAddon > getJREFolder(): " + jreFolder.getAbsolutePath());
            if (jreFolder == null || !jreFolder.exists()) {
                System.out.println("> MacOSAddon > Invalid jreFolder");
                return;
            }
            
            System.out.println("> MacOSAddon > createXMLDefinitionForVM(...)");
            String xmlDefinition = createXMLDefinitionForVM(jreFolder);
            
            System.out.println("> MacOSAddon > Set JavaRuntime.PREF_VM_XML -> " + xmlDefinition);
            prefStore.setValue(JavaRuntime.PREF_VM_XML, xmlDefinition);

            System.out.println("> MacOSAddon > Set UPDATED_PREF_VM_XML_KEY -> " + currentConfigurationLocation);
            prefStore.setValue(UPDATED_PREF_VM_XML_KEY, currentConfigurationLocation);

            System.out.println("> MacOSAddon > Save configurations");
            prefStore.save();
        } catch (IOException | CoreException e) {
            System.out.println("> MacOSAddon > initDefaultJRE() Exception: ");
            System.out.println(e);
            LogUtil.logError(e);
        }
    }

    private static String createXMLDefinitionForVM(File jreFolder) throws CoreException {
        IVMInstallType standardVMType = JavaRuntime.getVMInstallType(StandardVMType.ID_STANDARD_VM_TYPE);
        if (standardVMType == null) {
            System.out.println("> MacOSAddon > standardVMType == null");
            return null;
        }
        VMStandin vmStandin = new VMStandin(standardVMType, String.valueOf(System.currentTimeMillis()));
        vmStandin.setInstallLocation(jreFolder);
        vmStandin.setName(MAC_OSX_JRE);
        
        System.out.println("> MacOSAddon > vmStandin.convertToRealVM()");
        IVMInstall defaultJRE = vmStandin.convertToRealVM();
        System.out.println("> MacOSAddon > defaultJRE: " + defaultJRE.getName());
        
        // Create a VM definition container
        VMDefinitionsContainer vmContainer = new VMDefinitionsContainer();
        
        // Set the default VM Id on the container
        System.out.println("> MacOSAddon > vmContainer.setDefaultVMInstallCompositeID(...)");
        System.out.println("> MacOSAddon > JavaRuntime.getCompositeIdFromVM(defaultJRE): " + JavaRuntime.getCompositeIdFromVM(defaultJRE));
        vmContainer.setDefaultVMInstallCompositeID(JavaRuntime.getCompositeIdFromVM(defaultJRE));
        
        // Set the VMs on the container
        System.out.println("> MacOSAddon > vmContainer.addVM(defaultJRE)");
        vmContainer.addVM(defaultJRE);
        
        // Generate XML for the VM defs and save it as the new value of the VM preference
        System.out.println("> MacOSAddon > vmContainer.getAsXML()");
        return vmContainer.getAsXML();
    }

    private static File getJREFolder() throws IOException {
        File jreFolder = new File(getConfigurationFolder().getParentFile(), MAC_JRE_HOME_RELATIVE_PATH);
        System.out.println("> MacOSAddon > jreFolder: " + jreFolder.getAbsolutePath());
        if (!jreFolder.exists()) {
            System.out.println("> MacOSAddon > jreFolder does not exist ");
            jreFolder = new File(System.getProperty("java.home"));
            System.out.println("> MacOSAddon > jreFolder is set to: " + jreFolder.getAbsolutePath());
        } else {
            System.out.println("> MacOSAddon > makeJREFilesExecutable(jreFolder)");
            makeJREFilesExecutable(jreFolder);
        }
        return jreFolder;
    }

    private static File getConfigurationFolder() throws IOException {
        System.out.println("> MacOSAddon > Platform.getConfigurationLocation().getURL(): " + Platform.getConfigurationLocation().getURL());
        File configurationFolder = new File(FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile());
        System.out.println("> MacOSAddon > configurationFolder: " + configurationFolder.getAbsolutePath());
        return configurationFolder;
    }

    private static void makeJREFilesExecutable(File jreFolder) throws IOException {
        for (File file : jreFolder.listFiles()) {
            if (!file.isFile()) {
                if (file.isDirectory()) {
//                    System.out.println("> MacOSAddon > makeJREFilesExecutable > dir: " + file.getAbsolutePath());
                    makeJREFilesExecutable(file);
                }
                continue;
            }
            Set<PosixFilePermission> perms = new HashSet<>();
            for (PosixFilePermission permission : PosixFilePermission.values()) {
                perms.add(permission);
            }
//            System.out.println("> MacOSAddon > permissions: " + perms);
//            System.out.println("> MacOSAddon > makeJREFilesExecutable: " + file.toPath());
            Files.setPosixFilePermissions(file.toPath(), perms);
        }
    }
}
