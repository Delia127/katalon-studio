package com.kms.katalon.composer.testdata.parts;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.testdata.constants.StringConstants;

public class WarnLargeFileUtil {

    private static boolean isDialogOpened = false;

    private static final int MAX_COLUMN_COUNT = 100;

    public static void showDialog() {
        if (!isDialogOpened) {
            isDialogOpened = true;
            MessageDialog.openWarning(null, StringConstants.WARN,
                    MessageFormat.format(StringConstants.PA_FILE_TOO_LARGE, MAX_COLUMN_COUNT));
            isDialogOpened = false;
        }
    }
}
