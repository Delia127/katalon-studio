package com.kms.katalon.composer.execution.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;

public class ProblemsViewDialog extends Dialog {

    public static final int SHOW_PROBLEM_ID = 1;
    public static final int PROCEED_ID = 2;
    public static final int CANCEL_ID = 3;
    private boolean isOpen = false;
    
    private Button btnShowProblemsView;
    private Button btnPreceed;
    private Button btnCancel;
    
    public ProblemsViewDialog(Shell parentShell) {
        super(parentShell);
        // TODO Auto-generated constructor stub
    }

    protected Control createDialogArea(Composite parent){
        Composite body = new Composite(parent, SWT.NONE);
        GridData bodyGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        bodyGridData.widthHint = 400;
        body.setLayoutData(bodyGridData);
        GridLayout bodyGridLayout = new GridLayout(1, false);
        bodyGridLayout.marginWidth = 10;
        bodyGridLayout.marginHeight = 10;
        body.setLayout(bodyGridLayout);
        
        CLabel lblInformation = new CLabel(body, SWT.WRAP);
        lblInformation.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
        lblInformation.setBottomMargin(2);
        lblInformation.setText("There are errors in the script. Please fix in your project before running.");
        
        return body;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        btnShowProblemsView = createButton(parent, SHOW_PROBLEM_ID, "Show Prolems in View", true);
        btnPreceed = createButton(parent, PROCEED_ID, "Proceed", false);
        btnCancel = createButton(parent, CANCEL_ID, StringConstants.BTN_CANCEL, false);
    }
    
    @Override
    protected void buttonPressed(int buttonId) {
        // TODO Auto-generated method stub
        setReturnCode(buttonId);
        close();
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.ERROR_TITLE);
    }

}
