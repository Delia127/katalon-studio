package com.kms.katalon.composer.update.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.constants.MessageConstants;

public class InstallUpdateConfirmationDialog extends AbstractDialog {

    public InstallUpdateConfirmationDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        Label lblInfo = new Label(container, SWT.NONE);

        lblInfo.setText(MessageConstants.DIA_LBL_UPDATE_ALREADY_TO_INSTALL);
        return container;
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, OK, MessageConstants.DIA_BTN_INSTALL_AND_RELAUNCH, true);
        createButton(parent, CANCEL, MessageConstants.DIA_BTN_NO_THANKS, false);
    }

    @Override
    public String getDialogTitle() {
        return MessageConstants.DIA_TITLE_KS_UPDATE;
    }
    
}
