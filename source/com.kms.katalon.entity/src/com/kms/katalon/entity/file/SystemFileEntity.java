package com.kms.katalon.entity.file;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.entity.constants.StringConstants;

public class SystemFileEntity extends FileEntity {
    private static final long serialVersionUID = 6098402482835020979L;

    private File file;
    
    public SystemFileEntity(File file) {
        setFile(file);
    }

    @Override
    public String getFileExtension() {
        return "." + FilenameUtils.getExtension(file.getName());
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File newFile) {
        this.file = newFile;
        this.name = FilenameUtils.getName(newFile.getName());
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

    public String getRelativePathForUI() {
        if (parentFolder != null) {
            return parentFolder.getRelativePath() + File.separator + name;
        } else {
            return name;
        }
    }

    public String getIdForDisplay() {
        return getRelativePathForUI().replace(File.separator, StringConstants.ENTITY_ID_SEPARATOR);
    }
}
