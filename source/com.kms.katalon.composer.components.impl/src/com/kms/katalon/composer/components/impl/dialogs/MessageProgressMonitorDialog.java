package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MessageProgressMonitorDialog extends ProgressMonitorDialogWithThread {

    /**
     * Name to use for task when normal task name is empty string.
     */
    private static String DEFAULT_TASKNAME = JFaceResources.getString("ProgressMonitorDialog.message"); //$NON-NLS-1$

    private static int BAR_DLUS = 9;

    private StyledText txtDetails;

    public MessageProgressMonitorDialog(Shell parent) {
        super(parent);
    }
    
    private void setMessage(String messageString, boolean force) {
        // must not set null text in a label
        message = messageString == null ? "" : messageString; //$NON-NLS-1$
        if (messageLabel == null || messageLabel.isDisposed()) {
            return;
        }
        if (force || messageLabel.isVisible()) {
            messageLabel.setToolTipText(message);
            messageLabel.setText(shortenText(message, messageLabel));
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setMessage(DEFAULT_TASKNAME, false);

        createMessageArea(parent);
        // Only set for backwards compatibility
        taskLabel = messageLabel;
        // progress indicator
        progressIndicator = new ProgressIndicator(parent);
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        gd.heightHint = convertVerticalDLUsToPixels(BAR_DLUS);
        gd.horizontalSpan = 2;
        progressIndicator.setLayoutData(gd);

        txtDetails = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL| SWT.READ_ONLY | SWT.WRAP);
        GridData gdText = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdText.heightHint = 100;
        gdText.horizontalSpan = 2;
        txtDetails.setLayoutData(gdText);
        return parent;
    }
    
    public void setDetails(String text) {
        txtDetails.setText(text);
    }

    public void appendDetails(String text) {
        if (txtDetails.isDisposed()) {
            return;
        }
        txtDetails.append(text);
        txtDetails.setTopIndex(txtDetails.getLineCount() - 1);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(CANCEL).forceFocus();
    }
    
    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

}
