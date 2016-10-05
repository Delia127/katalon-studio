package com.kms.katalon.composer.project.exception;

import java.text.MessageFormat;

import com.kms.katalon.composer.project.constants.ComposerProjectMessageConstants;

public class MissingProjectSettingPageException extends Exception {

    private static final long serialVersionUID = 5155815888158251277L;

    public MissingProjectSettingPageException(String pageId) {
        super(MessageFormat.format(ComposerProjectMessageConstants.HAND_PROJECT_SETTINGS_PAGE_ID_NOT_FOUND, pageId));
    }

}
