package com.kms.katalon.composer.update;

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.launching.JavaRuntime;

import com.kms.katalon.composer.update.models.ExecInfo;

public class UpdaterLauncher implements UpdateComponent {
    private static final String[] CANDIDATES_JAVA_FILES = { "java", "java.exe" };

    private static final String[] CANDIDATE_JAVA_LOCATIONS = { "bin" + File.separatorChar,
            "jre" + File.separatorChar + "bin" + File.separatorChar };

    private String javaExecFile;

    private String updateJarFile;

    private String appDir;

    private String katalonExecFile;

    private String latestVersionDir;

    private String latestVersion;

    private String currentVersion;

    public UpdaterLauncher(String latestVersion, String currentVersion) throws IOException {
        UpdateManager updateManager = getUpdateManager();
        this.javaExecFile = getInstalledJRE();
        this.updateJarFile = updateManager.getUpdateJar().getAbsolutePath();
        this.appDir = updateManager.getApplicationDir().getAbsolutePath();
        this.katalonExecFile = updateManager.getApplicationExecFile().getAbsolutePath();
        this.latestVersionDir = updateManager.getVersionUpdateDir(latestVersion).getAbsolutePath();
        this.latestVersion = latestVersion;
        this.currentVersion = currentVersion;
    }

    public void startUpdaterLauncher() throws IOException, InterruptedException {
        ExecInfo execInfo = new ExecInfo();
        execInfo.setAppDir(this.appDir);
        execInfo.setExecFile(this.katalonExecFile);
        execInfo.setLatestVersionDir(this.latestVersionDir);
        execInfo.setLatestVersion(this.latestVersion);
        execInfo.setCurrentVersion(this.currentVersion);

        getUpdateManager().saveExecInfo(execInfo);

        String[] commands = new String[] { "./java", "-jar", updateJarFile };

        new File(updateJarFile).setReadable(true);
        new File(updateJarFile).setWritable(true);
        new File(updateJarFile).setExecutable(true);
        ProcessBuilder builder = new ProcessBuilder(commands)
                .directory(new File(javaExecFile).getParentFile());
        Process p = builder.start();
        p.waitFor();
    }

    private String getInstalledJRE() {
        File vmInstallLocation = JavaRuntime.getDefaultVMInstall().getInstallLocation();
        for (int i = 0; i < CANDIDATES_JAVA_FILES.length; i++) {
            for (int j = 0; j < CANDIDATE_JAVA_LOCATIONS.length; j++) {
                File javaFile = new File(vmInstallLocation, CANDIDATE_JAVA_LOCATIONS[j] + CANDIDATES_JAVA_FILES[i]);
                if (javaFile.isFile()) {
                    return javaFile.getAbsolutePath();
                }
            }
        }
        return "java";
    }
}
