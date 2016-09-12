package com.kms.katalon.composer.explorer.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // AdvancedSearchDialog
    public static final String CUS_DIALOG_TITLE = "Advanced " + SEARCH;

    public static final String CUS_LBL_SEARCH = SEARCH;

    public static final String CUS_LBL_CLEAR = CLEAR;

    // SearchDropDownBox
    public static final String CUS_SEARCH_ALL = ALL;

    // DeleteHandler
    public static final String HAND_DELETE_CONFIRM_MSG = "Are you sure you want to delete {0}?";

    public static final String HAND_MULTI_DELETE_CONFIRM_MSG = "Are you sure you want to delete these {0} entities?";

    public static final String HAND_DELETE_TITLE = DELETE;

    // AbstractDeleteEntityDialog
    public static final String DIA_FIELD_SOURCE_ID = "Source ID";

    public static final String DIA_MSG_HEADER_ENTITY_REFERENCES = "Caution: ''{0}'' has been referred by some sources listed below.\nDo you want to remove all these references?";

    // AbstractDeleteReferredEntityHandler
    public static final String HAND_JOB_DELETING_FOLDER = "Deleting folder: ''{0}''...";

    // RenameHandler
    public static final String HAND_CONFIRM_TITLE = "Confirmation";

    public static final String HAND_CONFIRM_MSG_REQUIRE_SAVE_ALL_B4_CONTINUE = "System needs to save all before renaming. Do you want to continue?";

    // ExplorerTreePart
    public static final String PA_SEARCH_TEXT_DEFAULT_VALUE = "Enter text to search...";

    public static final String PA_IMAGE_TIP_ADVANCED_SEARCH = "Advanced " + SEARCH;

    public static final String PA_IMAGE_TIP_SEARCH = SEARCH;

    public static final String PA_IMAGE_TIP_CLOSE_SEARCH = CLEAR;

    // TreeEntityDropListener
    public static final String LIS_ERROR_MSG_CANNOT_MOVE_THE_SELECTION = "Unable to move the selection: {0}";

    public static final String LIS_ERROR_MSG_CANNOT_MOVE_INTO_DIFF_REGION = "Cannot move ''{0}'' type into ''{1}'' region";

    // EntityTooltip
    public static final String TOOLTIP_MESSAGE_PROPERTIES_ENTITY = "Right-click and choose Properties to edit";
}
