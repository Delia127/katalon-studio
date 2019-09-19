package com.kms.katalon.entity.file;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.constants.StringConstants;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class UserFileEntity extends FileEntity {

    private static final long serialVersionUID = 5232182810796545943L;
    
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
    public boolean equals(Object object) {
        if (!(object instanceof UserFileEntity)) {
            return false;
        }
        UserFileEntity that = (UserFileEntity)object;
        
        if (this.getName().equals(that.getName())) {
            return true;
        }
        return false;
        
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
    
    @Override
    
    
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
