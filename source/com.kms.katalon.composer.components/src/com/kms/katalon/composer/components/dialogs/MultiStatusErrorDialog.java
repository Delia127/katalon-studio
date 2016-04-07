package com.kms.katalon.composer.components.dialogs;

import static org.eclipse.core.runtime.IStatus.WARNING;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.internal.services.Activator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.constants.StringConstants;

@SuppressWarnings("restriction")
public class MultiStatusErrorDialog {
    private static final int DEFAULT_REASON_WIDTH = 80;

    public static void showErrorDialog(Exception e, String errorMessage, String reason) {
        List<Status> childStatuses = new ArrayList<Status>();

        for (String line : StringUtils.defaultString(e.getMessage()).split(System.getProperty("line.separator"))) {
            childStatuses.add(new Status(WARNING, Activator.PLUGIN_ID, line));
        }

        MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, WARNING,
                childStatuses.toArray(new Status[childStatuses.size()]), StringUtils.abbreviate(reason,
                        DEFAULT_REASON_WIDTH), null);

        ErrorDialog.openError(Display.getDefault().getActiveShell(), StringConstants.WARN_TITLE, errorMessage, ms);
    }
}
