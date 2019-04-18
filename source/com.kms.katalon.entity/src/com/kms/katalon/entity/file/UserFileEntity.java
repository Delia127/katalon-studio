package com.kms.katalon.entity.file;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class UserFileEntity extends FileEntity {

    private File file;
    
    public UserFileEntity(File file) {
        setFile(file);
    }
    
    @Override
    public String getFileExtension() {
        return "." + FilenameUtils.getExtension(file.getName());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.name = FilenameUtils.getName(file.getName());
    }
    
    @Override
    public String getLocation() {
        if (parentFolder != null) {
            return parentFolder.getLocation() + File.separator + name;
        } else if (project != null) {
            return project.getFolderLocation() + File.separator + name;
        } else {
            return name + getFileExtension();
        }
    }
    
    @Override
    public String getRelativePath() {
        if (parentFolder != null) {
            return parentFolder.getRelativePath() + File.separator + name;
        } else {
            return name;
        }
    }
}
