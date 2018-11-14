package com.kms.katalon.composer.integration.qtest.activation;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.integration.qtest.activation.constant.QTestActivationMessageConstant;
import com.kms.katalon.constants.GlobalStringConstants;

public class NoLongerSupportFreePackageDialog extends AbstractDialog {

    private Link lnkWarning;

    public NoLongerSupportFreePackageDialog(Shell parentShell) {
        super(parentShell, false);
    }

    @Override
    protected void registerControlModifyListeners() {
        lnkWarning.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }

    @Override
    public void create() {
        super.create();
        getButton(OK).forceFocus();
    }

    @Override
    protected void setInput() {
        getButton(OK).forceFocus();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));

        Label imgWarning = new Label(container, SWT.NONE);
        imgWarning.setImage(parent.getDisplay().getSystemImage(SWT.ICON_WARNING));

        lnkWarning = new Link(container, SWT.WRAP);
        lnkWarning.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        lnkWarning.setText(MessageFormat.format(
                QTestActivationMessageConstant.NoLongerSupportFreePackageDialog_WARN_MSG_NO_LONGER_SUPPORT,
                ApplicationInfo.versionNo()));
        return parent;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, GlobalStringConstants.OK, true);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, super.getInitialSize().y + 50);
    }

    @Override
    public String getDialogTitle() {
        return GlobalStringConstants.WARN;
    }
}
