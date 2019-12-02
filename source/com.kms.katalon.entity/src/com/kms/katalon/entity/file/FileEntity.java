package com.kms.katalon.entity.file;

import java.io.File;

import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.constants.StringConstants;

/**
 * 
 * Entity that has been save on file on local machine.
 *
 */
public abstract class FileEntity extends Entity implements IFileEntity {
    private static final long serialVersionUID = -4689867707864707962L;

    @Override
    public String getId() {
        return getLocation();
    }

    @Override
    public String getLocation() {
        if (parentFolder != null) {
            return parentFolder.getLocation() + File.separator + name + getFileExtension();
        } else if (project != null) {
            return project.getFolderLocation() + File.separator + name + getFileExtension();
        } else {
            return name + getFileExtension();
        }
    }
    
    public String getLocationProjectFolder(){
    	return project.getFolderLocation();
    }

    @Override
    public String getRelativePath() {
        if (parentFolder != null) {
            return parentFolder.getRelativePath() + File.separator + name + getFileExtension();
        } else {
            return name + getFileExtension();
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

    public File toFile() {
        return new File(getId());
    }
}
