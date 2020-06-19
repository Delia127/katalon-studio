package com.kms.katalon.composer.windows.nativerecorder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class NativeRecorderDriver {
    private static final String DRIVER_FOLDER = Paths.get("resources", "KatalonNativeRecorder").toString();

    private static final String DRIVER_FILE_NAME = "KatalonNativeRecorder.exe";
    
    private Thread driverThread = null;
    
    private boolean isRunning = false;
    
    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public File getDriverDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WINDOWS_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        Path driverDirectory = null;
        if (bundleFile.isDirectory()) {
            // run by IDE
            driverDirectory = Paths.get(bundleFile.toString(), DRIVER_FOLDER);
        } else {
            // run as product
            driverDirectory = Paths.get(ClassPathResolver.getConfigurationFolder().toString(), DRIVER_FOLDER);
        }
        return new File(driverDirectory.toString());
    }
    
    public String getDriverFilePath() throws IOException {
        return Paths.get(getDriverDirectory().getAbsolutePath(), DRIVER_FILE_NAME).toString();
    }

    public void start() throws IOException {
        start(false);
    }

    public void start(boolean forceNew) throws IOException {
        String driverPath = getDriverFilePath();
        if (forceNew || !isRunning()) {
            if (driverThread != null && !driverThread.isInterrupted()) {
                driverThread.interrupt();
            }
            driverThread = new Thread() {
                public void run() {
                    try {
                        List<String> driverOutputs = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(new String[] { driverPath });
                        String driverLog = driverOutputs.stream().collect(Collectors.joining("\r\n"));
                        LoggerSingleton.logInfo("Driver running log:");
                        LoggerSingleton.logInfo(driverLog);
                    } catch (IOException | InterruptedException exception) {
                        LoggerSingleton.logError(exception);
                    } finally {
                        setRunning(false);
                    }
                };
            };
            driverThread.start();
        }
        setRunning(true);
    }
}
