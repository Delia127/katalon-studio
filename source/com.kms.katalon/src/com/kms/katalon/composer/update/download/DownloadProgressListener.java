package com.kms.katalon.composer.update.download;

public interface DownloadProgressListener {
    void onProgressUpdate(long progress, long totalSize, long speedInKps);
}
