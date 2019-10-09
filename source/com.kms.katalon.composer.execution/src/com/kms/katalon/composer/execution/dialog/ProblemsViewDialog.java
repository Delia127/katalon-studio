package com.kms.katalon.composer.execution.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.IdConstants;

public class ProblemsViewDialog extends Dialog {

    private Button btnShowProblemsView;

    private Button btnPreceed;

    private Button btnCancel;

    public ProblemsViewDialog(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData bodyGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        body.setLayoutData(bodyGridData);
        GridLayout bodyGridLayout = new GridLayout(2, false);
        bodyGridLayout.marginWidth = 10;
        bodyGridLayout.marginHeight = 30;
        body.setLayout(bodyGridLayout);

        Label lblIcon = new Label(body, SWT.NONE);
        Image logo = ImageManager.getImage(IImageKeys.ERROR_20);
        lblIcon.setImage(logo);

        Label lblInformation = new Label(body, SWT.NONE);
        GridData gdTxtLevel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdTxtLevel.heightHint = 18;
        lblInformation.setLayoutData(gdTxtLevel);
        lblInformation.setText(StringConstants.MSG_PROBLEMS_VIEW);

        return body;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        btnShowProblemsView = createButton(parent, IdConstants.SHOW_PROBLEM_ID, StringConstants.DIA_SHOW_PROBLEM, true);
        btnPreceed = createButton(parent, IDialogConstants.PROCEED_ID, StringConstants.DIA_PROCEED, false);
        btnCancel = createButton(parent, IDialogConstants.CANCEL_ID, StringConstants.DIA_CANCEL, false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.ERROR_TITLE);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

}
