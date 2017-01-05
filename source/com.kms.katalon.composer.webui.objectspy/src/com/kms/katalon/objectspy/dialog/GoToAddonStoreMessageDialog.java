package com.kms.katalon.objectspy.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;

public class GoToAddonStoreMessageDialog extends MessageDialogWithToggle {

    private static final int NO_BUTTON_INDEX = 1;

    private static final int OK_BUTTON_INDEX = 0;

    public GoToAddonStoreMessageDialog(Shell parentShell, String dialogTitle, String dialogMessage,
            String dialogToogleMessage) {
        super(parentShell, dialogTitle, null, // accept the default window icon
                dialogMessage, MessageDialog.QUESTION_WITH_CANCEL,
                new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL },
                0, dialogToogleMessage, false);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults()
                .numColumns(0) // this is incremented
                // by createButton
                .equalWidth(false)
                .applyTo(composite);

        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).span(2, 1).applyTo(composite);
        composite.setFont(parent.getFont());
        // Add the buttons to the button bar.
        createButtonsForButtonBar(composite);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        Button okButton = getButton(OK_BUTTON_INDEX);
        okButton.setText(getOKButonLabel());
        setCustomButtonLayoutData(okButton, 80);
        Button noButton = getButton(NO_BUTTON_INDEX);
        noButton.setText(getNoButtonLabel());
        setCustomButtonLayoutData(noButton, 100);
    }

    protected String getOKButonLabel() {
        return ObjectspyMessageConstants.LBL_DLG_GO_TO_STORE;
    }

    protected String getNoButtonLabel() {
        return ObjectspyMessageConstants.LBL_DLG_CONTINUE_WITH_OBJECT_SPY;
    }

    protected void setCustomButtonLayoutData(Button button, int width) {
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(width);
        Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        button.setLayoutData(data);
    }
}
