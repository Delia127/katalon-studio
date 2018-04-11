package com.kms.katalon.composer.update;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.impl.util.PlatformUtil;
import com.kms.katalon.composer.update.models.AppInfo;
import com.kms.katalon.composer.update.models.ExecInfo;
import com.kms.katalon.composer.update.models.LastestVersionInfo;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.execution.preferences.ProxyPreferences;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class UpdateManager {

    private static final String UPDATE_SITE_ARGUMENT = "updateSite";

    private static final String SERVER_UPDATE_URL = "https://s3.amazonaws.com/katalon/update/";

    private static final String LATEST_VERSION_INFO = "latest_version.json";

    private static final String APP_INFO = "app_info.json";

    private static final String EXEC_INFO = "exec_info.json";

    private static UpdateManager instance;

    private UpdateManager() {
    }

    public static UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }

    public File getApplicationDir() throws IOException {
        File installLoc = new File(FileLocator.resolve(Platform.getInstallLocation().getURL()).getFile());
        if (PlatformUtil.isMacOS()) {
            return installLoc.getParentFile().getParentFile().getParentFile();
        }
        return installLoc;
    }

    public File getApplicationExecFile() throws IOException {
        File parent = new File(FileLocator.resolve(Platform.getInstanceLocation().getURL()).getFile()).getParentFile();
        String execFileName = PlatformUtil.isWindows() ? "katalon.exe" : "katalon";
        return new File(parent, execFileName);
    }

    public File getUpdateResourcesDir() throws IOException {
        String configurationDir = FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile();

        return new File(configurationDir, "resources/update");
    }

    public File getLocalLatestVersionInfoFile() throws IOException {
        return new File(getUpdateResourcesDir(), LATEST_VERSION_INFO);
    }

    public LastestVersionInfo getLocalLatestVersion() throws IOException {
        File localLatestVersion = getLocalLatestVersionInfoFile();
        if (localLatestVersion.exists()) {
            return JsonUtil.fromJson(FileUtils.readFileToString(localLatestVersion), LastestVersionInfo.class);
        }

        LastestVersionInfo latestVersionInfo = new LastestVersionInfo();
        latestVersionInfo.setLatestVersion(VersionUtil.getCurrentVersion().getVersion());
        return latestVersionInfo;
    }

    public File saveLocalLatestVersionInfo(LastestVersionInfo latestVersion) throws IOException {
        File localLatestVersionFile = getLocalLatestVersionInfoFile();

        FileUtils.write(localLatestVersionFile, JsonUtil.toJson(latestVersion));
        return localLatestVersionFile;
    }

    public File getVersionUpdateDir(String version) throws IOException {
        return new File(getUpdateResourcesDir(), version);
    }

    private File getUpdaterJarDir() throws IOException {
        return new File(getUpdateResourcesDir(), "updater");
    }

    public File getUpdateJar() throws IOException {
        File parentDir = getUpdaterJarDir();
        String updaterJarpath = parentDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("updater-") && name.endsWith(".jar");
            }
        })[0];
        return new File(parentDir, updaterJarpath);
    }

    public String getUpdateSiteArgument() {
        String[] args = Platform.getApplicationArgs();
        
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        
        parser.accepts(UPDATE_SITE_ARGUMENT).withOptionalArg();
        
        OptionSet options = parser.parse(args);
        if (options.has(UPDATE_SITE_ARGUMENT)) {
            return (String) options.valueOf(UPDATE_SITE_ARGUMENT);
        }
        return StringUtils.EMPTY;
    }

    public String getServerUrl() {
        return StringUtils.defaultIfEmpty(getUpdateSiteArgument(), SERVER_UPDATE_URL);
    }

    private String addLatestSlash(String rawUrl) {
        return rawUrl.endsWith("/") ? rawUrl : rawUrl + "/";
    }

    public String getLatestVersionUrl() {
        return addLatestSlash(getServerUrl()) + LATEST_VERSION_INFO;
    }

    public String getVersionUrl(String versionLocation, String platform) {
        return addLatestSlash(versionLocation) + platform;
    }

    public String getAppInfoVersionUrl(String versionLocation, String platform) {
        return addLatestSlash(getVersionUrl(versionLocation, platform)) + APP_INFO;
    }

    public String getFileInfoVersionUrl(String versionLocation, String platform, String fileName) {
        return addLatestSlash(getVersionUrl(versionLocation, platform)) + fileName;
    }

    public Proxy getProxy() throws URISyntaxException, IOException {
        return ProxyUtil.getProxy(ProxyPreferences.getProxyInformation());
    }

    public AppInfo getCurrentVersionLocalAppInfo() throws IOException {
        return getLocalAppInfo(VersionUtil.getCurrentVersion().getVersion());
    }

    public AppInfo getLocalAppInfo(String version) throws IOException {
        File appInfoOfVersion = getLocalAppInfoFile(version);
        if (appInfoOfVersion.exists()) {
            String appInfoContent = FileUtils.readFileToString(appInfoOfVersion);
            return JsonUtil.fromJson(appInfoContent, AppInfo.class);
        }

        AppInfo appInfo = new AppInfo();
        appInfo.setPlatform(Platform.getOS());
        appInfo.setVersion(version);
        saveAppInfo(appInfo);
        return appInfo;
    }

    public File getLocalExtractDir(String version) throws IOException {
        return new File(getUpdateResourcesDir(), version + "/extract");
    }

    public File getLocalDownloadDir(String version) throws IOException {
        return new File(getUpdateResourcesDir(), version + "/download");
    }

    public File getLocalDownloadFileInfo(String version, String fileName) throws IOException {
        return new File(getLocalDownloadDir(version), fileName);
    }

    private File getLocalAppInfoFile(String version) throws IOException {
        File updateFolderInfoInLocal = new File(getUpdateResourcesDir(), version);
        File appInfoOfVersion = new File(updateFolderInfoInLocal, APP_INFO);
        return appInfoOfVersion;
    }

    public File saveAppInfo(AppInfo appInfo) throws IOException {
        File localAppInfoFile = getLocalAppInfoFile(appInfo.getVersion());
        String appInfoContent = JsonUtil.toJson(appInfo, AppInfo.class, true);
        FileUtils.writeStringToFile(localAppInfoFile, appInfoContent, StandardCharsets.UTF_8);
        return localAppInfoFile;
    }

    public File saveExecInfo(ExecInfo execInfo) throws IOException {
        File execInfoFile = new File(getUpdaterJarDir(), EXEC_INFO);
        String execInfoContent = JsonUtil.toJson(execInfo, ExecInfo.class, true);
        FileUtils.writeStringToFile(execInfoFile, execInfoContent, StandardCharsets.UTF_8);
        return execInfoFile;
    }
}
