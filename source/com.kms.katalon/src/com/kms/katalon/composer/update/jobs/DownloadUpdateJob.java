package com.kms.katalon.composer.update.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.kms.katalon.composer.components.impl.util.PlatformUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.update.UpdateComponent;
import com.kms.katalon.composer.update.UpdateException;
import com.kms.katalon.composer.update.UpdateManager;
import com.kms.katalon.composer.update.download.DownloadProgressListener;
import com.kms.katalon.composer.update.download.FileDownloader;
import com.kms.katalon.composer.update.jobs.CheckForUpdatesJob.CheckForUpdateResult;
import com.kms.katalon.composer.update.models.AppInfo;
import com.kms.katalon.composer.update.models.FileInfo;
import com.kms.katalon.composer.update.util.ExtractUtils;

public class DownloadUpdateJob extends Job implements UpdateComponent {
    private CheckForUpdateResult newUpdateResult;

    public DownloadUpdateJob(CheckForUpdateResult newUpdateResult) {
        super("Downloading Update...");

        this.newUpdateResult = newUpdateResult;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            SubMonitor subMonitor = SubMonitor.convert(monitor);
            subMonitor.beginTask("", 100);

            SubMonitor downloadMonitor = subMonitor.split(80, SubMonitor.SUPPRESS_NONE);
            downloadMonitor.beginTask("Downloading Update Files...", 100);

            // Clean download directory
            File downloadDir = getUpdateManager().getLocalDownloadDir(getLatestVersion());
            downloadDir.mkdir();
            FileUtils.cleanDirectory(downloadDir);

            // Start downloading update files...
            long totalSize = newUpdateResult.getUpdateFiles()
                    .stream()
                    .mapToLong(f -> f.getSize())
                    .reduce((a, b) -> (a + b))
                    .getAsLong();
            for (FileInfo fileInfo : newUpdateResult.getUpdateFiles()) {
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                int subWork = Math.round(((float) fileInfo.getSize() / totalSize) * 100);
                SubMonitor downloadFileMonitor = downloadMonitor.newChild(subWork, SubMonitor.SUPPRESS_BEGINTASK);
                downloadFileMonitor.beginTask("", 100);
                try {
                    downloadFile(fileInfo, new DownloadProgressListenerImpl(downloadFileMonitor, fileInfo));
                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                }

                downloadFileMonitor.done();
            }
            downloadMonitor.done();

            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            SubMonitor extractMonitor = subMonitor.split(20, SubMonitor.SUPPRESS_SETTASKNAME);
            extractMonitor.beginTask("Extracting Update Files...", 100);

            // Clean extract directory
            File extractDir = getUpdateManager().getLocalExtractDir(getLatestVersion());
            extractDir.mkdir();
            FileUtils.cleanDirectory(extractDir);

            // Start extracting update files...
            for (FileInfo fileInfo : newUpdateResult.getUpdateFiles()) {
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                File downloadedFileInfo = new File(downloadDir, fileInfo.getLocation());
                ExtractUtils.extract(downloadedFileInfo, extractDir);

                int subWork = Math.round(((float) fileInfo.getSize() / totalSize) * 100);
                extractMonitor.worked(subWork);
            }

            // Clean downloadDir
            FileUtils.cleanDirectory(downloadDir);

            replaceUpdater(extractDir);

            return Status.OK_STATUS;
        } catch (IOException e) {
            return new Status(Status.ERROR, "com.kms.katalon", "Unable to download update file from Katalon server",
                    new UpdateException(e));
        } catch (UpdateException e) {
            return new Status(Status.ERROR, "com.kms.katalon", "Unable to download update file from Katalon server", e);
        } finally {
            monitor.done();
        }
    }

    private void replaceUpdater(File extractDir) {
        try {
            File newUpdaterDir = new File(extractDir, "resources/update/updater");
            File oldUpdaterDir = getUpdateManager().getUpdateJar().getParentFile();
            if (newUpdaterDir.exists() && newUpdaterDir.isDirectory()) {
                FileUtils.copyDirectory(newUpdaterDir, oldUpdaterDir);
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private String getLatestVersion() {
        return newUpdateResult.getLatestAppInfo().getVersion();
    }

    private void downloadFile(FileInfo fileInfo, DownloadProgressListenerImpl downloadProgressListener)
            throws UpdateException, InterruptedException {
        UpdateManager updateManager = getUpdateManager();
        File localFileInfo;
        String fileInfoUrl;
        try {
            AppInfo latestAppInfo = newUpdateResult.getLatestAppInfo();
            localFileInfo = updateManager.getLocalDownloadFileInfo(latestAppInfo.getVersion(), fileInfo.getLocation());
            fileInfoUrl = updateManager.getFileInfoVersionUrl(
                    newUpdateResult.getLatestVersionInfo().getLatestUpdateLocation(), PlatformUtil.getPlatform(),
                    fileInfo.getLocation());
        } catch (IOException e) {
            throw new UpdateException(e);
        }

        localFileInfo.getParentFile().mkdirs();
        try (FileOutputStream outputStream = new FileOutputStream(localFileInfo)) {
            FileDownloader fileDownloader = new FileDownloader(fileInfo.getSize());
            fileDownloader.addListener(downloadProgressListener);

            fileDownloader.download(fileInfoUrl, outputStream, downloadProgressListener.getMonitor());

        } catch (IOException e) {
            throw new UpdateException(e);
        }
    }

    private class DownloadProgressListenerImpl implements DownloadProgressListener {

        private SubMonitor monitor;

        private int currentWork = 0;

        private FileInfo fileInfo;

        public SubMonitor getMonitor() {
            return monitor;
        }

        public DownloadProgressListenerImpl(SubMonitor monitor, FileInfo fileInfo) {
            this.monitor = monitor;
            this.fileInfo = fileInfo;
        }

        @Override
        public void onProgressUpdate(long progress, long fileSize, long speedInKps) {
            int work = Math.round(((float) (progress) / fileSize) * 100);
            int nextWork = work - currentWork;
            if (nextWork > 0) {
                monitor.worked(nextWork);
                String subTaskName = MessageFormat.format("Downloading {0}...({1})", fileInfo.getLocation(),
                        FileUtils.byteCountToDisplaySize(speedInKps) + "/s");
                monitor.subTask(subTaskName);
            }
            currentWork = work;
        }

    }
}
