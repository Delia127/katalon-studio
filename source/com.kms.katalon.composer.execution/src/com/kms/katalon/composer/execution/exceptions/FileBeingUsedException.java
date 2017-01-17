package com.kms.katalon.composer.execution.exceptions;

import java.io.File;
import java.text.MessageFormat;

import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class FileBeingUsedException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -4089427526773268218L;

    public FileBeingUsedException(File file, ProjectEntity currentProject) {
        super(MessageFormat.format(ComposerExecutionMessageConstants.ERR_MSG_LIB_FILE_BEING_USED, file.getName(),
                currentProject.getLocation() + File.separator));
    }
}
