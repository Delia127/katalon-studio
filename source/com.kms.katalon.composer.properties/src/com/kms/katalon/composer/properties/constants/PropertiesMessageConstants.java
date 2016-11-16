package com.kms.katalon.composer.properties.constants;

import org.eclipse.osgi.util.NLS;

public class PropertiesMessageConstants extends NLS {

    public static String PART_EXCEPTION_CANNOT_SAVE_THE_PROPERTIES;

    public static String PART_MSG_DO_YOU_WANT_TO_SAVE_THE_CHANGES_IN_PROPERTIES;

    public static String PART_TOOLITEM_DISCARD_CHANGES;

    public static String PART_TOOLITEM_SAVE_CHANGES;

    public static String PART_LBL_TAKEN_DATE;

    public static String PART_LBL_DATA_TYPE;

    static {
        NLS.initializeMessages(PropertiesMessageConstants.class.getName(), PropertiesMessageConstants.class);
    }

    private PropertiesMessageConstants() {
        // do nothing
    }
}
