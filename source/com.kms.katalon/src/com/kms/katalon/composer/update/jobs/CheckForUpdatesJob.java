package com.kms.katalon.composer.update.jobs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.impl.util.PlatformUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.update.UpdateComponent;
import com.kms.katalon.composer.update.UpdateException;
import com.kms.katalon.composer.update.UpdateManager;
import com.kms.katalon.composer.update.download.FileDownloader;
import com.kms.katalon.composer.update.models.AppInfo;
import com.kms.katalon.composer.update.models.FileInfo;
import com.kms.katalon.composer.update.models.LastestVersionInfo;
import com.kms.katalon.core.util.internal.JsonUtil;

public class CheckForUpdatesJob extends Job implements UpdateComponent {

    private boolean silenceMode;
    
    public CheckForUpdatesJob(boolean silenceMode) {
        super("Checking for Updates...");
        this.silenceMode = silenceMode;
    }

    private CheckForUpdateResult updateResult;

    public CheckForUpdateResult getUpdateResult() {
        return updateResult;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Checking for Updates...", 100);
        if (VersionUtil.isStagingBuild() || VersionUtil.isDevelopmentBuild()) {
            updateResult = new CheckForUpdateResult();
            updateResult.setUpdateResult(UpdateResultValue.UP_TO_DATE);
            return Status.OK_STATUS;
        }

        UpdateManager updateManager = getUpdateManager();

        LastestVersionInfo lastestUpdateVersion;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FileDownloader downloader = new FileDownloader(FileDownloader.UNKNOWN_SIZE);
            downloader.download(updateManager.getLatestVersionUrl(), outputStream, monitor);

            lastestUpdateVersion = JsonUtil.fromJson(outputStream.toString(), LastestVersionInfo.class);
            LastestVersionInfo localLatestVersion = updateManager.getLocalLatestVersion();
            if (!VersionUtil.isNewer(lastestUpdateVersion.getLatestVersion(),
                    VersionUtil.getCurrentVersion().getVersion())
                    || isIgnoredInSilenceMode(lastestUpdateVersion, localLatestVersion)) {
                updateResult = new CheckForUpdateResult();
                updateResult.setUpdateResult(UpdateResultValue.UP_TO_DATE);
                return Status.OK_STATUS;
            }

            if (!lastestUpdateVersion.isNewMechanism()) {
                updateResult = new CheckForUpdateResult();
                updateResult.setUpdateResult(UpdateResultValue.APPLIED_LEGACY_MECHANISM);
                return Status.OK_STATUS;
            }
        } catch (InterruptedException e) {
            return Status.CANCEL_STATUS;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            updateResult = new CheckForUpdateResult();
            updateResult.setUpdateResult(UpdateResultValue.APPLIED_LEGACY_MECHANISM);
            return Status.OK_STATUS;
        }
        monitor.worked(40);

        try {
            monitor.setTaskName("Fetching new update info...");
            AppInfo latestAppInfo = downloadLastestAppInfo(lastestUpdateVersion, monitor);
            updateManager.saveAppInfo(latestAppInfo);
            monitor.worked(40);

            monitor.setTaskName("Determining update size...");
            AppInfo currentAppInfo = updateManager.getCurrentVersionLocalAppInfo();
            List<FileInfo> newDownloadFiles = determineUpdateFiles(currentAppInfo, latestAppInfo);

            updateResult = new CheckForUpdateResult();
            updateResult.setUpdateResult(UpdateResultValue.NEW_UPDATE_FOUND);
            updateResult.setLatestAppInfo(latestAppInfo);
            updateResult.setCurrentAppInfo(currentAppInfo);
            updateResult.setUpdateFiles(newDownloadFiles);
            updateResult.setLatestVersionInfo(lastestUpdateVersion);

            monitor.worked(20);
            return Status.OK_STATUS;
        } catch (InterruptedException e) {
            return Status.CANCEL_STATUS;
        } catch (UpdateException e) {
            return new Status(Status.ERROR, "com.kms.katalon", "Unable to connect to katalon update server", e);
        } catch (Exception e) {
            return new Status(Status.ERROR, "com.kms.katalon", "Unable to connect to katalon update server",
                    new UpdateException(e));
        } finally {
            monitor.done();
        }

    }

    private boolean isIgnoredInSilenceMode(LastestVersionInfo lastestUpdateVersion,
            LastestVersionInfo localLatestVersion) {
        return silenceMode && isLatestVersionIgnored(lastestUpdateVersion, localLatestVersion);
    }

    private boolean isLatestVersionIgnored(LastestVersionInfo lastestUpdateVersion,
            LastestVersionInfo localLatestVersion) {
        return lastestUpdateVersion.getLatestVersion().equals(localLatestVersion.getLatestVersion())
                && localLatestVersion.isLatestVersionIgnored();
    }

    private AppInfo downloadLastestAppInfo(LastestVersionInfo lastestUpdateVersion, IProgressMonitor monitor)
            throws UpdateException, InterruptedException {
        UpdateManager updateManger = getUpdateManager();

        String latestVersionAppInfoUrl = updateManger
                .getAppInfoVersionUrl(lastestUpdateVersion.getLatestUpdateLocation(), PlatformUtil.getPlatform());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FileDownloader downloader = new FileDownloader(FileDownloader.UNKNOWN_SIZE);
            downloader.download(latestVersionAppInfoUrl, outputStream, monitor);

            return JsonUtil.fromJson(outputStream.toString(), AppInfo.class);
        } catch (IOException e) {
            throw new UpdateException(e);
        }
    }

    public List<FileInfo> determineUpdateFiles(AppInfo currentAppInfo, AppInfo latestAppInfo) {
        Map<String, FileInfo> latestFileMap = latestAppInfo.getFiles()
                .stream()
                .collect(Collectors.toMap(FileInfo::getLocation, f -> f));
        currentAppInfo.getFiles().forEach(f -> {
            String location = f.getLocation();
            if (latestFileMap.containsKey(location) && f.getHash().equals(latestFileMap.get(location).getHash())) {
                latestFileMap.remove(location);
            }
        });
        return new ArrayList<>(latestFileMap.values());
    }

    public class CheckForUpdateResult {
        private LastestVersionInfo latestVersionInfo;

        private AppInfo currentAppInfo;

        private AppInfo latestAppInfo;

        private UpdateResultValue updateResult;

        private List<FileInfo> updateFiles;

        public UpdateResultValue getUpdateResult() {
            return updateResult;
        }

        private void setUpdateResult(UpdateResultValue updateResult) {
            this.updateResult = updateResult;
        }

        public AppInfo getLatestAppInfo() {
            return latestAppInfo;
        }

        private void setLatestAppInfo(AppInfo appInfo) {
            this.latestAppInfo = appInfo;
        }

        public List<FileInfo> getUpdateFiles() {
            return updateFiles;
        }

        private void setUpdateFiles(List<FileInfo> updateFiles) {
            this.updateFiles = updateFiles;
        }

        public AppInfo getCurrentAppInfo() {
            return currentAppInfo;
        }

        private void setCurrentAppInfo(AppInfo currentAppInfo) {
            this.currentAppInfo = currentAppInfo;
        }

        public LastestVersionInfo getLatestVersionInfo() {
            return latestVersionInfo;
        }

        private void setLatestVersionInfo(LastestVersionInfo latestVersion) {
            this.latestVersionInfo = latestVersion;
        }

    }

    public static enum UpdateResultValue {
        UP_TO_DATE, NEW_UPDATE_FOUND, APPLIED_LEGACY_MECHANISM;
    }
}
