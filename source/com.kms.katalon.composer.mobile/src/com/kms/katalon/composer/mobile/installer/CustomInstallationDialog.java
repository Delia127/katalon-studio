package com.kms.katalon.composer.mobile.installer;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;

public class CustomInstallationDialog extends ProgressMonitorDialogWithThread {

    private static String DEFAULT_TASKNAME = JFaceResources.getString("ProgressMonitorDialog.message"); //$NON-NLS-1$

    private static int BAR_DLUS = 5;
    
    private String successfulMessage = "The installation has been finished!";

    private StyledText txtDetails;

    private Label lblSubtask;

    private String dialogTitle = DEFAULT_TASKNAME;

    private Button btnFinish;
    
    private Label lblStatus;

    public CustomInstallationDialog(Shell parentShell) {
        super(parentShell);
    }
    
    @Override
    protected void cancelPressed() {
        super.cancelPressed();
        decrementNestingDepth();
        getProgressMonitor().done();
        close();
    }

    @Override
    protected void configureShell(final Shell shell) {
        shell.setText(getDialogTitle());
        shell.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW));
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setMessage(DEFAULT_TASKNAME, true);

        createMessageArea(parent);
        createDetailsLog(parent);
        createProgressIndicator(parent);
        createSubTaskLabel(parent);
        createInstallationStatusLabel(parent);

        taskLabel = messageLabel; // Kept for backwards compatibility.

        return parent;
    }

    @Override
    protected Control createMessageArea(Composite parent) {
        Control messageArea = super.createMessageArea(parent);

        FontDescriptor bigDescriptor = FontDescriptor.createFrom(messageLabel.getFont()).setHeight(16);
        Font bigFont = bigDescriptor.createFont(messageLabel.getDisplay());
        messageLabel.setFont(bigFont);

        return messageArea;
    }

    private void createDetailsLog(Composite parent) {
        txtDetails = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP);
        GridData gdText = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdText.heightHint = 200;
        gdText.horizontalSpan = 2;
        txtDetails.setLayoutData(gdText);
    }

    private void createProgressIndicator(Composite parent) {
        lblSubtask = new Label(parent, SWT.NONE);
        GridData gdSubtask = new GridData(SWT.FILL, SWT.FILL, true, false);
        gdSubtask.horizontalSpan = 2;
        lblSubtask.setLayoutData(gdSubtask);

        progressIndicator = new ProgressIndicator(parent);
        GridData gdProgress = new GridData(SWT.FILL, SWT.TOP, true, false);
        gdProgress.heightHint = convertVerticalDLUsToPixels(BAR_DLUS);
        gdProgress.horizontalSpan = 2;
        progressIndicator.setLayoutData(gdProgress);
    }

    private void createSubTaskLabel(Composite parent) {
        subTaskLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        subTaskLabel.setLayoutData(gd);
        subTaskLabel.setFont(parent.getFont());
    }
    
    private void createInstallationStatusLabel(Composite parent) {
        lblStatus = new Label(parent, SWT.LEFT | SWT.WRAP);
        GridData gdStatus = new GridData(GridData.FILL_HORIZONTAL);
        gdStatus.horizontalSpan = 2;
        lblStatus.setLayoutData(gdStatus);
        FontDescriptor bigDescriptor = FontDescriptor.createFrom(parent.getFont()).setHeight(13);
        Font bigFont = bigDescriptor.createFont(messageLabel.getDisplay());
        lblStatus.setFont(bigFont);
        lblStatus.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
    }

    private void setMessage(String messageString, boolean force) {
        message = messageString == null ? "" : messageString;
        if (messageLabel == null || messageLabel.isDisposed()) {
            return;
        }
        if (force || messageLabel.isVisible()) {
            messageLabel.setToolTipText(message);
            messageLabel.setText(shortenText(message, messageLabel));
        }
    }

    public void setDialogTitle(String title) {
        this.dialogTitle = title;
        if (getShell() != null) {
            getShell().setText(title);
        }
    }

    private String getDialogTitle() {
        return dialogTitle;
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

        btnFinish = createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.FINISH_LABEL, true);
        if (arrowCursor == null) {
            arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
        }
        btnFinish.setCursor(arrowCursor);
        btnFinish.setEnabled(false);
        btnFinish.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                close();
            }
        });
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    @Override
    protected void finishedRun() {
        decrementNestingDepth();
        updateButtonStatesWhenFinished();
        lblStatus.setText(getSuccessfulMessage());
    }

    private void updateButtonStatesWhenFinished() {
        setCancelable(false);
        btnFinish.setEnabled(true);
        getButton(IDialogConstants.FINISH_ID).forceFocus();
    }

    public String getSuccessfulMessage() {
        return successfulMessage;
    }

    public void setSuccessfulMessage(String successfulMessage) {
        this.successfulMessage = successfulMessage;
    }
}
