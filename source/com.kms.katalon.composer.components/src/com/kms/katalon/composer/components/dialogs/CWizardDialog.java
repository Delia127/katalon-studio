package com.kms.katalon.composer.components.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class CWizardDialog extends WizardDialog {

    /** Default height of dialog is 250 */
    private int height = 250;

    public CWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL | getDefaultOrientation());
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        Button finishBtn = getButton(IDialogConstants.FINISH_ID);
        if (finishBtn != null) {
            // Change Finish button label to OK
            finishBtn.setText(IDialogConstants.OK_LABEL);
        }
    }

    @Override
    protected Point getInitialSize() {
        Point initSize = super.getInitialSize();
        return new Point(initSize.x, getHeight());
    }

	public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
