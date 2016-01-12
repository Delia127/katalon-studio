package com.kms.katalon.composer.components.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.internal.services.Activator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.constants.StringConstants;

@SuppressWarnings("restriction")
public class MultiStatusErrorDialog {
    public static void showErrorDialog(Exception e, String errorMessage, String reason) {
        List<Status> childStatuses = new ArrayList<Status>();
        String message = (e.getMessage() != null) ? e.getMessage() : "";

        for (String line : message.split(System.getProperty("line.separator"))) {
            childStatuses.add(new Status(IStatus.WARNING, Activator.PLUGIN_ID, line));
        }
        MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.WARNING, childStatuses.toArray(new Status[] {}),
                reason, null);

        ErrorDialog.openError(Display.getDefault().getActiveShell(), StringConstants.WARN_TITLE, errorMessage, ms);
    }
}
