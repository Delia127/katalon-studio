package com.kms.katalon.composer.mobile.util;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MissingMobileDriverWarningDialog;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public class MobileUtil {
    public static boolean detectAppiumAndNodeJs(Shell activeShell) {
        try {
            MobileExecutionUtil.detectInstalledAppiumAndNodeJs();
        } catch (MobileSetupException e) {
            MissingMobileDriverWarningDialog.showWarning(activeShell, e.getMessage());
            return false;
        }
        return true;
    }
}
