package com.kms.katalon.composer.mobile.objectspy.dialog;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MessageProgressMonitorDialog;
import com.kms.katalon.composer.components.services.UISynchronizeService;

public class AppiumMonitorDialog extends MessageProgressMonitorDialog implements AppiumStreamHandler {

    public AppiumMonitorDialog(Shell parent) {
        super(parent);
    }

    @Override
    public void handleOutput(String line) {
        UISynchronizeService.syncExec(() -> appendDetails(line + "\n"));
    }
}
