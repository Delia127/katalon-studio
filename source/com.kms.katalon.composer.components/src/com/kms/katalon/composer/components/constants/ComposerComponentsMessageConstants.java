package com.kms.katalon.composer.components.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerComponentsMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.components.constants.composerComponentsMessages";

    public static String DIA_MESSAGE;

    public static String DIA_DEFAULT_LBL_VAL;

    public static String DIA_OBJ_HEADER_LOC;

    public static String WIZ_NEW_NAME_PAGE_DESCRIPTION_TEMPLATE;

    public static String WIZ_LABEL_NEW_NAME_TEXT;

    public static String WIZ_NEW_NAME_PAGE_TITLE;

    public static String WIZ_NAME_ALREADY_EXISTS;

    public static String WIZ_NAME_ALREADY_EXISTS_IN_DIFFERENT_CASE;

    public static String WIZ_RENAME_WIZARD_TITLE;

    public static String WIZ_UPDATE_REFERENCES_PAGE_DESCRIPTION;

    public static String WIZ_UPDATE_REFERENCES_PAGE_TITLE;

    public static String EDI_MSG_VALIDATOR_REQUIRE_MESSAGE;

    public static String TOOLTIP_HELP_WITH_DOCUMENTATION;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerComponentsMessageConstants.class);
    }

    private ComposerComponentsMessageConstants() {
    }
}
