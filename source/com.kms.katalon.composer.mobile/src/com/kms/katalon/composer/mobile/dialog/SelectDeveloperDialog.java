package com.kms.katalon.composer.mobile.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;

public class SelectDeveloperDialog extends AbstractDialog {

    public SelectDeveloperDialog(Shell parentShell) {
        super(parentShell);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void registerControlModifyListeners() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void setInput() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        return container;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        return parent;
    }
}
