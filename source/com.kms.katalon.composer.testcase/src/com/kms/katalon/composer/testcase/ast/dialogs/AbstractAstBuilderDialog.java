package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractAstBuilderDialog extends Dialog implements IAstDialogBuilder {
    protected static final int DEFAULT_DIALOG_HEIGHT = 500;

    protected static final int DEFAULT_DIALOG_WIDTH = 700;

    public AbstractAstBuilderDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(DEFAULT_DIALOG_WIDTH, DEFAULT_DIALOG_HEIGHT);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button btnOK = createButton(parent, 102, IDialogConstants.OK_LABEL, false);
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processEditingValueWhenOKPressed();
                okPressed();
            }
        });
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    // apply editting value when user click OK. However, not all child dialog take care this action.
    // So, we make the method be empty. As the result, if a child dialog need take care this action,
    // then it will override the method.
    protected void processEditingValueWhenOKPressed() {
    }

    /**
     * Get the dialog title for a specific ast node
     * 
     * @return the dialog title for a specific ast node
     */
    public abstract String getDialogTitle();

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }
}
