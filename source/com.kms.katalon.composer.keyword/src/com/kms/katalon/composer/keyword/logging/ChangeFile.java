package com.kms.katalon.composer.keyword.logging;

public class ChangeFile {
    private String relativePath;

    private String hash;

    private FileStatus status;

    public enum FileStatus {
        OVERWRITE, CREATE_DUPLICATE, NEW, SKIP_KEEP_OLD_FILE
    }
    
    public ChangeFile(String relativePath, String hash, FileStatus status) {
        this.relativePath = relativePath;
        this.hash = hash;
        this.status = status;
    }

}