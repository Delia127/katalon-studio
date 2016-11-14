package com.kms.katalon.composer.project.views;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class NewProjectWizardDialog extends WizardDialog {

    public NewProjectWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL | getDefaultOrientation());
        addListeners();
    }

    private void addListeners() {
        addPageChangingListener(new IPageChangingListener() {

            @Override
            public void handlePageChanging(PageChangingEvent e) {
                Shell shell = ((WizardDialog) e.getSource()).getShell();
                if (e.getTargetPage() instanceof ResizableProjectPage) {
                    Point pageSize = ((ResizableProjectPage) e.getTargetPage()).getPageSize();
                    shell.setSize(pageSize);
                    shell.layout(true, true);
                    return;
                }
                shell.pack(true);
            }
        });
    }

}
